<template>
  <ul class="space-y-2">
    <li
      v-for="p in playlists"
      :key="p.name"
      class="flex justify-between items-center bg-[#1f1f1f] p-2 rounded"
    >
      <span class="cursor-pointer text-blue-300 hover:underline" @click="$emit('select', p)">
        {{ p.name }}
      </span>
      <button @click="$emit('delete', p.name)" class="text-red-500 hover:underline">Delete</button>
    </li>
  </ul>
</template>

<script setup>
defineProps(['playlists'])
defineEmits(['select', 'delete'])
</script>
