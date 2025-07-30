<template>
  <div class="absolute inset-0 z-50 bg-black/70 flex items-center justify-center">
    <div
      class="bg-[#161b22] text-white w-full max-w-2xl p-4 rounded-xl border border-gray-700 shadow-xl"
    >
      <input
        v-model="query"
        ref="input"
        @keydown.up.prevent="moveSelection(-1)"
        @keydown.down.prevent="moveSelection(1)"
        @keydown.enter.prevent="confirmSelection"
        @keydown.esc="emit('close')"
        type="text"
        class="w-full p-2 bg-transparent border-b border-gray-600 focus:outline-none"
        placeholder="Search downloaded songs..."
      />

      <ul class="mt-4 max-h-60 overflow-y-auto">
        <li
          v-for="(result, index) in filtered"
          :key="result.title + result.artist"
          :class="[
            'px-3 py-1 rounded',
            selectedIndex === index ? 'bg-[#30363d] font-semibold' : 'hover:bg-gray-800',
          ]"
        >
          {{ result.title }} <span class="text-gray-400 text-sm">– {{ result.artist }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import Fuse from 'fuse.js'

const props = defineProps({
  songs: Array,
})
const emit = defineEmits(['close'])

const query = ref('')
const selectedIndex = ref(0)
const filtered = ref([])

const fuse = new Fuse(props.songs, {
  keys: ['title', 'artist'],
  threshold: 0.4,
})

watch(query, () => {
  const results = fuse.search(query.value)
  filtered.value = query.value ? results.map((r) => r.item) : props.songs
  selectedIndex.value = 0
})

function moveSelection(delta) {
  if (!filtered.value.length) return
  selectedIndex.value =
    (selectedIndex.value + delta + filtered.value.length) % filtered.value.length
}

function confirmSelection() {
  const selected = filtered.value[selectedIndex.value]
  if (selected) {
    // you can do something with the selected song here
    console.log('Selected:', selected)
    emit('close')
  }
}

const input = ref(null)
onMounted(() => {
  input.value?.focus()
  filtered.value = props.songs
})
</script>
