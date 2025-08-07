<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="text-white font-mono">
    <h1 class="text-2xl font-bold mb-4">Artists</h1>
    <div
      v-for="artist in uniqueArtists"
      :key="artist"
      class="py-2 px-4 hover:bg-[#1f2933] cursor-pointer"
      @click="goToArtist(artist)"
    >
      {{ artist }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()

const uniqueArtists = computed(() => {
  const seen = new Set()
  return store.state.tracks
    .map((t) => t.artist || 'Unknown Artist')
    .filter((artist) => {
      if (seen.has(artist)) return false
      seen.add(artist)
      return true
    })
    .sort((a, b) => a.localeCompare(b))
})

function goToArtist(artist) {
  router.push({ name: 'ArtistDetail', params: { artist } })
}
</script>
