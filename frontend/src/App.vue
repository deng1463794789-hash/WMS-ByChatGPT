<template>
  <LoadingBar ref="loadingBarRef" />
  <router-view v-slot="{ Component }">
    <transition name="app-shell" appear>
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
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: "HarmonyOS Sans SC", "MiSans", "PingFang SC", "Microsoft YaHei", sans-serif;
  color: #0f172a;
  background: #eef3f8;
}

#app {
  height: 100%;
}

.app-shell-enter-active,
.app-shell-leave-active {
  transition: opacity 0.16s ease, transform 0.16s ease;
}

.app-shell-enter-from {
  opacity: 0;
  transform: translateY(10px) scale(0.99);

}

.app-shell-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.995);

}
</style>