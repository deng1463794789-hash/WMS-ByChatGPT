<template>
  <div class="loading-bar-container">
    <div class="loading-bar" :class="{ loading: isLoading, done: !isLoading && hasLoaded }" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const isLoading = ref(false)
const hasLoaded = ref(false)
let timer: ReturnType<typeof setTimeout> | null = null

const start = () => {
  hasLoaded.value = false
  requestAnimationFrame(() => {
    isLoading.value = true
  })
}

const done = () => {
  isLoading.value = false
  hasLoaded.value = true
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => {
    hasLoaded.value = false
  }, 300)
}

defineExpose({ start, done })
</script>

<style scoped>
.loading-bar-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  height: 3px;
  pointer-events: none;
}

.loading-bar {
  width: 0;
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  transition: width 0.3s ease;
}

.loading-bar.loading {
  width: 70%;
  transition: width 8s cubic-bezier(0, 1, 0, 1);
}

.loading-bar.done {
  width: 100%;
  transition: width 0.3s ease;
  opacity: 0;
  transition: width 0.3s ease, opacity 0.3s ease 0.2s;
}
</style>
