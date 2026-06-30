import type { Router } from 'vue-router'
import { getToken } from '@/utils/storage'

const whiteList = ['/login']

let loadingBar: { start: () => void; done: () => void } | null = null

export function setLoadingBar(bar: { start: () => void; done: () => void }) {
  loadingBar = bar
}

export function setupRouterGuards(router: Router) {
  router.beforeEach((to, _from, next) => {
    loadingBar?.start()
    document.title = `${to.meta.title || 'WMS管理系统'} - WMS`

    const token = getToken()
    if (token) {
      if (to.path === '/login') {
        next({ path: '/' })
      } else {
        next()
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
