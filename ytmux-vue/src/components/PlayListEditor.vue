<template>
  <div class="mt-6">
    <h2 class="text-xl font-semibold mb-2">Editing: {{ playlist.name }}</h2>
    <div v-if="playlist.tracks.length === 0" class="text-gray-500 mb-2">No tracks yet.</div>
    <ul class="mb-4 space-y-1">
      <li
        v-for="(track, i) in local.tracks"
        :key="i"
        class="flex justify-between items-center bg-[#2a2a2a] p-2 rounded"
      >
        <span>{{ track }}</span>
        <button @click="remove(i)" class="text-red-400 hover:underline">Remove</button>
      </li>
    </ul>

    <!-- Add new track path -->
    <div class="flex gap-2 mb-4">
      <input
        v-model="trackPath"
        placeholder="Add track (filePath)"
        class="border px-2 py-1 rounded w-full"
      />
      <button @click="add" class="bg-blue-500 px-3 py-1 text-white rounded">Add</button>
    </div>

    <button @click="save" class="bg-green-600 px-4 py-2 rounded text-white">Save Playlist</button>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
const props = defineProps(['playlist'])
const emit = defineEmits(['save'])

const local = reactive({ tracks: [...props.playlist.tracks] })
const trackPath = ref('')

const add = () => {
  if (trackPath.value.trim()) {
    local.tracks.push(trackPath.value.trim())
    trackPath.value = ''
  }
}

const remove = (index) => {
  local.tracks.splice(index, 1)
}

const save = () => {
  emit('save', { ...props.playlist, tracks: local.tracks })
}
</script>
