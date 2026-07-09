import { createRouter, createWebHistory } from 'vue-router'
import { routerRoutes } from './routes'
import { setupRouterGuards } from './guards'

const router = createRouter({
  history: createWebHistory(),
  routes: routerRoutes,
  scrollBehavior: () => ({ top: 0 })
})

export function setupRouter(app: any) {
  setupRouterGuards(router)
  app.use(router)
}

export default router