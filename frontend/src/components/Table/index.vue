<template>
  <div class="app-table">
    <el-table
      v-loading="loading"
      :data="loading ? [] : data"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      :element-loading-text="loadingText"
      :element-loading-svg-view-box="'-10, -10, 50, 50'"
    >
      <template v-if="loading" #empty>
        <div class="table-skeleton">
          <div v-for="i in skeletonRows" :key="i" class="skeleton-row">
            <div
              v-for="j in columns.length + (showSelection ? 1 : 0)"
              :key="j"
              class="skeleton-cell"
              :class="{
                'skeleton-selection': showSelection && j === 1,
                'skeleton-last': j === columns.length + (showSelection ? 1 : 0)
              }"
            >
              <div class="skeleton-line" :style="{ width: skeletonWidth(j) }" />
            </div>
          </div>
        </div>
      </template>

      <el-table-column
        v-if="showSelection"
        type="selection"
        width="50"
        align="center"
      />
      <el-table-column
        v-for="col in columns"
        :key="col.prop || col.label"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :align="col.align || 'center'"
        :sortable="col.sortable"
        :fixed="col.fixed"
        :show-overflow-tooltip="col.showOverflowTooltip !== false"
      >
        <template #default="{ row }" v-if="col.slot">
          <slot :name="col.slot" :row="row" />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
export interface TableColumn {
  prop?: string
  label: string
  width?: string | number
  minWidth?: string | number
  align?: 'left' | 'center' | 'right'
  sortable?: boolean | 'custom'
  showOverflowTooltip?: boolean
  fixed?: boolean | 'left' | 'right'
  slot?: string
}

const props = withDefaults(defineProps<{
  data: any[]
  columns: TableColumn[]
  loading?: boolean
  loadingText?: string
  showSelection?: boolean
  skeletonRows?: number
  operationWidth?: string
}>(), {
  loading: false,
  loadingText: '加载中...',
  showSelection: false,
  skeletonRows: 5
})

const emit = defineEmits<{
  'selection-change': [value: any[]]
  'sort-change': [value: { prop: string; order: string | null }]
}>()

const handleSelectionChange = (selection: any[]) => {
  emit('selection-change', selection)
}

const handleSortChange = (sort: { prop: string; order: string | null }) => {
  emit('sort-change', sort)
}

const skeletonWidth = (colIndex: number) => {
  if (props.showSelection && colIndex === 1) return '20px'
  const lastIdx = props.columns.length + (props.showSelection ? 1 : 0)
  if (colIndex === lastIdx) return '60px'
  return Math.random() * 40 + 50 + '%'
}
</script>

<style scoped lang="scss">
.app-table {
  .table-skeleton {
    padding: 0;

    .skeleton-row {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }
    }

    .skeleton-cell {
      flex: 1;
      padding: 0 12px;

      &.skeleton-selection {
        flex: 0 0 50px;
        display: flex;
        justify-content: center;
      }

      &.skeleton-last {
        flex: 0 0 160px;
      }
    }

    .skeleton-line {
      height: 14px;
      background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: skeletonShimmer 1.5s ease-in-out infinite;
      border-radius: 4px;
      min-width: 40px;
    }
  }
}

@keyframes skeletonShimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
