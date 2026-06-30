<template>
  <div class="customer-page">
    <el-card class="search-card">
      <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增客户
        </el-button>
      </div>

      <Table :data="list" :columns="columns" :loading="loading">
        <template #status="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
            {{ row.status === 'active' ? '启用' : '禁用' }}
          </el-tag>
        </template>
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </Table>

      <Pagination v-model:modelValue="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="客户编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系人">
              <el-input v-model="form.contact" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="active">启用</el-radio>
            <el-radio label="inactive">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import SearchForm, { type SearchField } from '@/components/SearchForm/index.vue'
import Table, { type TableColumn } from '@/components/Table/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import { getCustomerList, createCustomer, updateCustomer, deleteCustomer } from '@/api/system'
import type { Customer } from '@/api/types/system'

const loading = ref(false)
const submitLoading = ref(false)
const list = ref<Customer[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const searchFields: SearchField[] = [
  { prop: 'keyword', label: '关键词', type: 'input', placeholder: '编码/名称/联系人', inputWidth: '200px' }
]

const columns: TableColumn[] = [
  { prop: 'code', label: '客户编码', minWidth: '110' },
  { prop: 'name', label: '客户名称', minWidth: '130' },
  { prop: 'contact', label: '联系人', width: '100' },
  { prop: 'phone', label: '联系电话', width: '120' },
  { prop: 'address', label: '地址', minWidth: '150', showOverflowTooltip: true },
  { prop: 'status', label: '状态', width: '80', slot: 'status' },
  { prop: 'createdAt', label: '创建时间', width: '160' },
  { prop: '', label: '操作', width: '150', slot: 'operation' }
]

const initialForm = { id: undefined as number | undefined, code: '', name: '', contact: '', phone: '', address: '', email: '', status: 'active' }
const form = reactive({ ...initialForm })

const formRules: FormRules = {
  code: [{ required: true, message: '请输入客户编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getCustomerList({ ...queryParams })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { list.value = [] } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.keyword = ''; queryParams.pageNum = 1; getList() }

const handleAdd = () => { isEdit.value = false; Object.assign(form, initialForm); dialogVisible.value = true }
const handleEdit = (row: Customer) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value && form.id) { await updateCustomer(form.id, form); ElMessage.success('更新成功') }
      else { await createCustomer(form); ElMessage.success('新增成功') }
      dialogVisible.value = false; getList()
    } catch (e: any) { ElMessage.error(e?.message || '操作失败') } finally { submitLoading.value = false }
  })
}

const handleDelete = (row: Customer) => {
  ElMessageBox.confirm(`确定删除客户「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteCustomer(row.id); ElMessage.success('删除成功'); getList() }).catch(() => {})
}

onMounted(() => { getList() })
</script>

<style scoped lang="scss">
.customer-page { .search-card { margin-bottom: 16px; } .table-header { margin-bottom: 16px; } }
</style>
