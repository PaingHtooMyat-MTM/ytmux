<!-- src/views/ArtistDetail.vue -->
<template>
  <div class="text-white font-mono">
    <h1 class="text-2xl font-bold mb-4">Songs by {{ artist }}</h1>
    <ul>
      <li
        v-for="track in artistTracks"
        :key="track.id"
        class="py-2 px-4 hover:text-[#81c8f1] cursor-pointer"
        @click="playTrack(track)"
      >
        {{ track.title }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'

const route = useRoute()
const store = useStore()

const artist = route.params.artist

const artistTracks = computed(() =>
  store.state.tracks.filter((t) => (t.artist || 'Unknown Artist') === artist),
)

function playTrack(track) {
  store.commit('setCurrentTrackById', track.id)
  store.commit('setIsPlaying', true)
}
</script>
