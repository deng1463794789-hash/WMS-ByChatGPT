<template>
  <div class="product-list">
    <el-card class="search-card">
      <SearchForm
        :model="queryParams"
        :fields="searchFields"
        @search="handleQuery"
        @reset="resetQuery"
      />
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增商品
        </el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>导出Excel
        </el-button>
      </div>

      <Table
        :data="list"
        :columns="columns"
        :loading="loading"
        show-selection
        @selection-change="handleSelectionChange"
      >
        <template #stockQuantity="{ row }">
          <el-tag
            :type="row.stockQuantity > row.safeStock * 2 ? 'success' : row.stockQuantity > row.safeStock ? 'warning' : 'danger'"
            size="small"
          >
            {{ row.stockQuantity }}
          </el-tag>
        </template>
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </Table>

      <Pagination
        v-model:modelValue="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '新增商品'"
      width="600px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="商品编码" prop="sku">
          <el-input v-model="form.sku" placeholder="请输入商品编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="初始库存" prop="stockQuantity" v-if="!isEdit">
          <el-input-number v-model="form.stockQuantity" :min="0" style="width: 200px" />
        </el-form-item>
        <el-form-item label="安全库存" prop="safeStock">
          <el-input-number v-model="form.safeStock" :min="0" style="width: 200px" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">

defineOptions({ name: 'ProductList' })
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import SearchForm, { type SearchField } from '@/components/SearchForm/index.vue'
import Table, { type TableColumn } from '@/components/Table/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import { getProductList, createProduct, updateProduct, deleteProduct, batchDeleteProducts, exportProducts } from '@/api/product'
import type { Product, ProductQuery } from '@/api/types/product'
import { downloadFile } from '@/utils'

const router = useRouter()

const loading = ref(false)
const submitLoading = ref(false)
const list = ref<Product[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const queryParams = reactive<ProductQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  categoryId: undefined,
  stockStatus: ''
})

const searchFields: SearchField[] = [
  { prop: 'keyword', label: '关键词', type: 'input', placeholder: '商品编码/名称', inputWidth: '200px' },
  { prop: 'stockStatus', label: '库存状态', type: 'select', inputWidth: '140px', options: [
    { label: '全部', value: '' },
    { label: '充足', value: 'sufficient' },
    { label: '预警', value: 'warning' },
    { label: '缺货', value: 'shortage' }
  ]}
]

const columns: TableColumn[] = [
  { prop: 'sku', label: '商品编码', minWidth: '120' },
  { prop: 'name', label: '商品名称', minWidth: '140' },
  { prop: 'unit', label: '单位', width: '80' },
  { prop: 'stockQuantity', label: '库存数量', width: '100', slot: 'stockQuantity' },
  { prop: 'safeStock', label: '安全库存', width: '100' },
  { prop: 'remark', label: '备注', minWidth: '150', showOverflowTooltip: true },
  { prop: 'createdAt', label: '创建时间', width: '160' },
  { prop: '', label: '操作', slot: 'operation', width: '220' }
]

const initialForm = {
  id: undefined as number | undefined,
  sku: '',
  name: '',
  unit: '',
  stockQuantity: 0,
  safeStock: 0,
  remark: ''
}

const form = reactive({ ...initialForm })

const formRules: FormRules = {
  sku: [{ required: true, message: '请输入商品编码', trigger: 'blur' }, { max: 64, message: '不超过64个字符', trigger: 'blur' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }, { max: 128, message: '不超过128个字符', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }, { max: 32, message: '不超过32个字符', trigger: 'blur' }],
  stockQuantity: [{ required: true, message: '请输入初始库存', trigger: 'blur' }],
  safeStock: [{ required: true, message: '请输入安全库存', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getProductList({ ...queryParams })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.stockStatus = ''
  queryParams.categoryId = undefined
  queryParams.pageNum = 1
  getList()
}

const handleSelectionChange = (selection: Product[]) => {
  selectedIds.value = selection.map((item) => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, initialForm)
  dialogVisible.value = true
}

const handleEdit = (row: Product) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDetail = (row: Product) => {
  router.push(`/product/detail/${row.id}`)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value && form.id) {
        await updateProduct(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createProduct(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      getList()
    } catch (e: any) {
      ElMessage.error(e?.message || '操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row: Product) => {
  ElMessageBox.confirm(`确定删除商品「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteProduct(row.id)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个商品吗？`, '批量删除', { type: 'warning' })
    .then(async () => {
      await batchDeleteProducts(selectedIds.value)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

const handleExport = async () => {
  try {
    const res = await exportProducts(queryParams)
    downloadFile(res as any, '商品数据.xlsx')
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.product-list {
  .search-card {
    margin-bottom: 16px;
  }

  .table-header {
    margin-bottom: 16px;
  }
}
</style>
