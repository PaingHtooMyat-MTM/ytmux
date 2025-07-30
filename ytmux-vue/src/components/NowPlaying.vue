<template>
  <div
    class="bg-[#161b22] border-t border-[#30363d] flex items-center px-4 py-2 text-white select-none"
  >
    <!-- Album Art -->
    <img
      v-if="track?.thumbnail"
      :src="track.thumbnail"
      alt="Album Art"
      class="w-12 h-12 rounded-md mr-4 object-cover"
    />
    <div class="flex flex-col flex-1 min-w-0">
      <p class="truncate font-semibold">{{ track?.title || 'No Track Playing' }}</p>
      <p class="truncate text-gray-400 text-sm">{{ track?.artist || '' }}</p>
    </div>

    <!-- Playback Controls -->
    <div class="flex items-center gap-4 mx-4">
      <button @click="prevTrack" aria-label="Previous Track" class="hover:text-[#81c8f1]">
        ⏮
      </button>
      <button @click="togglePlayPause" aria-label="Play/Pause" class="hover:text-[#81c8f1]">
        {{ isPlaying ? '⏸' : '▶️' }}
      </button>
      <button @click="nextTrack" aria-label="Next Track" class="hover:text-[#81c8f1]">⏭</button>
    </div>

    <!-- Progress Bar -->
    <div class="flex flex-col items-end min-w-[150px]">
      <div class="text-xs text-gray-400 mb-1">
        {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
      </div>
      <div class="w-full h-1 bg-gray-700 rounded overflow-hidden">
        <div class="h-1 bg-[#81c8f1]" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

// Mock current track data (replace with your store or API data)
const track = ref({
  title: 'Redbone',
  artist: 'Childish Gambino',
  thumbnail: 'https://i.ytimg.com/vi/Kp7eSUU9oy8/hqdefault.jpg',
  duration: 326, // in seconds
})

const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(track.value.duration || 0)

// Calculate progress percentage
const progressPercent = computed(() =>
  duration.value ? (currentTime.value / duration.value) * 100 : 0,
)

// Playback controls (stub, you will wire these to real logic)
function togglePlayPause() {
  isPlaying.value = !isPlaying.value
}

function prevTrack() {
  alert('Previous track - to implement')
}

function nextTrack() {
  alert('Next track - to implement')
}

// Format seconds to mm:ss
function formatTime(seconds) {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// Mock playback progress
let interval = null
watch(isPlaying, (play) => {
  if (play) {
    interval = setInterval(() => {
      if (currentTime.value < duration.value) {
        currentTime.value++
      } else {
        clearInterval(interval)
        isPlaying.value = false
      }
    }, 1000)
  } else {
    clearInterval(interval)
  }
})
</script>

<style scoped>
button {
  font-size: 1.25rem;
  background: transparent;
  border: none;
  cursor: pointer;
}
button:focus {
  outline: none;
}
</style>
