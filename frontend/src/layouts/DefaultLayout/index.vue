<template>
  <div class="default-layout">
    <div class="layout-sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
      <AppSidebar />
    </div>
    <div class="layout-main" :class="{ expanded: appStore.sidebarCollapsed }">
      <div class="layout-header">
        <AppHeader />
      </div>
      <div class="layout-tags" v-if="tagsViewStore.visitedViews.length > 0">
        <AppTagsView />
      </div>
      <AppMain />
    </div>
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
.default-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;

  .layout-sidebar {
    width: 220px;
    min-width: 220px;
    transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1), min-width 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &.collapsed {
      width: 64px;
      min-width: 64px;
    }
  }

  .layout-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-width: 0;
    transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .layout-header {
    flex-shrink: 0;
  }

  .layout-tags {
    flex-shrink: 0;

    :deep(.tags-view-container) {
      background: #fff;
      padding: 6px 12px 0;
      border-bottom: 1px solid #e8e8e8;
    }
  }
}
</style>
