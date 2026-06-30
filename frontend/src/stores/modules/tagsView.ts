import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteLocationNormalized } from 'vue-router'

export interface TagView {
  path: string
  name: string
  title: string
  affix?: boolean
}

export const useTagsViewStore = defineStore('tagsView', () => {
  const visitedViews = ref<TagView[]>([])
  const cachedViews = ref<string[]>([])

  const addVisitedView = (view: TagView) => {
    if (visitedViews.value.some((v) => v.path === view.path)) return
    visitedViews.value.push({ ...view })
  }

  const addCachedView = (view: TagView) => {
    if (cachedViews.value.includes(view.name)) return
    if (view.name) {
      cachedViews.value.push(view.name)
    }
  }

  const delVisitedView = (view: TagView) => {
    const index = visitedViews.value.findIndex((v) => v.path === view.path)
    if (index > -1) {
      visitedViews.value.splice(index, 1)
    }
  }

  const delCachedView = (view: TagView) => {
    const index = cachedViews.value.indexOf(view.name)
    if (index > -1) {
      cachedViews.value.splice(index, 1)
    }
  }

  const delOthersVisitedViews = (view: TagView) => {
    const keepNames = visitedViews.value
      .filter((v) => v.affix || v.path === view.path)
      .map((v) => v.name)
    visitedViews.value = visitedViews.value.filter(
      (v) => v.affix || v.path === view.path
    )
    cachedViews.value = cachedViews.value.filter((name) => keepNames.includes(name))
  }

  const delAllVisitedViews = () => {
    const affixNames = visitedViews.value.filter((v) => v.affix).map((v) => v.name)
    visitedViews.value = visitedViews.value.filter((v) => v.affix)
    cachedViews.value = cachedViews.value.filter((name) => affixNames.includes(name))
  }

  const updateVisitedView = (view: TagView) => {
    const item = visitedViews.value.find((v) => v.path === view.path)
    if (item) {
      Object.assign(item, view)
    }
  }

  const addView = (route: RouteLocationNormalized) => {
    addVisitedView({
      name: route.name as string,
      path: route.path,
      title: (route.meta?.title as string) || 'no-name',
      affix: route.meta?.affix as boolean
    })
    addCachedView({
      name: route.name as string,
      path: route.path,
      title: (route.meta?.title as string) || 'no-name'
    })
  }

  return {
    visitedViews,
    cachedViews,
    addView,
    addVisitedView,
    addCachedView,
    delVisitedView,
    delCachedView,
    delOthersVisitedViews,
    delAllVisitedViews,
    updateVisitedView
  }
})
