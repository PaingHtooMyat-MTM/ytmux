import { createStore } from 'vuex'
import axios from 'axios'

export default createStore({
  state: {
    volume: 0.5,
    showSidebar: true,
    showSearchModal: false,
    tracks: [],
    currentTrackIndex: null,
    isPlaying: false,
    currentTime: 0,
    repeat: false,
    shuffle: false,
    history: [], // track indices played in shuffle mode
    download: {
      url: '',
      folder: 'C:/Users/Paing Htoo Myat/Downloads/Music',
      downloading: false,
      downloadedTrack: null,
      error: null,
    },
  },
  getters: {
    currentTrack(state) {
      if (state.currentTrackIndex === null) return null
      return state.tracks[state.currentTrackIndex] || null
    },
    progressPercent(state) {
      const track = state.tracks[state.currentTrackIndex]
      if (!track || !track.duration) return 0
      return (state.currentTime / track.duration) * 100
    },
  },
  mutations: {
    setShowSidebar(state, val) {
      state.showSidebar = val
    },
    setShowSearchModal(state, val) {
      state.showSearchModal = val
    },
    setTracks(state, tracks) {
      state.tracks = tracks
    },
    setCurrentTrackIndex(state, index) {
      state.currentTrackIndex = index
      state.currentTime = 0
    },
    setIsPlaying(state, val) {
      state.isPlaying = val
    },
    setCurrentTime(state, time) {
      state.currentTime = time
    },
    setDownloadUrl(state, url) {
      state.download.url = url
    },
    setDownloadFolder(state, folder) {
      state.download.folder = folder
    },
    setDownloading(state, val) {
      state.download.downloading = val
    },
    setDownloadedTrack(state, track) {
      state.download.downloadedTrack = track
    },
    setDownloadError(state, error) {
      state.download.error = error
    },
    setVolume(state, value) {
      state.volume = value
    },
    setCurrentTrackById(state, trackId) {
      const index = state.tracks.findIndex((t) => t.id === trackId)
      if (index !== -1) {
        state.currentTrackIndex = index
        state.currentTime = 0
      }
    },
    togglePlayPause(state) {
      state.isPlaying = !state.isPlaying
    },
    toggleRepeat(state) {
      state.repeat = !state.repeat
    },
    toggleShuffle(state) {
      state.shuffle = !state.shuffle
      if (state.shuffle) {
        state.history = []
        if (state.currentTrackIndex !== null) {
          state.history.push(state.currentTrackIndex)
        }
      }
    },

    nextTrack(state) {
      if (state.tracks.length === 0) return

      if (state.repeat) {
        // Repeat current track — do nothing to currentTrackIndex
      } else if (state.shuffle) {
        const unplayedIndices = state.tracks
          .map((_, idx) => idx)
          .filter((idx) => !state.history.includes(idx))

        if (unplayedIndices.length === 0) {
          // All played — reset history and pick random track
          state.history = []
          state.currentTrackIndex = Math.floor(Math.random() * state.tracks.length)
          state.history.push(state.currentTrackIndex)
        } else {
          const nextIndex = unplayedIndices[Math.floor(Math.random() * unplayedIndices.length)]
          state.currentTrackIndex = nextIndex
          state.history.push(nextIndex)
        }
      } else {
        // Normal next
        state.currentTrackIndex = (state.currentTrackIndex + 1) % state.tracks.length
      }

      state.currentTime = 0
    },

    prevTrack(state) {
      if (state.tracks.length === 0) return

      if (state.repeat) {
        // Repeat current track — do nothing
      } else if (state.shuffle) {
        // Simplified prev in shuffle — pick random unplayed or reset
        const unplayedIndices = state.tracks
          .map((_, idx) => idx)
          .filter((idx) => !state.history.includes(idx))

        if (unplayedIndices.length === 0) {
          state.history = []
          state.currentTrackIndex = Math.floor(Math.random() * state.tracks.length)
          state.history.push(state.currentTrackIndex)
        } else {
          const prevIndex = unplayedIndices[Math.floor(Math.random() * unplayedIndices.length)]
          state.currentTrackIndex = prevIndex
          state.history.push(prevIndex)
        }
      } else {
        state.currentTrackIndex =
          (state.currentTrackIndex - 1 + state.tracks.length) % state.tracks.length
      }

      state.currentTime = 0
    },
    updateTrack(state, updatedTrack) {
      const index = state.tracks.findIndex((t) => t.path === updatedTrack.path)
      if (index !== -1) {
        Object.assign(state.tracks[index], {
          title: updatedTrack.title,
          artist: updatedTrack.artist,
          album: updatedTrack.album,
        })
      }
    },
  },
  actions: {
    async fetchTracks({ commit }) {
      try {
        const res = await axios.get('http://localhost:8080/api/tracks')
        const data = res.data.map((t, i) => ({
          id: String(i + 1),
          title: t.title,
          artist: t.artist,
          album: t.album,
          duration: t.duration,
          filePath: t.filePath,
          playUrl: `http://localhost:8080/api/play?path=${encodeURIComponent(t.filePath)}`,
          thumbnail: t.thumbnail || null,
        }))
        commit('setTracks', data)
      } catch (error) {
        console.error('Error fetching tracks:', error)
      }
    },

    async downloadTrack({ commit, state }) {
      commit('setDownloadedTrack', null)
      commit('setDownloadError', null)
      commit('setDownloading', true)

      try {
        const response = await axios.post('http://localhost:8080/api/download', {
          url: state.download.url,
          folder: state.download.folder,
        })
        commit('setDownloadedTrack', response.data)
      } catch (error) {
        commit('setDownloadError', error?.response?.data?.error || 'Download failed')
      } finally {
        commit('setDownloading', false)
      }
    },

    async updateTrackMetadata({ commit }, updatedTrack) {
      try {
        await fetch('http://localhost:8080/api/tracks/edit', {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            path: updatedTrack.filePath,
            title: updatedTrack.title,
            artist: updatedTrack.artist,
            album: updatedTrack.album,
          }),
        })
        commit('updateTrack', updatedTrack)
      } catch (err) {
        console.error('Failed to update metadata', err)
      }
    },
  },
})
