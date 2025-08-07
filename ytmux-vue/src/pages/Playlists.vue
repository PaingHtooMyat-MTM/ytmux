<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="p-4 text-white font-mono">
    <h1 class="text-2xl font-bold mb-4">Playlists</h1>

    <!-- Create playlist -->
    <div class="flex gap-2 mb-4">
      <input
        v-model="newPlaylist"
        @keyup.enter="createPlaylist"
        placeholder="New Playlist Name"
        class="bg-[#1f2933] text-white p-2 rounded"
      />
      <button @click="createPlaylist" class="bg-green-600 px-3 py-1 rounded">Create</button>
    </div>

    <!-- If no playlists -->
    <div v-if="!playlists.length" class="text-gray-400 italic">No playlists found.</div>

    <!-- List playlists -->
    <div v-else class="grid gap-2">
      <div
        v-for="playlist in playlists"
        :key="playlist.id"
        class="flex justify-between items-center bg-[#1a1f29] px-4 py-2 rounded hover:bg-[#111827] cursor-pointer"
      >
        <span @click="goToPlaylist(playlist.id)" class="hover:underline">
          {{ playlist.name }}
        </span>
        <button @click.stop="deletePlaylist(playlist.id)" class="text-red-500 hover:underline">
          Delete
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const playlists = ref([])
const newPlaylist = ref('')
const router = useRouter()

const loadPlaylists = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/playlists')
    playlists.value = res.data
  } catch (e) {
    console.error('Failed to load playlists:', e)
  }
}

const createPlaylist = async () => {
  if (!newPlaylist.value.trim()) return
  try {
    await axios.post('http://localhost:8080/api/playlists', {
      name: newPlaylist.value.trim(),
    })
    newPlaylist.value = ''
    await loadPlaylists()
  } catch (e) {
    console.error('Failed to create playlist:', e)
  }
}

const deletePlaylist = async (id) => {
  try {
    await axios.delete(`http://localhost:8080/api/playlists/${id}`)
    await loadPlaylists()
  } catch (e) {
    console.error('Failed to delete playlist:', e)
  }
}

const goToPlaylist = (id) => {
  router.push(`/playlists/${id}`)
}

onMounted(loadPlaylists)
</script>
