<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="h-full w-full p-4 font-mono text-white">
    <h1 class="text-xl font-bold mb-4">Download from YouTube</h1>

    <div class="flex flex-col gap-2 mb-4">
      <label class="text-sm text-gray-400">YouTube URL:</label>
      <input
        v-model="url"
        type="text"
        class="bg-[#1a1f25] text-white px-3 py-2 rounded focus:outline-none"
        placeholder="https://www.youtube.com/watch?v=..."
      />

      <label class="text-sm text-gray-400">Folder Path:</label>
      <input
        v-model="folder"
        type="text"
        class="bg-[#1a1f25] text-white px-3 py-2 rounded focus:outline-none"
        placeholder="C:/Users/Paing Htoo Myat/Music"
      />

      <button
        class="mt-2 bg-[#81c8f1] hover:bg-[#63b3ed] text-black font-bold py-2 px-4 rounded w-max disabled:opacity-50 disabled:cursor-not-allowed"
        @click="startDownload"
        :disabled="downloading"
      >
        {{ downloading ? 'Downloading...' : 'Download' }}
      </button>

      <div v-if="downloading" class="text-yellow-400 text-sm mt-2 font-bold">
        ⏳ Downloading... Please wait.
      </div>
    </div>

    <div v-if="downloadedTrack" class="mt-4 text-sm">
      <p>
        <span class="text-green-400">✓</span> <strong>Downloaded:</strong>
        {{ downloadedTrack.title }}
      </p>
      <p><strong>Duration:</strong> {{ downloadedTrack.duration }}s</p>
      <p><strong>Artist:</strong> {{ downloadedTrack.artist }}</p>
      <p><strong>Album:</strong> {{ downloadedTrack.album }}</p>
      <p><strong>File:</strong> {{ downloadedTrack.filePath }}</p>
      <img
        v-if="downloadedTrack.thumbnail"
        :src="downloadedTrack.thumbnail"
        class="w-32 mt-2 rounded"
      />
    </div>

    <div v-if="error" class="text-red-500 mt-4">{{ error }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

const url = computed({
  get: () => store.state.download.url,
  set: (val) => store.commit('setDownloadUrl', val),
})

const folder = computed({
  get: () => store.state.download.folder,
  set: (val) => store.commit('setDownloadFolder', val),
})

const downloading = computed(() => store.state.download.downloading)
const downloadedTrack = computed(() => store.state.download.downloadedTrack)
const error = computed(() => store.state.download.error)

function startDownload() {
  store.dispatch('downloadTrack')
}
</script>
