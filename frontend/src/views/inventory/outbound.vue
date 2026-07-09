<template>
  <div class="inventory-outbound">
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
          <el-icon><Plus /></el-icon>新增出库单
        </el-button>
      </div>

      <Table :data="list" :columns="columns" :loading="loading">
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleDetail(row)">查看详情</el-button>
          <el-button type="success" link size="small" @click="handlePrint(row)">打印</el-button>
        </template>
      </Table>

      <Pagination
        v-model:modelValue="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增出库单" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出库类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择出库类型" style="width: 100%">
                <el-option label="销售出库" value="sale" />
                <el-option label="退货出库" value="return" />
                <el-option label="调拨出库" value="transfer" />
                <el-option label="其他出库" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出库时间" prop="outboundTime">
              <el-date-picker v-model="form.outboundTime" type="datetime" placeholder="选择时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>

        <el-divider content-position="left">商品明细</el-divider>

        <el-button type="primary" size="small" @click="addItem" style="margin-bottom: 12px;">
          <el-icon><Plus /></el-icon>添加商品
        </el-button>

        <el-table :data="form.items" border size="small">
          <el-table-column label="商品" min-width="160">
            <template #default="{ row: item }">
              <el-select v-model="item.productId" placeholder="选择商品" filterable style="width: 100%" size="small">
                <el-option
                  v-for="p in productList"
                  :key="p.id"
                  :label="`${p.sku} - ${p.name}`"
                  :value="p.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="120">
            <template #default="{ row: item }">
              <el-input-number v-model="item.quantity" :min="1" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="160">
            <template #default="{ row: item }">
              <el-input v-model="item.remark" size="small" placeholder="备注" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" @click="removeItem($index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">

defineOptions({ name: 'InventoryOutbound' })
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import SearchForm, { type SearchField } from '@/components/SearchForm/index.vue'
import Table, { type TableColumn } from '@/components/Table/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import { getOutboundList, createOutbound, getProductList } from '@/api/product'
import type { OutboundRecord, OutboundQuery, Product } from '@/api/types/product'

const loading = ref(false)
const submitLoading = ref(false)
const list = ref<OutboundRecord[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const productList = ref<Product[]>([])

const queryParams = reactive<OutboundQuery>({
  pageNum: 1,
  pageSize: 10,
  outboundNo: '',
  type: ''
})

const searchFields: SearchField[] = [
  { prop: 'outboundNo', label: '出库单号', type: 'input', inputWidth: '200px' },
  { prop: 'type', label: '出库类型', type: 'select', inputWidth: '140px', options: [
    { label: '全部', value: '' },
    { label: '销售出库', value: 'sale' },
    { label: '退货出库', value: 'return' },
    { label: '调拨出库', value: 'transfer' },
    { label: '其他出库', value: 'other' }
  ]}
]

const columns: TableColumn[] = [
  { prop: 'outboundNo', label: '出库单号', minWidth: '140' },
  { prop: 'type', label: '出库类型', width: '100' },
  { prop: 'productCount', label: '商品数量', width: '100' },
  { prop: 'operator', label: '操作人', width: '100' },
  { prop: 'outboundTime', label: '出库时间', width: '160' },
  { prop: 'remark', label: '备注', minWidth: '150', showOverflowTooltip: true },
  { prop: '', label: '操作', width: '160', slot: 'operation' }
]

const form = reactive({
  type: 'sale',
  outboundTime: '',
  remark: '',
  items: [] as Array<{ productId: number | null; productName?: string; sku?: string; quantity: number; remark: string }>
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getOutboundList({ ...queryParams })
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
  queryParams.outboundNo = ''
  queryParams.type = ''
  queryParams.pageNum = 1
  getList()
}

const handleAdd = async () => {
  form.type = 'sale'
  form.outboundTime = ''
  form.remark = ''
  form.items = [{ productId: null, quantity: 1, remark: '' }]
  try {
    const res = await getProductList({ pageNum: 1, pageSize: 999 })
    productList.value = res.data?.records || []
  } catch (e) {
    productList.value = []
  }
  dialogVisible.value = true
}

const addItem = () => {
  form.items.push({ productId: null, quantity: 1, remark: '' })
}

const removeItem = (index: number) => {
  form.items.splice(index, 1)
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    await createOutbound({
      type: form.type,
      outboundTime: form.outboundTime,
      remark: form.remark,
      items: form.items
        .filter((i) => i.productId !== null)
        .map((i) => ({
          productId: i.productId as number,
          quantity: i.quantity,
          remark: i.remark
        }))
    })
    ElMessage.success('出库单创建成功')
    dialogVisible.value = false
    getList()
  } catch (e: any) {
    ElMessage.error(e?.message || '创建失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDetail = (row: OutboundRecord) => {
  ElMessage.info(`查看出库单详情: ${row.outboundNo}`)
}

const handlePrint = (row: OutboundRecord) => {
  ElMessage.info(`打印出库单: ${row.outboundNo}`)
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.inventory-outbound {
  .search-card {
    margin-bottom: 16px;
  }

  .table-header {
    margin-bottom: 16px;
  }
}
</style>
