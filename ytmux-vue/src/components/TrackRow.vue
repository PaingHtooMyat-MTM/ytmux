<template>
  <div
    class="w-full px-2 py-1 flex items-center gap-x-2 text-sm transition-colors"
    :class="{
      'bg-[#1f2933] text-[#81c8f1] font-bold': isSelected,
      'hover:text-[#81c8f1] hover:bg-[#1a1f25] cursor-pointer': !isSelected,
    }"
  >
    <template v-if="!isEditing">
      <span class="w-[5%]">{{ index + 1 }}</span>
      <span class="w-[50%] truncate" @dblclick="startEditing">{{ track.title }}</span>
      <span class="w-[15%] truncate text-gray-400" @dblclick="startEditing">{{
        track.artist
      }}</span>
      <span class="w-[15%] truncate text-gray-500" @dblclick="startEditing">{{ track.album }}</span>
      <span class="w-[5%] text-gray-500">{{ track.duration }}</span>
      <span class="w-[5%] text-blue-300 hover:text-green-300 text-center">
        <button @click.stop="togglePlay" :title="isCurrentTrackPlaying ? 'Pause' : 'Play'">
          {{ isCurrentTrackPlaying ? '⏸' : '▶' }}
        </button>
      </span>
      <span class="w-[5%] text-white hover:text-yellow-200 text-right">
        <button @click.stop="startEditing" title="Edit metadata">Edit</button>
      </span>
    </template>

    <!-- Edit Mode -->
    <template v-else>
      <span class="w-[5%]">{{ index + 1 }}</span>
      <input
        v-model="edited.title"
        class="w-[50%] bg-[#2a2f36] text-white px-2 py-1 truncate rounded-sm"
      />
      <input
        v-model="edited.artist"
        class="w-[15%] bg-[#2a2f36] text-white px-2 py-1 truncate rounded-sm"
      />
      <input
        v-model="edited.album"
        class="w-[15%] bg-[#2a2f36] text-white px-2 py-1 truncate rounded-sm"
      />
      <span class="w-[5%] text-gray-500 text-center">{{ track.duration }}</span>
      <span class="w-[5%] text-green-400 hover:text-green-200 text-center">
        <button @click.stop="save" title="Save">Save</button>
      </span>
      <span class="w-[5%] text-red-400 hover:text-red-200 text-right">
        <button @click.stop="cancel" title="Cancel">Cancel</button>
      </span>
    </template>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useStore } from 'vuex'

const { track, isSelected, index } = defineProps({
  track: Object,
  isSelected: Boolean,
  index: Number,
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

  // Save the changes and reload tracks
  await store.dispatch('updateTrackMetadata', edited.value)
  await store.dispatch('fetchTracks')

  // Restore the current track index without playing
  const newIndex = store.state.tracks.findIndex((t) => t.path === currentTrackPath)
  if (newIndex !== -1) {
    store.commit('setCurrentTrackIndex', newIndex)
  }

  // Ensure nothing is playing
  store.commit('setIsPlaying', false)

  isEditing.value = false
}
</script>
