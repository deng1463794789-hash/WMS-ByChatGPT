<template>
  <div class="app-sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
    <div class="sidebar-logo">
      <el-icon :size="24" color="#fff"><Box /></el-icon>
      <span class="logo-title" v-show="!appStore.sidebarCollapsed">WMS管理系统</span>
    </div>
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :unique-opened="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        class="sidebar-menu"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <template v-if="!route.meta?.hidden && route.children && route.children.length > 0">
            <el-sub-menu :index="route.path" v-if="!hasOneChild(route)">
              <template #title>
                <el-icon><component :is="route.meta?.icon || 'Menu'" /></el-icon>
                <span>{{ route.meta?.title }}</span>
              </template>
              <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="resolvePath(route.path, child.path)"
                v-show="!child.meta?.hidden"
              >
                <el-icon v-if="child.meta?.icon"><component :is="child.meta.icon" /></el-icon>
                <span>{{ child.meta?.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item
              v-else
              :index="resolvePath(route.path, firstVisibleChild(route)?.path || '')"
            >
              <el-icon><component :is="route.meta?.icon || firstVisibleChild(route)?.meta?.icon || 'Menu'" /></el-icon>
              <span>{{ route.meta?.title || firstVisibleChild(route)?.meta?.title }}</span>
            </el-menu-item>
          </template>
          <el-menu-item
            v-else-if="!route.meta?.hidden"
            :index="route.path"
          >
            <el-icon><component :is="route.meta?.icon || 'Menu'" /></el-icon>
            <span>{{ route.meta?.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/modules/app'
import { allRoutes } from '@/router/routes'
import type { RouteRecordRaw } from 'vue-router'

const appStore = useAppStore()
const route = useRoute()

const activeMenu = computed(() => {
  const { path } = route
  return path
})

const menuRoutes = computed(() => {
  return allRoutes.filter((r: RouteRecordRaw) => !r.meta?.hidden)
})

const hasOneChild = (route: RouteRecordRaw): boolean => {
  if (!route.children) return false
  const visibleChildren = route.children.filter((c) => !c.meta?.hidden)
  return visibleChildren.length === 1
}

const firstVisibleChild = (route: RouteRecordRaw): RouteRecordRaw | null => {
  if (!route.children) return null
  return route.children.find((c) => !c.meta?.hidden) || null
}

const resolvePath = (parent: string, child: string): string => {
  if (child.startsWith('/')) return child
  return `${parent}/${child}`.replace(/\/+/g, '/')
}
</script>

<style scoped lang="scss">
.app-sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 220px;
  background-color: #304156;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1001;
  overflow: hidden;

  &.collapsed {
    width: 64px;
  }

  .sidebar-logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    transition: all 0.3s ease;

    .logo-title {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      white-space: nowrap;
      overflow: hidden;
    }
  }

  .sidebar-menu {
    border-right: none;
  }

  :deep(.el-menu) {
    border-right: none;

    .el-menu-item,
    .el-sub-menu__title {
      transition: all 0.2s ease;
      margin: 2px 8px;
      border-radius: 6px;
      min-width: auto;

      &:hover {
        background: rgba(255, 255, 255, 0.08) !important;
      }
    }

    .el-menu-item.is-active {
      background: linear-gradient(90deg, rgba(64, 158, 255, 0.15), rgba(64, 158, 255, 0.05)) !important;
      border-radius: 6px;
    }
  }

  :deep(.el-scrollbar__view) {
    height: calc(100vh - 60px);
  }
}
</style>
