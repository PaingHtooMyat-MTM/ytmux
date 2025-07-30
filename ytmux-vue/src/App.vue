<template>
  <div class="w-screen h-screen bg-[#0d1117] text-white flex flex-col relative">
    <!-- Modal -->
    <SearchModal v-if="showSearchModal" :songs="songs" @close="showSearchModal = false" />

    <!-- Main Layout -->
    <div class="flex flex-1 overflow-hidden">
      <transition name="slide">
        <Sidebar v-if="showSidebar" />
      </transition>
      <main class="flex-1 h-full overflow-y-auto p-6 bg-[#0d1117]">
        <RouterView />
      </main>
    </div>

    <div class="now-playing-container">
      <NowPlaying />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import Sidebar from './components/SideBar.vue'
import SearchModal from './components/SearchModal.vue'
import NowPlaying from './components/NowPlaying.vue'

const showSidebar = ref(true)
const showSearchModal = ref(false)

// Dummy data for downloaded songs (replace with your real list)
const songs = ref([
  { title: 'Redbone', artist: 'Childish Gambino' },
  { title: 'Blinding Lights', artist: 'The Weeknd' },
  { title: 'Bohemian Rhapsody', artist: 'Queen' },
])

function toggleSidebar() {
  showSidebar.value = !showSidebar.value
}

function handleKeydown(e) {
  if (e.ctrlKey && e.key.toLowerCase() === 'b') {
    e.preventDefault()
    toggleSidebar()
  } else if (e.key === '/') {
    e.preventDefault()
    showSearchModal.value = true
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.slide-enter-active,
.slide-leave-active {
  transition: all 0.2s ease;
}
.slide-enter-from,
.slide-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}
.now-playing-container {
  position: fixed;
  bottom: 0;
  width: 100%;
  z-index: 100;
}
</style>
