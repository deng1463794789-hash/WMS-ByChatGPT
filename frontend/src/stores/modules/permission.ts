import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import { asyncRoutes } from '@/router/routes'
import { getMenuList } from '@/api/system'
import type { MenuItem } from '@/api/types/system'

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([])
  const addRoutes = ref<RouteRecordRaw[]>([])
  const menus = ref<MenuItem[]>([])

  const generateRoutes = async () => {
    let accessedRoutes: RouteRecordRaw[]
    try {
      const res = await getMenuList()
      menus.value = res.data || []
      accessedRoutes = filterAsyncRoutes(asyncRoutes, menus.value)
    } catch (e) {
      accessedRoutes = asyncRoutes
    }
    routes.value = accessedRoutes
    addRoutes.value = accessedRoutes
    return accessedRoutes
  }

  const filterAsyncRoutes = (routes: RouteRecordRaw[], menus: MenuItem[]): RouteRecordRaw[] => {
    const res: RouteRecordRaw[] = []
    routes.forEach((route) => {
      const tmp = { ...route }
      if (hasPermission(menus, tmp)) {
        if (tmp.children) {
          tmp.children = filterAsyncRoutes(tmp.children, menus)
        }
        res.push(tmp)
      }
    })
    return res
  }

  const hasPermission = (menus: MenuItem[], route: RouteRecordRaw): boolean => {
    if (route.meta?.hidden) return true
    if (!route.name) return false
    return menus.some((menu) => menu.path === route.path || menu.name === route.name)
  }

  const resetRoutes = () => {
    routes.value = []
    addRoutes.value = []
    menus.value = []
  }

  return {
    routes,
    addRoutes,
    menus,
    generateRoutes,
    resetRoutes
  }
})
