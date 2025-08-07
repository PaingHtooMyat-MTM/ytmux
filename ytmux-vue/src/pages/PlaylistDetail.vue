<!-- src/pages/PlaylistDetail.vue -->
<template>
  <div class="h-full w-full flex flex-col">
    <Tracklists />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import Tracklists from '@/pages/TrackLists.vue'

const route = useRoute()
const store = useStore()

onMounted(async () => {
  const id = route.params.id
  await store.dispatch('fetchPlaylistTracks', id)
})
</script>
