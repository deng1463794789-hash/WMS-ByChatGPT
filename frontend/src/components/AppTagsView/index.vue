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
      <el-button size="small" type="primary" link>
        <el-icon><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="closeAll">关闭所有</el-dropdown-item>
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

const isActive = (tag: TagView): boolean => {
  return tag.path === route.path
}

const closeTag = (view: TagView) => {
  tagsViewStore.delVisitedView(view)
  tagsViewStore.delCachedView(view)
  if (isActive(view)) {
    const views = tagsViewStore.visitedViews
    const lastView = views[views.length - 1]
    if (lastView) {
      router.push(lastView.path)
    } else {
      router.push('/')
    }
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
.app-tags-view {
  display: flex;
  align-items: center;
  height: 34px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  padding-right: 8px;

  .tags-container {
    display: flex;
    padding: 4px 4px 0;

    .tag-item {
      display: flex;
      align-items: center;
      height: 26px;
      padding: 0 10px;
      margin-right: 4px;
      font-size: 12px;
      color: #495060;
      background: #f0f2f5;
      border: 1px solid #e8e8e8;
      border-radius: 2px;
      text-decoration: none;
      white-space: nowrap;

      &.active {
        color: #fff;
        background: #409EFF;
        border-color: #409EFF;

        .tag-close {
          color: #fff;
        }
      }

      .tag-close {
        margin-left: 6px;
        font-size: 12px;
        cursor: pointer;
        border-radius: 50%;
        &:hover {
          background: rgba(0, 0, 0, 0.1);
        }
      }
    }
  }

  .tags-dropdown {
    flex-shrink: 0;
  }
}
</style>
