<template>
  <div
    class="h-full w-full flex flex-col text-white bg-transparent"
    @keydown="handleKey"
    tabindex="0"
    ref="tracklistRef"
  >
    <div class="flex items-center justify-between mb-2">
      <h1 class="text-xl font-bold">Tracklists</h1>
      <button
        @click="editingAll = !editingAll"
        class="text-sm px-3 py-1 font-semibold bg-blue-500 rounded hover:bg-blue-600 transition"
      >
        {{ editingAll ? 'Done' : 'Edit' }}
      </button>
    </div>

    <!-- Sortable Headers -->
    <div
      v-if="!editingAll"
      class="flex items-center justify-around px-2 py-1 gap-x-2 text-[#9ca3af] text-sm select-none"
    >
      <span class="w-[3%]">#</span>
      <span
        class="w-[47%] cursor-pointer hover:text-white hover:font-bold"
        @click="sortBy('title')"
      >
        Name
      </span>
      <span
        class="w-[20%] cursor-pointer hover:text-white hover:font-bold"
        @click="sortBy('artist')"
      >
        Artist
      </span>
      <span
        class="w-[20%] cursor-pointer hover:text-white hover:font-bold"
        @click="sortBy('album')"
      >
        Album
      </span>
      <span class="w-[5%]">Length</span>
      <span class="w-[5%] flex justify-center items-center">Playlist</span>
      <!-- Moved this to the end -->
    </div>

    <div v-else class="flex items-center px-2 py-1 gap-x-2 text-[#9ca3af] text-sm select-none">
      <span class="w-[3%]">#</span>
      <span
        class="w-[47%] cursor-pointer hover:text-white hover:font-bold"
        @click="sortBy('title')"
      >
        Name
      </span>
      <span
        class="w-[15%] cursor-pointer hover:text-white hover:font-bold"
        @click="sortBy('artist')"
      >
        Artist
      </span>
      <span
        class="w-[15%] cursor-pointer hover:text-white hover:font-bold"
        @click="sortBy('album')"
      >
        Album
      </span>
      <span class="w-[5%]">Length</span>
      <span class="w-[5%]">&nbsp;</span>
    </div>

    <!-- Track Rows -->
    <div class="flex flex-col overflow-y-auto">
      <TrackRow
        v-for="(track, index) in sortedTracks"
        :key="track.id"
        :track="track"
        :index="index + 1"
        :isSelected="index === selectedIndex"
        :isPlaying="track.id === currentTrack?.id && isPlaying"
        :editing="editingAll"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useStore } from 'vuex'
import TrackRow from '@/components/TrackRow.vue'

const props = defineProps({
  tracks: {
    type: Array,
    default: () => [],
  },
})

const store = useStore()
const selectedIndex = ref(0)
const tracklistRef = ref(null)
const editingAll = ref(false)
const openDropdownId = ref(null)

const sortedTracks = computed(() => {
  const list = props.tracks.length ? props.tracks : store.getters.sortedTracks
  // Apply your sorting logic here (if needed)
  return list
})
const currentTrack = computed(() => store.getters.currentTrack)
const isPlaying = computed(() => store.state.isPlaying)

function handleKey(e) {
  // Ignore all shortcuts when typing in an input, textarea, or contentEditable
  const target = e.target
  if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable) {
    return
  }

  if (!sortedTracks.value.length) return

  if (e.key === 'j') {
    selectedIndex.value = (selectedIndex.value + 1) % sortedTracks.value.length
  } else if (e.key === 'k') {
    selectedIndex.value =
      (selectedIndex.value - 1 + sortedTracks.value.length) % sortedTracks.value.length
  } else if (e.key === 'l' || e.key === 'Enter') {
    const selectedTrack = sortedTracks.value[selectedIndex.value]
    const originalIndex = store.state.tracks.findIndex((t) => t.id === selectedTrack.id)

    if (store.state.currentTrackIndex !== originalIndex) {
      store.commit('setCurrentTrackIndex', originalIndex)
      store.commit('setIsPlaying', true)
    }
  }
}

function sortBy(column) {
  store.dispatch('setSort', column)
}

onMounted(async () => {
  if (!store.state.tracks.length) {
    await store.dispatch('fetchTracks')
    await store.dispatch('setSort', 'title')
  }

  const currentId = store.state.tracks[store.state.currentTrackIndex]?.id
  const sortedIndex = sortedTracks.value.findIndex((t) => t.id === currentId)

  selectedIndex.value = sortedIndex >= 0 ? sortedIndex : 0

  nextTick(() => tracklistRef.value?.focus())
})
</script>
