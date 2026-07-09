import type { Router } from 'vue-router'
import { getToken } from '@/utils/storage'
import { preloadRouteComponents } from './routes'

const whiteList = ['/login']

let loadingBar: { start: () => void; done: () => void } | null = null

export function setLoadingBar(bar: { start: () => void; done: () => void }) {
  loadingBar = bar
}

export function setupRouterGuards(router: Router) {
  router.beforeEach((to, _from, next) => {
    loadingBar?.start()
    document.title = `${to.meta.title || 'WMS 管理系统'} - WMS`

    const token = getToken()
    if (token) {
      if (to.path === '/login') {
        next({ path: '/' })
      } else {
        next()
        preloadRouteComponents()
      }
    } else {
      if (whiteList.includes(to.path)) {
        next()
      } else {
        next(`/login?redirect=${to.path}`)
      }
    }
  })

  router.afterEach(() => {
    loadingBar?.done()
  })
}