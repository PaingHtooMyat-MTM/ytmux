import { createStore } from 'vuex'
import axios from 'axios'

export default createStore({
  state: {
    volume: 1,
    showSidebar: true,
    showSearchModal: false,
    playlists: [],
    playlistTracks: [],
    selectedPlaylist: null,
    tracks: [],
    currentTrackIndex: null,
    isPlaying: false,
    currentTime: 0,
    repeat: false,
    shuffle: false,
    history: [],
    download: {
      url: '',
      folder: 'C:/Users/Paing Htoo Myat/Downloads/Music',
      downloading: false,
      downloadedTrack: null,
      error: null,
    },
    trackSort: {
      column: null,
      ascending: true,
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
    sortedTracks(state) {
      if (!state.trackSort.column) return state.tracks
      const sorted = [...state.tracks].sort((a, b) => {
        let valA = a[state.trackSort.column] || ''
        let valB = b[state.trackSort.column] || ''

        if (state.trackSort.column === 'duration') {
          const toSeconds = (str) => {
            if (typeof str !== 'string') return 0
            const parts = str.split(':').map(Number)
            if (parts.length === 2) return parts[0] * 60 + parts[1]
            return 0
          }
          valA = toSeconds(valA)
          valB = toSeconds(valB)
        } else {
          valA = valA.toString().toLowerCase()
          valB = valB.toString().toLowerCase()
        }

        if (valA < valB) return state.trackSort.ascending ? -1 : 1
        if (valA > valB) return state.trackSort.ascending ? 1 : -1
        return 0
      })
      return sorted
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
    setPlaylists(state, playlists) {
      state.playlists = playlists
    },
    setSelectedPlaylist(state, playlist) {
      state.selectedPlaylist = playlist
    },
    setTrackSort(state, { column, ascending }) {
      state.trackSort.column = column
      state.trackSort.ascending = ascending
    },
    setPlaylistTracks(state, tracks) {
      state.playlistTracks = tracks
    },
  },
  actions: {
    async fetchTracks({ commit }) {
      try {
        const res = await axios.get('http://localhost:8080/api/tracks')
        const data = res.data.map((t) => ({
          id: t.id,
          title: t.title,
          artist: t.artist,
          album: t.album,
          duration: t.duration,
          filePath: t.filePath,
          playUrl: `http://localhost:8080/api/play?path=${encodeURIComponent(t.filePath)}`,
          thumbnail: t.thumbnail || null,
        }))
        commit('setTracks', data)
      } catch (e) {
        console.error('Failed to fetch tracks:', e)
      }
    },

    async downloadTrack({ commit, state }) {
      commit('setDownloadedTrack', null)
      commit('setDownloadError', null)
      commit('setDownloading', true)
      try {
        const res = await axios.post('http://localhost:8080/api/download', {
          url: state.download.url,
          folder: state.download.folder,
        })
        commit('setDownloadedTrack', res.data)
      } catch (e) {
        commit('setDownloadError', e?.response?.data?.error || 'Download failed')
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
      } catch (e) {
        console.error('Failed to update metadata:', e)
      }
    },

    async loadPlaylists({ commit }) {
      const res = await fetch('http://localhost:8080/api/playlists')
      const data = await res.json()
      commit('setPlaylists', data)
    },

    async createPlaylist({ dispatch }, name) {
      await fetch(`http://localhost:8080/api/playlists/${encodeURIComponent(name)}`, {
        method: 'POST',
      })
      dispatch('loadPlaylists')
    },

    async addToPlaylist(_, { name, filePath }) {
      await fetch(`http://localhost:8080/api/playlists/${encodeURIComponent(name)}/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ filePath }),
      })
    },

    async removeFromPlaylist(_, { name, filePath }) {
      await fetch(`http://localhost:8080/api/playlists/${encodeURIComponent(name)}/remove`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ filePath }),
      })
    },

    async deletePlaylist({ dispatch }, name) {
      await fetch(`http://localhost:8080/api/playlists/${encodeURIComponent(name)}`, {
        method: 'DELETE',
      })
      dispatch('loadPlaylists')
    },

    async fetchPlaylistTracks(_, name) {
      const res = await fetch(
        `http://localhost:8080/api/playlists/${encodeURIComponent(name)}/tracks`,
      )
      return await res.json()
    },

    setSort({ commit, state }, column) {
      if (state.trackSort.column === column) {
        commit('setTrackSort', {
          column,
          ascending: !state.trackSort.ascending,
        })
      } else {
        commit('setTrackSort', {
          column,
          ascending: true,
        })
      }
    },
    nextTrack({ state, commit, getters }) {
      const sorted = getters.sortedTracks
      if (!sorted.length) return

      const currentId = state.tracks[state.currentTrackIndex]?.id
      const sortedIndex = sorted.findIndex((t) => t.id === currentId)

      if (state.repeat) return

      let nextSortedIndex
      if (state.shuffle) {
        const unplayed = sorted.map((_, i) => i).filter((i) => !state.history.includes(i))

        if (!unplayed.length) state.history = []

        nextSortedIndex = unplayed[Math.floor(Math.random() * unplayed.length)]
        state.history.push(nextSortedIndex)
      } else {
        nextSortedIndex = (sortedIndex + 1) % sorted.length
      }

      const nextTrack = sorted[nextSortedIndex]
      const actualIndex = state.tracks.findIndex((t) => t.id === nextTrack.id)

      commit('setCurrentTrackIndex', actualIndex)
    },

    prevTrack({ state, commit, getters }) {
      const sorted = getters.sortedTracks
      if (!sorted.length) return

      const currentId = state.tracks[state.currentTrackIndex]?.id
      const sortedIndex = sorted.findIndex((t) => t.id === currentId)

      if (state.repeat) return

      let prevSortedIndex
      if (state.shuffle) {
        if (state.history.length > 1) {
          state.history.pop()
          prevSortedIndex = state.history[state.history.length - 1]
        } else {
          return
        }
      } else {
        prevSortedIndex = (sortedIndex - 1 + sorted.length) % sorted.length
      }

      const prevTrack = sorted[prevSortedIndex]
      const actualIndex = state.tracks.findIndex((t) => t.id === prevTrack.id)

      commit('setCurrentTrackIndex', actualIndex)
    },
  },
  async fetchPlaylistTracks({ commit }, playlistId) {
    if (!playlistId) return
    const res = await axios.get(`http://localhost:8080/api/playlists/${playlistId}/tracks`)
    const data = res.data.map((t) => ({
      id: t.id,
      title: t.title,
      artist: t.artist,
      album: t.album,
      duration: t.duration,
      filePath: t.filePath,
      playUrl: `http://localhost:8080/api/play?path=${encodeURIComponent(t.filePath)}`,
      thumbnail: t.thumbnail || null,
    }))
    commit('setPlaylistTracks', data)
  },
})
