<template>
  <LoadingBar ref="loadingBarRef" />
  <router-view v-slot="{ Component }">
    <transition name="fade-transform" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import LoadingBar from '@/components/LoadingBar/index.vue'
import { setLoadingBar } from '@/router/guards'

const loadingBarRef = ref<InstanceType<typeof LoadingBar>>()

onMounted(() => {
  if (loadingBarRef.value) {
    setLoadingBar(loadingBarRef.value)
  }
})
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

#app {
  height: 100%;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
