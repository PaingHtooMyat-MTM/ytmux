<template>
  <div
    class="w-full px-2 py-1 flex items-center justify-between font-mono text-sm transition-colors"
    :class="{
      'bg-[#1f2933] text-[#81c8f1] font-bold': isSelected,
      'hover:text-[#81c8f1] hover:bg-[#1a1f25] cursor-pointer': !isSelected,
    }"
  >
    <div class="flex w-full items-center gap-1">
      <template v-if="!isEditing">
        <span class="w-[5%]">1111</span>
        <span class="w-[50%] truncate" @dblclick="startEditing">{{ track.title }}</span>
        <span class="w-[15%] truncate text-gray-400" @dblclick="startEditing">{{
          track.artist
        }}</span>
        <span class="w-[15%] truncate text-gray-500" @dblclick="startEditing">{{
          track.album
        }}</span>
        <span class="w-[5%] text-gray-500">{{ track.duration }}</span>

        <!-- Play/Pause Button -->
        <button
          @click.stop="togglePlay"
          class="w-[5%] pl-4 text-blue-300 hover:text-green-300"
          :title="isCurrentTrackPlaying ? 'Pause' : 'Play'"
        >
          {{ isCurrentTrackPlaying ? '⏸' : '▶' }}
        </button>

        <!-- Edit Button -->
        <button
          @click.stop="startEditing"
          class="w-[5%] ml-1 text-yellow-400 hover:text-yellow-200"
          title="Edit metadata"
        >
          ✏️
        </button>
      </template>

      <!-- Edit Mode -->
      <template v-else>
        <div class="flex w-full items-center gap-1">
          <span class="w-[5%]">1111</span>
          <input v-model="edited.title" class="w-[50%] bg-[#2a2f36] text-white px-1" />
          <input v-model="edited.artist" class="w-[15%] bg-[#2a2f36] text-white px-1" />
          <input v-model="edited.album" class="w-[15%] bg-[#2a2f36] text-white px-1" />

          <button @click.stop="save" class="text-green-400 hover:text-green-200 ml-2" title="Save">
            💾
          </button>
          <button @click.stop="cancel" class="text-red-400 hover:text-red-200" title="Cancel">
            ✖
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useStore } from 'vuex'

const { track, isSelected } = defineProps({
  track: Object,
  isSelected: Boolean,
})

const store = useStore()
const isEditing = ref(false)
const edited = ref({ ...track })

const currentTrack = computed(() => store.getters.currentTrack)
const isPlaying = computed(() => store.state.isPlaying)

const isCurrentTrack = computed(() => currentTrack.value?.id === track.id)
const isCurrentTrackPlaying = computed(() => isPlaying.value && isCurrentTrack.value)

function togglePlay() {
  if (isCurrentTrack.value) {
    store.commit('setIsPlaying', !isPlaying.value)
  } else {
    store.commit('setCurrentTrackById', track.id)
    store.commit('setIsPlaying', true)
  }
}

function startEditing() {
  edited.value = { ...track }
  isEditing.value = true
}

function cancel() {
  isEditing.value = false
}

async function save() {
  const currentTrackPath = track.filePath

  await store.dispatch('updateTrackMetadata', edited.value)
  await store.dispatch('fetchTracks')

  // Restore selection or current track
  const newIndex = store.state.tracks.findIndex((t) => t.path === currentTrackPath)
  if (newIndex !== -1) {
    store.commit('setCurrentTrackIndex', newIndex)
  }

  isEditing.value = false
}
</script>
