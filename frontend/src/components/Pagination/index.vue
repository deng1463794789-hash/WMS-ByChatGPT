<template>
  <div class="pagination-container" :class="{ hidden: hidden }">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      :layout="layout"
      :background="true"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  modelValue?: number
  limit?: number
  total: number
  hidden?: boolean
  layout?: string
}>(), {
  modelValue: 1,
  limit: 10,
  hidden: false,
  layout: 'total, sizes, prev, pager, next, jumper'
})

const emit = defineEmits<{
  'update:modelValue': [value: number]
  'update:limit': [value: number]
  pagination: []
}>()

const currentPage = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const pageSize = computed({
  get: () => props.limit,
  set: (val) => emit('update:limit', val)
})

const handleSizeChange = () => {
  emit('pagination')
}

const handleCurrentChange = () => {
  emit('pagination')
}
</script>

<style scoped lang="scss">
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;

  &.hidden {
    display: none;
  }
}
</style>
