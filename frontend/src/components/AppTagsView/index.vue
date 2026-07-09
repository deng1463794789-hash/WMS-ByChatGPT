<template>
  <div class="app-tags-view">
    <el-scrollbar>
      <div class="tags-container">
        <router-link
          v-for="tag in tagsViewStore.visitedViews"
          :key="tag.path"
          :to="tag.path"
          class="tag-item"
          :class="{ active: isActive(tag) }"
        >
          <span class="tag-dot" />
          <span class="tag-title">{{ tag.title }}</span>
          <el-icon
            class="tag-close"
            v-if="!tag.affix"
            @click.prevent.stop="closeTag(tag)"
          >
            <Close />
          </el-icon>
        </router-link>
      </div>
    </el-scrollbar>
    <el-dropdown trigger="click" class="tags-dropdown">
      <button class="tags-more" aria-label="标签操作">
        <el-icon><ArrowDown /></el-icon>
      </button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="closeAll">关闭全部</el-dropdown-item>
          <el-dropdown-item @click="closeOthers">关闭其他</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useTagsViewStore, type TagView } from '@/stores/modules/tagsView'

const route = useRoute()
const router = useRouter()
const tagsViewStore = useTagsViewStore()

const isActive = (tag: TagView): boolean => tag.path === route.path

const closeTag = (view: TagView) => {
  tagsViewStore.delVisitedView(view)
  tagsViewStore.delCachedView(view)
  if (isActive(view)) {
    const views = tagsViewStore.visitedViews
    const lastView = views[views.length - 1]
    router.push(lastView ? lastView.path : '/')
  }
}

const closeAll = () => {
  tagsViewStore.delAllVisitedViews()
  router.push('/')
}

const closeOthers = () => {
  const currentView = tagsViewStore.visitedViews.find((v) => v.path === route.path)
  if (currentView) {
    tagsViewStore.delOthersVisitedViews(currentView)
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.app-tags-view {
  display: flex;
  align-items: center;
  height: 44px;
  padding: 5px 8px 5px 6px;
  border-radius: 20px;
  border: 1px solid $border-color;
  background: rgba(255, 255, 255, 0.94);

  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.045);

  .tags-container {
    display: flex;
    gap: 8px;
    padding: 0 4px;
  }

  .tag-item {
    display: inline-flex;
    align-items: center;
    gap: 7px;
    height: 32px;
    padding: 0 10px;
    color: $text-secondary;
    font-size: 12px;
    font-weight: 650;
    text-decoration: none;
    white-space: nowrap;
    border-radius: 999px;
    background: rgba(241, 245, 249, 0.78);
    border: 1px solid rgba(148, 163, 184, 0.15);
    transition: transform 0.2s $ease-out, color 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;

    &:hover {
      color: $primary-color;
      transform: translateY(-1px);
      background: #fff;
      box-shadow: 0 10px 24px rgba(29, 78, 216, 0.08);
    }

    &.active {
      color: #fff;
      background: $primary-color;
      box-shadow: 0 12px 28px rgba(29, 78, 216, 0.22);
      border-color: transparent;

      .tag-dot {
        background: #fff;
      }

      .tag-close {
        color: rgba(255, 255, 255, 0.85);
      }
    }
  }

  .tag-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #94a3b8;
  }

  .tag-close {
    font-size: 12px;
    border-radius: 50%;
    transition: background 0.2s ease, transform 0.2s ease;

    &:hover {
      background: rgba(15, 23, 42, 0.1);
      transform: rotate(90deg);
    }
  }

  .tags-dropdown {
    flex-shrink: 0;
    margin-left: 8px;
  }

  .tags-more {
    width: 30px;
    height: 30px;
    border: none;
    border-radius: 50%;
    color: $text-secondary;
    background: rgba(241, 245, 249, 0.9);
    cursor: pointer;
    transition: color 0.2s ease, background 0.2s ease;

    &:hover {
      color: $primary-color;
      background: #fff;
    }
  }
}
</style>
