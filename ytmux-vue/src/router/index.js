import { createRouter, createWebHistory } from 'vue-router'
import Download from '@/pages/Download.vue'
import Playlists from '@/pages/Playlists.vue'
import TrackLists from '@/pages/TrackLists.vue'
// import Artists from '@/pages/Artists.vue'
// import Albums from '@/pages/Albums.vue'
// import ArtistsDetail from '@/pages/ArtistsDetail.vue'
import PlaylistDetail from '@/pages/PlaylistDetail.vue'

const routes = [
  { path: '/tracks', name: 'TracksLists', component: TrackLists },
  { path: '/download', name: 'Download', component: Download },
  { path: '/playlists', name: 'Playlists', component: Playlists },
  { path: '/playlists/:id', name: 'PlaylistDetail', component: PlaylistDetail, props: true },
  // { path: '/artists', name: 'Artists', component: Artists },
  // { path: '/artists/:artist', name: 'ArtistsDetail', component: ArtistsDetail },
  // { path: '/albums', name: 'Albums', component: Albums },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
