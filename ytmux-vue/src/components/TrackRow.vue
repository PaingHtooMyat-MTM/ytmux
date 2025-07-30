<template>
  <div
    class="w-full px-2 py-1 flex items-center font-mono text-sm transition-colors"
    :class="{
      'bg-[#1f2933] text-[#81c8f1] font-bold': isSelected,
      'hover:text-[#81c8f1] hover:bg-[#1a1f25] cursor-pointer': !isSelected,
    }"
  >
    <span class="w-[35%] truncate">{{ track.title }}</span>
    <span class="w-[30%] truncate text-gray-400">{{ track.artist }}</span>
    <span class="w-[25%] truncate text-gray-500">{{ track.album }}</span>
    <span class="w-[10%] text-right text-gray-500">{{ track.length }}</span>
  </div>
</template>

<script setup>
defineProps({
  track: Object,
  isSelected: Boolean,
})
</script>
