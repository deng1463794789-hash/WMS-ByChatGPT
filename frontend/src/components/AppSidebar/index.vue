<template>
  <div class="app-sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
    <div class="sidebar-logo">
      <div class="logo-mark">
        <el-icon :size="24"><Box /></el-icon>
      </div>
      <div class="logo-copy" v-show="!appStore.sidebarCollapsed">
        <strong>WMS</strong>
        <span>智能仓储中枢</span>
      </div>
    </div>

    <div class="sidebar-status" v-show="!appStore.sidebarCollapsed">
      <span class="status-dot" />
      <span>后端实时连接</span>
    </div>

    <el-scrollbar class="menu-scrollbar">
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >
        <template v-for="route in visibleMenuRoutes" :key="route.path">
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
          <el-menu-item v-else-if="!route.meta?.hidden" :index="route.path">
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
import { menuRoutes } from '@/router/routes'
import type { MenuRouteRecord } from '@/router/routes'

const appStore = useAppStore()
const route = useRoute()

const activeMenu = computed(() => route.path)
const visibleMenuRoutes = computed(() => menuRoutes.filter((r: MenuRouteRecord) => !r.meta?.hidden))

const hasOneChild = (route: MenuRouteRecord): boolean => {
  if (!route.children) return false
  const visibleChildren = route.children.filter((c) => !c.meta?.hidden)
  return visibleChildren.length === 1
}

const firstVisibleChild = (route: MenuRouteRecord): MenuRouteRecord | null => {
  if (!route.children) return null
  return route.children.find((c) => !c.meta?.hidden) || null
}

const resolvePath = (parent: string, child: string): string => {
  if (child.startsWith('/')) return child
  return `${parent}/${child}`.replace(/\/+/g, '/')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.app-sidebar {
  position: relative;
  height: 100%;
  width: 100%;
  overflow: hidden;
  border-radius: 26px;
  background:
    linear-gradient(180deg, rgba(11, 18, 32, 0.99), rgba(15, 23, 42, 0.98)),
    radial-gradient(circle at 24% 8%, rgba(37, 99, 235, 0.16), transparent 30%);
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.2);
  transition: border-radius 0.36s $ease-out;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      linear-gradient(90deg, rgba(255,255,255,0.035) 1px, transparent 1px),
      linear-gradient(180deg, rgba(255,255,255,0.03) 1px, transparent 1px);
    background-size: 28px 28px;
    mask-image: linear-gradient(to bottom, rgba(0,0,0,0.8), transparent 78%);
    pointer-events: none;
  }

  &.collapsed {
    border-radius: 24px;
  }

  .sidebar-logo {
    position: relative;
    z-index: 1;
    height: 76px;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 0 18px;
  }

  .logo-mark {
    flex: 0 0 44px;
    width: 44px;
    height: 44px;
    display: grid;
    place-items: center;
    color: #fff;
    border-radius: 16px;
    background: #1d4ed8;
    box-shadow: 0 14px 30px rgba(29, 78, 216, 0.32);
  }

  .logo-copy {
    display: flex;
    flex-direction: column;
    line-height: 1.1;
    white-space: nowrap;

    strong {
      color: #fff;
      font-size: 19px;
      letter-spacing: 0.12em;
    }

    span {
      margin-top: 6px;
      color: rgba(226, 232, 240, 0.68);
      font-size: 12px;
    }
  }

  .sidebar-status {
    position: relative;
    z-index: 1;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    margin: 0 18px 14px;
    padding: 8px 12px;
    color: rgba(226, 232, 240, 0.74);
    font-size: 12px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.08);
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #22c55e;
    box-shadow: 0 0 0 6px rgba(34, 197, 94, 0.12);
  }

  .menu-scrollbar {
    position: relative;
    z-index: 1;
    height: calc(100vh - 118px);
  }

  .sidebar-menu {
    border-right: none;
    background: transparent;
  }

  :deep(.el-menu) {
    border-right: none;
    background: transparent;

    .el-menu-item,
    .el-sub-menu__title {
      height: 46px;
      margin: 5px 12px;
      padding: 0 14px !important;
      border-radius: 15px;
      color: $sidebar-text;
      transition: transform 0.22s $ease-out, color 0.22s ease, background 0.22s ease, box-shadow 0.22s ease;

      .el-icon {
        font-size: 18px;
        margin-right: 12px;
      }

      &:hover {
        color: #fff !important;
        background: rgba(255, 255, 255, 0.08) !important;
      }
    }

    .el-menu-item.is-active {
      color: $sidebar-active-text !important;
      background: #1d4ed8 !important;
      box-shadow: 0 14px 28px rgba(29, 78, 216, 0.3);
    }

    .el-sub-menu.is-active > .el-sub-menu__title {
      color: #fff !important;
    }
  }

  &.collapsed :deep(.el-menu) {
    .el-menu-item,
    .el-sub-menu__title {
      margin: 6px 10px;
      justify-content: center;

      .el-icon {
        margin-right: 0;
      }
    }
  }
}
</style>
