<template>
  <aside
    class="bg-[#161b22] text-white h-full w-1/5 min-w-[200px] p-4 border-r border-[#30363d]"
    @keydown="handleNav"
    tabindex="0"
    ref="sidebarRef"
  >
    <div class="flex flex-col gap-6 outline-none">
      <h1 class="text-xl font-bold tracking-tight mb-2">
        <span class="text-4xl text-blue-400">𝄞</span>
        YTMux
      </h1>

      <!-- Library Section -->
      <div>
        <h2 class="text-gray-400 uppercase text-xs font-semibold mb-2 pl-2">Library</h2>
        <ul class="flex flex-col gap-1" role="menu">
          <RouterLink
            v-for="(item, index) in libraryItems"
            :key="item.name"
            :to="item.to"
            class="px-3 py-1 rounded focus:outline-none"
            :class="{
              'text-blue-400 font-bold': index === selectedIndex,
              'text-white': index !== selectedIndex,
            }"
            tabindex="-1"
            ref="el => itemRefs[index] = el"
          >
            {{ item.name }}
          </RouterLink>
        </ul>
      </div>

      <!-- Downloads -->
      <div>
        <h2 class="text-gray-400 uppercase text-xs font-semibold mb-2 pl-2">Downloads</h2>
        <ul>
          <RouterLink
            to="/download"
            class="px-3 py-1 rounded focus:outline-none"
            :class="{
              'text-blue-400 font-bold': selectedIndex === libraryItems.length,
              'text-white': selectedIndex !== libraryItems.length,
            }"
            tabindex="-1"
            ref="el => itemRefs[libraryItems.length] = el"
          >
            Download
          </RouterLink>
        </ul>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const libraryItems = [
  { name: 'Tracklists', to: '/tracks' },
  { name: 'Playlists', to: '/playlists' },
  { name: 'Albums', to: '/albums' },
  { name: 'Artists', to: '/artists' },
]

const allItems = [...libraryItems, { name: 'Download', to: '/download' }]

const selectedIndex = ref(0)
const itemRefs = []
const sidebarRef = ref(null)

function updateSelectedIndexFromRoute() {
  const i = allItems.findIndex((item) => route.path.startsWith(item.to))
  if (i !== -1) selectedIndex.value = i
}

function handleNav(e) {
  if (e.key === 'j') {
    selectedIndex.value = (selectedIndex.value + 1) % allItems.length
  } else if (e.key === 'k') {
    selectedIndex.value = (selectedIndex.value - 1 + allItems.length) % allItems.length
  } else if (e.key === 'l' || e.key === 'Enter') {
    const target = allItems[selectedIndex.value]
    if (target) router.push(target.to)
  }

  nextTick(() => {
    itemRefs[selectedIndex.value]?.focus()
  })
}

onMounted(() => {
  updateSelectedIndexFromRoute()
  nextTick(() => {
    sidebarRef.value?.focus()
  })
})

watch(() => route.path, updateSelectedIndexFromRoute)
</script>
