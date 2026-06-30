<template>
  <div class="inventory-stock">
    <el-card class="search-card">
      <SearchForm
        :model="queryParams"
        :fields="searchFields"
        @search="handleQuery"
        @reset="resetQuery"
      />
    </el-card>

    <el-card class="table-card">
      <Table :data="list" :columns="columns" :loading="loading">
        <template #stockQuantity="{ row }">
          <el-tag
            :type="row.stockQuantity > row.safeStock * 2 ? 'success' : row.stockQuantity > row.safeStock ? 'warning' : 'danger'"
            size="small"
          >
            {{ row.stockQuantity }}
          </el-tag>
        </template>
        <template #status="{ row }">
          <el-tag
            :type="row.stockQuantity > row.safeStock * 2 ? 'success' : row.stockQuantity > row.safeStock ? 'warning' : 'danger'"
            size="small"
          >
            {{ row.stockQuantity > row.safeStock * 2 ? '充足' : row.stockQuantity > row.safeStock ? '预警' : '缺货' }}
          </el-tag>
        </template>
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleDetail(row)">查看明细</el-button>
          <el-button type="warning" link size="small" @click="handleAdjust(row)">调整库存</el-button>
        </template>
      </Table>

      <Pagination
        v-model:modelValue="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </el-card>

    <el-dialog v-model="adjustVisible" title="调整库存" width="400px" destroy-on-close>
      <el-form :model="adjustForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ adjustForm.productName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ adjustForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="调整后库存" prop="newStock">
          <el-input-number v-model="adjustForm.newStock" :min="0" style="width: 200px" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="adjustForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" :loading="adjustLoading" @click="confirmAdjust">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import SearchForm, { type SearchField } from '@/components/SearchForm/index.vue'
import Table, { type TableColumn } from '@/components/Table/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import { getProductList, updateProductStock } from '@/api/product'
import type { Product, ProductQuery } from '@/api/types/product'

const loading = ref(false)
const list = ref<Product[]>([])
const total = ref(0)
const adjustVisible = ref(false)
const adjustLoading = ref(false)
const currentProduct = ref<Product | null>(null)

const queryParams = reactive<ProductQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  stockStatus: ''
})

const searchFields: SearchField[] = [
  { prop: 'keyword', label: '商品编码/名称', type: 'input', inputWidth: '200px' },
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
  { prop: 'stockQuantity', label: '当前库存', width: '100', slot: 'stockQuantity' },
  { prop: 'safeStock', label: '安全库存', width: '100' },
  { prop: '', label: '库存状态', width: '100', slot: 'status' },
  { prop: '', label: '操作', width: '180', slot: 'operation' }
]

const adjustForm = reactive({
  productName: '',
  currentStock: 0,
  newStock: 0,
  remark: ''
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getProductList({ ...queryParams })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    list.value = []
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
  queryParams.pageNum = 1
  getList()
}

const handleDetail = (row: Product) => {
  ElMessage.info(`查看商品明细: ${row.name}`)
}

const handleAdjust = (row: Product) => {
  currentProduct.value = row
  adjustForm.productName = row.name
  adjustForm.currentStock = row.stockQuantity
  adjustForm.newStock = row.stockQuantity
  adjustForm.remark = ''
  adjustVisible.value = true
}

const confirmAdjust = async () => {
  if (!currentProduct.value) return
  adjustLoading.value = true
  try {
    await updateProductStock(currentProduct.value.id, {
      stockQuantity: adjustForm.newStock,
      remark: adjustForm.remark
    })
    ElMessage.success('库存调整成功')
    adjustVisible.value = false
    getList()
  } catch (e: any) {
    ElMessage.error(e?.message || '库存调整失败')
  } finally {
    adjustLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.inventory-stock {
  .search-card {
    margin-bottom: 16px;
  }
}
</style>
