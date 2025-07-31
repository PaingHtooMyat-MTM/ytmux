<template>
  <div
    class="h-full w-full flex flex-col font-mono text-white bg-transparent outline-none"
    @keydown="handleKey"
    tabindex="0"
    ref="tracklistRef"
  >
    <h1 class="text-xl font-bold mb-2">Tracklists</h1>

    <div class="flex items-center px-2 py-1 text-[#9ca3af] text-sm">
      <span class="w-[5%]">#</span>
      <span class="w-[50%]">Name</span>
      <span class="w-[15%]">Artist</span>
      <span class="w-[15%]">Album</span>
      <span class="w-[5%]">Length</span>
      <span class="w-[5%]">Play/Stop</span>
      <span class="w-[5%] text-right">Edit</span>
    </div>

    <div class="flex flex-col overflow-y-auto">
      <TrackRow
        v-for="(track, index) in tracks"
        :key="track.id"
        :track="track"
        :isSelected="index === selectedIndex"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useStore } from 'vuex'
import TrackRow from '@/components/TrackRow.vue'

const store = useStore()
const selectedIndex = ref(0)
const tracklistRef = ref(null)

const tracks = computed(() => store.state.tracks)

function handleKey(e) {
  if (!tracks.value.length) return

  if (e.key === 'j') {
    selectedIndex.value = (selectedIndex.value + 1) % tracks.value.length
  } else if (e.key === 'k') {
    selectedIndex.value = (selectedIndex.value - 1 + tracks.value.length) % tracks.value.length
  } else if (e.key === 'l' || e.key === 'Enter') {
    if (store.state.currentTrackIndex !== selectedIndex.value) {
      store.commit('setCurrentTrackIndex', selectedIndex.value)
      store.commit('setIsPlaying', true)
    }
  }
}

// function handlePlay(url, index) {
//   store.commit('setCurrentTrackIndex', index)
//   store.commit('setIsPlaying', true)
// }

onMounted(() => {
  if (!store.state.tracks.length) {
    store.dispatch('fetchTracks')
  }

  // Only reset index if the current track is NOT from this page
  if (store.state.currentTrackIndex === null) {
    selectedIndex.value = 0
  } else {
    selectedIndex.value = store.state.currentTrackIndex
  }

  nextTick(() => tracklistRef.value?.focus())
})
</script>
