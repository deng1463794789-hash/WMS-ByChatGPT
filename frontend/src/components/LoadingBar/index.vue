<template>
  <div class="loading-bar-container">
    <div class="loading-glow" :class="{ active: isLoading }" />
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
  }, 360)
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
  height: 4px;
  pointer-events: none;
  overflow: hidden;
}

.loading-glow {
  position: absolute;
  inset: -10px 0 auto;
  height: 20px;
  opacity: 0;
  background: radial-gradient(circle, rgba(29, 78, 216, 0.32), transparent 60%);
  transition: opacity 0.24s ease;
}

.loading-glow.active {
  opacity: 1;
}

.loading-bar {
  width: 0;
  height: 100%;
  border-radius: 0 999px 999px 0;
  background: linear-gradient(90deg, #1d4ed8, #2563eb, #0891b2);
  box-shadow: 0 0 18px rgba(29, 78, 216, 0.42);
  transition: width 0.28s ease;
}

.loading-bar.loading {
  width: 76%;
  transition: width 7s cubic-bezier(0.16, 1, 0.3, 1);
}

.loading-bar.done {
  width: 100%;
  opacity: 0;
  transition: width 0.22s ease, opacity 0.34s ease 0.16s;
}
</style>
