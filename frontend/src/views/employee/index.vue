<template>
  <div class="employee-page">
    <el-card class="search-card">
      <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增员工
        </el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>

      <Table :data="list" :columns="columns" :loading="loading" show-selection @selection-change="handleSelectionChange">
        <template #gender="{ row }">
          <el-tag :type="row.gender === 'male' ? 'primary' : 'danger'" size="small">
            {{ row.gender === 'male' ? '男' : '女' }}
          </el-tag>
        </template>
        <template #status="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : row.status === 'leave' ? 'warning' : 'danger'" size="small">
            {{ row.status === 'active' ? '在职' : row.status === 'leave' ? '请假' : '离职' }}
          </el-tag>
        </template>
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="info" link size="small" @click="handleDetail(row)">详情</el-button>
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
      :title="isEdit ? '编辑员工' : '新增员工'"
      width="650px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="工号" prop="code">
              <el-input v-model="form.code" placeholder="请输入工号" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="department">
              <el-select v-model="form.department" placeholder="请选择部门" style="width: 100%">
                <el-option label="仓储部" value="仓储部" />
                <el-option label="物流部" value="物流部" />
                <el-option label="质检部" value="质检部" />
                <el-option label="采购部" value="采购部" />
                <el-option label="行政部" value="行政部" />
                <el-option label="财务部" value="财务部" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker
                v-model="form.hireDate"
                type="date"
                placeholder="请选择入职日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资(元)" prop="salary">
              <el-input-number v-model="form.salary" :min="0" :precision="2" style="width: 100%" placeholder="请输入薪资" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="active">在职</el-radio>
            <el-radio label="leave">请假</el-radio>
            <el-radio label="resigned">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="员工详情" width="600px">
      <el-descriptions :column="2" border v-if="currentEmployee">
        <el-descriptions-item label="工号">{{ currentEmployee.code }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentEmployee.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentEmployee.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentEmployee.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentEmployee.department || '-' }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{ currentEmployee.position || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ currentEmployee.hireDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="薪资">¥{{ currentEmployee.salary?.toFixed(2) || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentEmployee.status === 'active' ? 'success' : currentEmployee.status === 'leave' ? 'warning' : 'danger'" size="small">
            {{ currentEmployee.status === 'active' ? '在职' : currentEmployee.status === 'leave' ? '请假' : '离职' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentEmployee.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentEmployee.createdAt || '-' }}</el-descriptions-item>
      </el-descriptions>
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
import { getEmployeeList, createEmployee, updateEmployee, deleteEmployee, batchDeleteEmployees } from '@/api/system'
import type { Employee } from '@/api/types/system'

const loading = ref(false)
const submitLoading = ref(false)
const list = ref<Employee[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const currentEmployee = ref<Employee | null>(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  department: '',
  status: ''
})

const searchFields: SearchField[] = [
  { prop: 'keyword', label: '关键词', type: 'input', placeholder: '工号/姓名/手机号', inputWidth: '200px' },
  { prop: 'department', label: '部门', type: 'select', inputWidth: '130px', options: [
    { label: '全部', value: '' },
    { label: '仓储部', value: '仓储部' },
    { label: '物流部', value: '物流部' },
    { label: '质检部', value: '质检部' },
    { label: '采购部', value: '采购部' },
    { label: '行政部', value: '行政部' },
    { label: '财务部', value: '财务部' }
  ]},
  { prop: 'status', label: '状态', type: 'select', inputWidth: '110px', options: [
    { label: '全部', value: '' },
    { label: '在职', value: 'active' },
    { label: '请假', value: 'leave' },
    { label: '离职', value: 'resigned' }
  ]}
]

const columns: TableColumn[] = [
  { prop: 'code', label: '工号', width: '100' },
  { prop: 'name', label: '姓名', width: '80' },
  { prop: 'gender', label: '性别', width: '70', slot: 'gender' },
  { prop: 'phone', label: '手机号', width: '120' },
  { prop: 'department', label: '部门', width: '90' },
  { prop: 'position', label: '职位', width: '100' },
  { prop: 'hireDate', label: '入职日期', width: '110' },
  { prop: 'status', label: '状态', width: '80', slot: 'status' },
  { prop: '', label: '操作', width: '200', slot: 'operation' }
]

const initialForm = {
  id: undefined as number | undefined,
  code: '',
  name: '',
  gender: 'male',
  phone: '',
  department: '',
  position: '',
  hireDate: '',
  salary: 0,
  status: 'active',
  remark: ''
}

const form = reactive({ ...initialForm })

const formRules: FormRules = {
  code: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getEmployeeList({ ...queryParams })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    list.value = []
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }

const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.department = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  getList()
}

const handleSelectionChange = (selection: Employee[]) => {
  selectedIds.value = selection.map((item) => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, initialForm)
  dialogVisible.value = true
}

const handleEdit = (row: Employee) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDetail = (row: Employee) => {
  currentEmployee.value = row
  detailVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value && form.id) {
        await updateEmployee(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createEmployee(form)
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

const handleDelete = (row: Employee) => {
  ElMessageBox.confirm(`确定删除员工「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteEmployee(row.id)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的员工')
    return
  }
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 名员工吗？`, '批量删除', { type: 'warning' })
    .then(async () => {
      await batchDeleteEmployees(selectedIds.value)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

onMounted(() => { getList() })
</script>

<style scoped lang="scss">
.employee-page {
  .search-card { margin-bottom: 16px; }
  .table-header { margin-bottom: 16px; }
}
</style>
