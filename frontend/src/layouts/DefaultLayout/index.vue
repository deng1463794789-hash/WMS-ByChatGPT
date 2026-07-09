<template>
  <div class="default-layout">
    <div class="ambient ambient-a" />
    <div class="ambient ambient-b" />

    <aside class="layout-sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
      <AppSidebar />
    </aside>

    <section class="layout-main" :class="{ expanded: appStore.sidebarCollapsed }">
      <header class="layout-header">
        <AppHeader />
      </header>
      <div class="layout-tags" v-if="tagsViewStore.visitedViews.length > 0">
        <AppTagsView />
      </div>
      <AppMain />
    </section>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/modules/app'
import { useTagsViewStore } from '@/stores/modules/tagsView'
import AppSidebar from '@/components/AppSidebar/index.vue'
import AppHeader from '@/components/AppHeader/index.vue'
import AppTagsView from '@/components/AppTagsView/index.vue'
import AppMain from '@/components/AppMain/index.vue'

const appStore = useAppStore()
const tagsViewStore = useTagsViewStore()
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.default-layout {
  position: relative;
  display: flex;
  height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at 10% 8%, rgba(29, 78, 216, 0.1), transparent 30%),
    radial-gradient(circle at 88% 10%, rgba(8, 145, 178, 0.08), transparent 28%),
    linear-gradient(135deg, #f8fafc 0%, #f5f7fb 54%, #eef2f7 100%);

  .ambient {
    position: fixed;
    pointer-events: none;
    border-radius: 999px;

    opacity: 0.78;

  }

  .ambient-a {
    width: 320px;
    height: 320px;
    right: -110px;
    top: 90px;
    background: rgba(29, 78, 216, 0.07);
  }

  .ambient-b {
    width: 260px;
    height: 260px;
    left: 160px;
    bottom: -120px;
    background: rgba(8, 145, 178, 0.07);

  }

  .layout-sidebar {
    width: $sidebar-width;
    min-width: $sidebar-width;
    padding: 14px 0 14px 14px;
    transition: width 0.36s $ease-out, min-width 0.36s $ease-out, padding 0.36s $ease-out;
    z-index: 3;

    &.collapsed {
      width: $sidebar-collapsed-width;
      min-width: $sidebar-collapsed-width;
      padding-right: 0;
    }
  }

  .layout-main {
    position: relative;
    z-index: 2;
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    padding: 14px 18px 18px 18px;
    overflow: hidden;
  }

  .layout-header,
  .layout-tags {
    flex-shrink: 0;
  }

  .layout-tags {
    margin-top: 10px;
  }
}

@keyframes ambientFloat {
  from { transform: translate3d(0, 0, 0) scale(1); }
  to { transform: translate3d(18px, -16px, 0) scale(1.06); }
}

@media (max-width: 900px) {
  .default-layout {
    .layout-sidebar {
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: $sidebar-collapsed-width;
      min-width: $sidebar-collapsed-width;
      padding-right: 0;
    }

    .layout-main {
      padding-left: calc($sidebar-collapsed-width + 12px);
    }
  }
}
</style>
