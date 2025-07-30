<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="flex flex-col gap-6 text-sm text-[#c9d1d9] font-mono leading-relaxed">
    <!-- Now Playing -->
    <section v-if="nowPlaying">
      <h2 class="text-[#58a6ff] uppercase tracking-wider mb-1">Now Playing</h2>
      <div class="pl-2">
        <p class="font-bold">{{ nowPlaying.title }}</p>
        <p class="text-[#8b949e]">{{ nowPlaying.artist }}</p>
      </div>
    </section>

    <!-- Recently Played -->
    <section>
      <h2 class="text-[#58a6ff] uppercase tracking-wider mb-1">Recently Played</h2>
      <ul class="pl-2 space-y-1">
        <li
          v-for="track in recentPlayed"
          :key="track.id"
          @click="playTrack(track)"
          class="cursor-pointer hover:text-white"
        >
          <span class="text-[#8b949e]">▶</span> {{ track.title }} —
          <span class="text-[#8b949e]">{{ track.artist }}</span>
        </li>
      </ul>
    </section>

    <!-- Quick Access -->
    <section>
      <h2 class="text-[#58a6ff] uppercase tracking-wider mb-1">Quick Access</h2>
      <ul class="pl-2 space-y-1">
        <li class="cursor-pointer hover:text-white" @click="$router.push('/library')">
          → Open Library
        </li>
        <li class="cursor-pointer hover:text-white" @click="$router.push('/download')">
          → Download Music
        </li>
        <li class="cursor-pointer hover:text-white" @click="$router.push('/playlists')">
          → View Playlists
        </li>
      </ul>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const nowPlaying = ref({
  id: 'np1',
  title: 'Cool Song',
  artist: 'Awesome Artist',
})

const recentPlayed = ref([
  { id: 'r1', title: 'Track One', artist: 'Artist A' },
  { id: 'r2', title: 'Track Two', artist: 'Artist B' },
  { id: 'r3', title: 'Track Three', artist: 'Artist C' },
])

function playTrack(track) {
  console.log('Play track:', track)
}
</script>
