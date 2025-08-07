<template>
  <div
    v-if="currentTrack"
    class="w-full px-4 py-2 bg-[#0d1117] border-t border-[#1f2933] text-white text-sm flex flex-col"
    @keydown="handleKey"
    tabindex="0"
  >
    <div class="flex items-center justify-between w-full">
      <!-- Track Info -->
      <div class="truncate max-w-[50%]">
        <span class="text-[#81c8f1] font-bold">{{ currentTrack.title }}</span>
        <span class="text-gray-400 ml-2">{{ currentTrack.artist }}</span>
      </div>

      <!-- Playback Controls -->
      <div class="flex items-center gap-4 ml-auto">
        <!-- Repeat -->
        <button
          @click="toggleRepeat"
          :class="{
            'text-[#81c8f1] bg-[#1f2933] rounded px-2': isRepeating,
          }"
          class="text-gray-400 hover:text-[#81c8f1] text-lg"
          title="Repeat"
        >
          ⟳
        </button>

        <!-- Shuffle -->
        <button
          @click="toggleShuffle"
          :class="{
            'text-[#81c8f1] bg-[#1f2933] rounded px-2': isShuffling,
          }"
          class="text-gray-400 hover:text-[#81c8f1] text-lg"
          title="Shuffle"
        >
          ⇄
        </button>

        <!-- Previous -->
        <button
          @click="prevTrack"
          class="text-gray-400 hover:text-[#81c8f1] text-lg"
          title="Previous Song"
        >
          ⏮
        </button>

        <!-- Play/Pause -->
        <button
          @click="togglePlayback"
          class="text-blue-300 hover:text-green-300 text-lg"
          :title="isPlaying ? 'Pause' : 'Play'"
        >
          {{ isPlaying ? '⏸' : '▶' }}
        </button>

        <!-- Next -->
        <button
          @click="nextTrack"
          class="text-gray-400 hover:text-[#81c8f1] text-lg"
          title="Next Song"
        >
          ⏭
        </button>

        <!-- Volume -->
        <div class="flex items-center gap-2">
          <span class="text-gray-400 text-xs">Vol</span>
          <input
            type="range"
            min="0"
            max="1"
            step="0.01"
            :value="volume"
            @input="onVolumeChange"
            class="w-[120px] accent-[#81c8f1]"
          />
          <span class="text-xs text-gray-300 w-[32px] text-right"
            >{{ Math.round(volume * 100) }}%</span
          >
        </div>
      </div>
    </div>

    <!-- Progress Bar -->
    <div class="mt-3 w-full h-2 bg-[#1f2933] rounded relative cursor-pointer" @click="seek($event)">
      <div class="h-full bg-[#81c8f1] rounded" :style="{ width: progressPercent + '%' }"></div>
    </div>

    <!-- Time display -->
    <div class="flex justify-end text-xs text-gray-400 mt-1 select-none">
      {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
    </div>

    <!-- Hidden Audio -->
    <audio
      ref="audioRef"
      :src="currentTrack.playUrl"
      autoplay
      @timeupdate="onTimeUpdate"
      @ended="onTrackEnd"
      @loadedmetadata="setDuration"
      class="hidden"
    ></audio>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const audioRef = ref(null)

const currentTrack = computed(() => store.getters.currentTrack)
const isPlaying = computed(() => store.state.isPlaying)
const volume = computed(() => store.state.volume)
const currentTime = computed(() => store.state.currentTime)
const isRepeating = computed(() => store.state.repeat)
const isShuffling = computed(() => store.state.shuffle)
const duration = ref(0)

const progressPercent = computed(() =>
  duration.value ? (currentTime.value / duration.value) * 100 : 0,
)

function togglePlayback() {
  if (!audioRef.value) return
  isPlaying.value ? audioRef.value.pause() : audioRef.value.play()
  store.commit('setIsPlaying', !isPlaying.value)
}

function onTimeUpdate() {
  if (audioRef.value) {
    store.commit('setCurrentTime', audioRef.value.currentTime)
  }
}

function onTrackEnd() {
  if (isRepeating.value) {
    audioRef.value.currentTime = 0
    audioRef.value.play()
  } else {
    nextTrack()
  }
}

function setDuration() {
  if (audioRef.value) duration.value = audioRef.value.duration || 0
}

function seek(event) {
  if (!audioRef.value || !duration.value) return
  const { left, width } = event.currentTarget.getBoundingClientRect()
  const percent = (event.clientX - left) / width
  const newTime = duration.value * percent
  audioRef.value.currentTime = newTime
  store.commit('setCurrentTime', newTime)
}

function onVolumeChange(event) {
  const val = parseFloat(event.target.value)
  updateVolume(val)
}

function updateVolume(val) {
  const clamped = Math.max(0, Math.min(1, val))
  store.commit('setVolume', clamped)
  if (audioRef.value) audioRef.value.volume = clamped
}

function handleKey(event) {
  if (event.key === '+' || event.key === '=') {
    updateVolume(volume.value + 0.05)
  } else if (event.key === '-') {
    updateVolume(volume.value - 0.05)
  } else if (event.key === 'ArrowLeft') {
    audioRef.value.currentTime = Math.max(audioRef.value.currentTime - 5, 0)
    store.commit('setCurrentTime', audioRef.value.currentTime)
  } else if (event.key === 'ArrowRight') {
    audioRef.value.currentTime = Math.min(audioRef.value.currentTime + 5, duration.value)
    store.commit('setCurrentTime', audioRef.value.currentTime)
  }
}

function formatTime(seconds) {
  if (!seconds || isNaN(seconds)) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

function nextTrack() {
  store.dispatch('nextTrack').then(() => {
    store.commit('setIsPlaying', true)
  })
}

function prevTrack() {
  store.dispatch('prevTrack').then(() => {
    store.commit('setIsPlaying', true)
  })
}

function toggleRepeat() {
  store.commit('toggleRepeat')
}

function toggleShuffle() {
  store.commit('toggleShuffle')
}

watch(currentTrack, (track) => {
  if (audioRef.value && track) {
    audioRef.value.load()
    audioRef.value.play()
    store.commit('setIsPlaying', true)
  }
})

watch(volume, (val) => {
  if (audioRef.value) {
    audioRef.value.volume = val
  }
})

watch(isPlaying, (playing) => {
  if (!audioRef.value) return
  playing ? audioRef.value.play().catch(() => {}) : audioRef.value.pause()
})

onMounted(() => {
  if (audioRef.value) audioRef.value.volume = volume.value
})
</script>
