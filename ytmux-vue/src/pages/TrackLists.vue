<template>
  <div
    class="h-full w-full flex flex-col font-mono text-white bg-transparent outline-none"
    @keydown="handleKey"
    tabindex="0"
    ref="tracklistRef"
  >
    <!-- Title -->
    <h1 class="text-xl font-bold mb-2">Tracklist</h1>

    <!-- Header Row -->
    <div class="flex items-center px-2 py-1 text-[#9ca3af] text-sm">
      <span class="w-[35%]">Name</span>
      <span class="w-[30%]">Artist</span>
      <span class="w-[25%]">Album</span>
      <span class="w-[10%] text-right">Length</span>
    </div>

    <!-- List of Tracks -->
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
import { ref, onMounted, nextTick } from 'vue'
import TrackRow from '@/components/TrackRow.vue'

const tracks = ref([
  {
    id: '1',
    title: 'Redbone',
    artist: 'Childish Gambino',
    album: 'Awaken, My Love!',
    length: '5:26',
  },
  {
    id: '2',
    title: 'Bohemian Rhapsody',
    artist: 'Queen',
    album: 'A Night at the Opera',
    length: '6:00',
  },
  {
    id: '3',
    title: 'Blinding Lights',
    artist: 'The Weeknd',
    album: 'After Hours',
    length: '3:20',
  },
])

const selectedIndex = ref(0)
const tracklistRef = ref(null)

function handleKey(e) {
  if (e.key === 'j') {
    selectedIndex.value = (selectedIndex.value + 1) % tracks.value.length
  } else if (e.key === 'k') {
    selectedIndex.value = (selectedIndex.value - 1 + tracks.value.length) % tracks.value.length
  }
}

onMounted(() => {
  nextTick(() => {
    tracklistRef.value?.focus()
  })
})
</script>
