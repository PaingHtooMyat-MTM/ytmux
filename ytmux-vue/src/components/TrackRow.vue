<template>
  <div
    class="w-full px-2 py-1 flex items-center gap-x-2 text-sm transition-colors cursor-pointer"
    :class="{
      'bg-[#1f2933] text-[#81c8f1] font-bold': isSelected,
      'bg-[#113425] text-green-400 font-bold': isCurrentTrackPlaying && !isSelected,
      'hover:text-[#81c8f1] hover:bg-[#1a1f25]': !isSelected && !isCurrentTrackPlaying,
    }"
    @click="togglePlay"
  >
    <!-- Normal Mode -->
    <template v-if="!isEditing">
      <span class="w-[3%]">{{ index }}</span>
      <span class="w-[47%] truncate">{{ track.title }}</span>
      <span class="w-[20%] truncate text-gray-400">{{ track.artist }}</span>
      <span class="w-[20%] truncate text-gray-500">{{ track.album }}</span>
      <span class="w-[5%] text-gray-500">{{ formatDuration(track.duration) }}</span>

      <!-- Add to Playlist Button -->
      <span class="w-[5%] flex justify-center items-center relative">
        <img
          src="/add-to-playlist.png"
          class="h-4 w-4 opacity-80 hover:opacity-100"
          @click.stop="togglePlaylistDropdown"
        />

        <!-- Playlist Dropdown -->
        <div
          v-if="showPlaylistDropdown"
          ref="dropdownRef"
          class="absolute right-0 mt-6 w-60 max-h-64 overflow-y-auto bg-[#1a1f29] border border-gray-700 rounded shadow-lg z-50"
        >
          <div v-if="playlists.length">
            <div
              v-for="p in playlists"
              :key="p.id"
              class="px-4 py-2 hover:bg-[#111827] cursor-pointer text-white transition-colors"
              @click="addToPlaylist(p.id)"
            >
              {{ p.name }}
            </div>
          </div>
          <div v-else class="px-4 py-2 text-gray-400 italic">No playlists</div>

          <!-- Divider -->
          <hr class="border-gray-600 my-2" />

          <!-- Add New Playlist -->
          <div class="px-3 pb-3 flex items-center gap-x-2">
            <input
              v-model="newPlaylistName"
              @keydown.enter="createPlaylist"
              type="text"
              placeholder="New playlist..."
              class="flex-grow px-2 py-1 rounded bg-[#2a2f36] text-white border border-gray-600 focus:outline-none focus:ring-1 focus:ring-green-400 text-sm"
            />
            <button
              @click="createPlaylist"
              class="text-green-400 hover:text-green-300 transition text-sm font-bold"
            >
              Add
            </button>
          </div>
        </div>
      </span>
    </template>

    <!-- Edit Mode -->
    <template v-else>
      <span class="w-[3%]">{{ index }}</span>
      <input
        v-model="edited.title"
        class="w-[47%] bg-[#2a2f36] text-white px-2 py-1 truncate rounded-sm"
      />
      <input
        v-model="edited.artist"
        class="w-[15%] bg-[#2a2f36] text-white px-2 py-1 truncate rounded-sm"
      />
      <input
        v-model="edited.album"
        class="w-[15%] bg-[#2a2f36] text-white px-2 py-1 truncate rounded-sm"
      />
      <span class="w-[5%] text-gray-500 text-center">{{ formatDuration(track.duration) }}</span>
      <span class="w-[5%] flex justify-center text-green-400 hover:text-green-200 font-bold">
        <button @click.stop="save" title="Save">Save</button>
      </span>
      <span class="w-[5%] flex justify-center text-red-400 hover:text-red-200 font-bold">
        <button @click.stop="cancel" title="Cancel">Cancel</button>
      </span>
    </template>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { useStore } from 'vuex'
import axios from 'axios'

const { track, isSelected, index, editing } = defineProps({
  track: Object,
  isSelected: Boolean,
  index: Number,
  editing: Boolean,
})

const store = useStore()
const isEditing = ref(false)
const edited = ref({ ...track })
const newPlaylistName = ref('')
const dropdownRef = ref(null)

const playlists = ref([])
const showPlaylistDropdown = ref(false)

onMounted(loadPlaylists)
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
async function loadPlaylists() {
  try {
    const res = await axios.get('http://localhost:8080/api/playlists')
    playlists.value = res.data
  } catch (e) {
    console.error('Failed to load playlists:', e)
  }
}

function togglePlaylistDropdown() {
  showPlaylistDropdown.value = !showPlaylistDropdown.value
}

async function addToPlaylist(playlistId) {
  try {
    await axios.post(`http://localhost:8080/api/playlists/${playlistId}/add/${track.id}`)
    showPlaylistDropdown.value = false
  } catch (e) {
    console.error('Failed to add to playlist:', e)
  }
}

async function createPlaylist() {
  const name = newPlaylistName.value.trim()
  if (!name) return
  try {
    const res = await axios.post(
      `http://localhost:8080/api/playlists?name=${encodeURIComponent(name)}`,
    )
    playlists.value.push(res.data)
    newPlaylistName.value = ''
  } catch (e) {
    console.error('Failed to create playlist:', e)
  }
}

function handleClickOutside(event) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    showPlaylistDropdown.value = false
  }
}

// If global editing changes, sync local edit mode
watch(
  () => editing,
  (newVal) => {
    isEditing.value = newVal
    if (newVal) {
      edited.value = { ...track }
    }
  },
  { immediate: true },
)

const currentTrack = computed(() => store.getters.currentTrack)
const isPlaying = computed(() => store.state.isPlaying)
const isCurrentTrack = computed(() => currentTrack.value?.id === track.id)
const isCurrentTrackPlaying = computed(() => isPlaying.value && isCurrentTrack.value)

function togglePlay() {
  if (isEditing.value) return
  if (isCurrentTrack.value) {
    store.commit('setIsPlaying', !isPlaying.value)
  } else {
    store.commit('setCurrentTrackById', track.id)
    store.commit('setIsPlaying', true)
  }
}

function cancel() {
  isEditing.value = false
}

async function save() {
  const currentTrackPath = track.filePath
  await store.dispatch('updateTrackMetadata', edited.value)
  await store.dispatch('fetchTracks')
  const newIndex = store.state.tracks.findIndex((t) => t.path === currentTrackPath)
  if (newIndex !== -1) {
    store.commit('setCurrentTrackIndex', newIndex)
  }
  store.commit('setIsPlaying', false)
  isEditing.value = false
}

function formatDuration(seconds) {
  const m = Math.floor(seconds / 60)
    .toString()
    .padStart(2, '0')
  const s = Math.floor(seconds % 60)
    .toString()
    .padStart(2, '0')
  return `${m}:${s}`
}
</script>
