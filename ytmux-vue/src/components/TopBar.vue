<template>
  <header class="h-12 bg-[#161b22] border-b border-[#30363d] flex items-center px-4 z-50 relative">
    <!-- Search Input -->
    <input
      v-model="searchQuery"
      type="search"
      placeholder="Search..."
      class="bg-[#0d1117] text-[#c9d1d9] border border-[#30363d] rounded px-3 py-1 w-1/3 focus:outline-none focus:ring-2"
    />
    <div class="flex-1"></div>

    <!-- Help Button -->
    <button @click="toggleHelp" class="text-[#8b949e] hover:text-white">❓</button>

    <!-- Help Dropdown -->
    <div
      v-if="showHelp"
      class="absolute right-4 top-12 w-56 bg-[#0d1117] text-sm text-[#c9d1d9] border border-[#30363d] rounded p-3 shadow-lg z-50"
    >
      <p class="font-bold text-white mb-2">Keyboard Shortcuts</p>
      <ul class="space-y-1">
        <li><kbd class="kbd">Ctrl</kbd> + <kbd class="kbd">B</kbd> — Toggle Sidebar</li>
        <li><kbd class="kbd">/</kbd> — Focus Search</li>
      </ul>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const searchQuery = ref('')
const showHelp = ref(false)

function toggleHelp() {
  showHelp.value = !showHelp.value
}

function handleGlobalKey(e) {
  if (e.key === '/' && !e.ctrlKey) {
    e.preventDefault()
    const input = document.querySelector('input[type="search"]')
    input?.focus()
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleGlobalKey)
})
onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleGlobalKey)
})
</script>

<style scoped>
/* @apply inline-block px-1.5 py-0.5 rounded bg-[#21262d] text-xs text-[#c9d1d9] font-mono border border-[#30363d];
  .kbd {
} */
</style>
