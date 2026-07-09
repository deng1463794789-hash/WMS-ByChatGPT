<template>
  <div class="app-breadcrumb">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
        {{ item.title }}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { menuRoutes } from '@/router/routes'
import type { MenuRouteRecord } from '@/router/routes'

const route = useRoute()

interface BreadcrumbItem {
  path: string
  title: string
}

const breadcrumbs = computed<BreadcrumbItem[]>(() => {
  const result: BreadcrumbItem[] = []
  const currentPath = route.path

  const findBreadcrumbs = (routes: MenuRouteRecord[], basePath: string) => {
    for (const r of routes) {
      const fullPath = (basePath + '/' + r.path).replace(/\/+/g, '/')
      if (r.meta?.hidden) continue
      if (currentPath === fullPath || (r.children && currentPath.startsWith(fullPath + '/'))) {
        if (r.meta?.title) {
          result.push({ path: fullPath, title: r.meta.title as string })
        }
        if (r.children) {
          findBreadcrumbs(r.children, fullPath)
        }
      }
    }
  }

  findBreadcrumbs(menuRoutes, '')
  return result
})
</script>

<style scoped lang="scss">
.app-breadcrumb {
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
}
</style>
