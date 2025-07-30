import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/pages/Home.vue'
import Download from '@/pages/Download.vue'
// import Library from '@/pages/Library.vue'
// import Playlists from '@/pages/Playlists.vue'
// import Settings from '@/pages/Settings.vue'
import TrackLists from '@/pages/TrackLists.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/library/tracks', name: 'TracksLists', component: TrackLists },
  { path: '/download', name: 'Download', component: Download },
  // { path: '/library', name: 'Library', component: Library },
  // { path: '/playlists', name: 'Playlists', component: Playlists },
  // { path: '/settings', name: 'Settings', component: Settings },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
