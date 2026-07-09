<template>
  <main class="app-main">
    <router-view v-slot="{ Component, route }">
      <transition name="page-slide">
        <keep-alive :include="tagsViewStore.cachedViews">
          <component :is="Component" :key="route.path" />
        </keep-alive>
      </transition>
    </router-view>
  </main>
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
@use '@/assets/styles/variables.scss' as *;

.app-main {
  position: relative;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  margin-top: 10px;
  padding: 16px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.4);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.52);

  .page-slide-enter-active,
  .page-slide-leave-active {
    transition: opacity 0.16s ease, transform 0.16s $ease-out;
  }

  .page-slide-enter-from {
    opacity: 0;
    transform: translate3d(6px, 3px, 0);

  }

  .page-slide-leave-to {
    opacity: 0;
    transform: translate3d(-4px, -2px, 0);

  }
}

@media (max-width: 720px) {
  .app-main {
    padding: 12px;
    border-radius: 20px;
  }
}
</style>