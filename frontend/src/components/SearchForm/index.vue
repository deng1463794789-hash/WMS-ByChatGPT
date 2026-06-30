<template>
  <div class="search-form">
    <el-form :model="model" inline>
      <slot name="fields">
        <el-form-item
          v-for="field in fields"
          :key="field.prop"
          :label="field.label"
          :style="field.width ? { width: field.width } : {}"
        >
          <el-input
            v-if="field.type === 'input'"
            v-model="model[field.prop]"
            :placeholder="field.placeholder || `请输入${field.label}`"
            clearable
            :style="{ width: field.inputWidth || '200px' }"
            @keyup.enter="handleSearch"
          />
          <el-select
            v-else-if="field.type === 'select'"
            v-model="model[field.prop]"
            :placeholder="field.placeholder || `请选择${field.label}`"
            clearable
            :style="{ width: field.inputWidth || '200px' }"
          >
            <el-option
              v-for="opt in field.options"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
          <el-date-picker
            v-else-if="field.type === 'date'"
            v-model="model[field.prop]"
            type="date"
            :placeholder="field.placeholder || `请选择${field.label}`"
            :style="{ width: field.inputWidth || '200px' }"
            value-format="YYYY-MM-DD"
          />
          <el-date-picker
            v-else-if="field.type === 'daterange'"
            v-model="model[field.prop]"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :style="{ width: field.inputWidth || '360px' }"
            value-format="YYYY-MM-DD"
          />
          <el-input-number
            v-else-if="field.type === 'number'"
            v-model="model[field.prop]"
            :placeholder="field.placeholder"
            :style="{ width: field.inputWidth || '200px' }"
          />
        </el-form-item>
      </slot>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
        <slot name="extra" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
export interface SearchField {
  prop: string
  label: string
  type: 'input' | 'select' | 'date' | 'daterange' | 'number'
  placeholder?: string
  width?: string
  inputWidth?: string
  options?: Array<{ label: string; value: string | number }>
}

const props = defineProps<{
  model: Record<string, any>
  fields: SearchField[]
}>()

const emit = defineEmits<{
  search: []
  reset: []
}>()

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  emit('reset')
}
</script>

<style scoped lang="scss">
.search-form {
  :deep(.el-form-item) {
    margin-bottom: 8px;
  }
}
</style>
