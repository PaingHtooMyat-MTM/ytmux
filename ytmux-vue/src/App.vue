<template>
  <div class="w-screen h-screen bg-[#0d1117] text-white flex flex-col relative">
    <!-- Modal -->
    <SearchModal v-if="showSearchModal" :songs="tracks" @close="closeSearch" />

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
import { computed, onMounted, onBeforeUnmount } from 'vue'
import { useStore } from 'vuex'
import Sidebar from './components/SideBar.vue'
import SearchModal from './components/SearchModal.vue'
import NowPlaying from './components/NowPlaying.vue'

const store = useStore()

const showSidebar = computed(() => store.state.showSidebar)
const showSearchModal = computed(() => store.state.showSearchModal)
const tracks = computed(() => store.state.tracks)

function toggleSidebar() {
  store.commit('setShowSidebar', !store.state.showSidebar)
}

function closeSearch() {
  store.commit('setShowSearchModal', false)
}

function handleKeydown(e) {
  if (e.ctrlKey && e.key.toLowerCase() === 'b') {
    e.preventDefault()
    toggleSidebar()
  } else if (e.key === '/') {
    e.preventDefault()
    store.commit('setShowSearchModal', true)
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
  store.dispatch('fetchTracks')
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
