import { createRouter, createWebHistory } from 'vue-router'
import { allRoutes } from './routes'
import { setupRouterGuards } from './guards'

const router = createRouter({
  history: createWebHistory(),
  routes: allRoutes,
  scrollBehavior: () => ({ top: 0 })
})

export function setupRouter(app: any) {
  setupRouterGuards(router)
  app.use(router)
}

export default router
