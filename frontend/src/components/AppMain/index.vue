<template>
  <div class="app-main">
    <router-view v-slot="{ Component, route }">
      <transition name="page-fade" mode="out-in">
        <keep-alive :include="tagsViewStore.cachedViews">
          <component :is="Component" :key="route.path" />
        </keep-alive>
      </transition>
    </router-view>
  </div>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { useRoute } from 'vue-router'
import { useTagsViewStore } from '@/stores/modules/tagsView'

const route = useRoute()
const tagsViewStore = useTagsViewStore()

watch(
  () => route.path,
  () => {
    tagsViewStore.addView(route)
  },
  { immediate: true }
)
</script>

<style scoped lang="scss">
.app-main {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f0f2f5;

  .page-fade-enter-active,
  .page-fade-leave-active {
    transition: opacity 0.25s ease, transform 0.25s ease;
  }

  .page-fade-enter-from {
    opacity: 0;
    transform: translateY(8px);
  }

  .page-fade-leave-to {
    opacity: 0;
  }
}
</style>
