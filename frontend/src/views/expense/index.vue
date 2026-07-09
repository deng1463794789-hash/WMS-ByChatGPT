<template>
  <div class="expense-page">
    <el-card class="search-card">
      <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增报销单
        </el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>

      <Table :data="list" :columns="columns" :loading="loading" show-selection @selection-change="handleSelectionChange">
        <template #amount="{ row }">
          <span style="color: #F56C6C; font-weight: 600">¥{{ row.amount?.toFixed(2) }}</span>
        </template>
        <template #type="{ row }">
          <el-tag :type="typeMap[row.type]?.tag || 'info'" size="small">
            {{ typeMap[row.type]?.label || row.type }}
          </el-tag>
        </template>
        <template #status="{ row }">
          <el-tag :type="statusMap[row.status]?.tag || 'info'" size="small">
            {{ statusMap[row.status]?.label || row.status }}
          </el-tag>
        </template>
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="info" link size="small" @click="handleDetail(row)">详情</el-button>
          <el-button
            v-if="row.status === 'pending'"
            type="success"
            link
            size="small"
            @click="handleApprove(row)"
          >审批</el-button>
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
      :title="isEdit ? '编辑报销单' : '新增报销单'"
      width="650px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="报销类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择报销类型" style="width: 100%">
                <el-option label="差旅费" value="travel" />
                <el-option label="办公费" value="office" />
                <el-option label="交通费" value="transport" />
                <el-option label="餐饮费" value="meal" />
                <el-option label="通讯费" value="phone" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报销金额" prop="amount">
              <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" placeholder="请输入金额" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicant">
              <el-input v-model="form.applicant" placeholder="请输入申请人" />
            </el-form-item>
          </el-col>
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
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发生日期" prop="expenseDate">
              <el-date-picker
                v-model="form.expenseDate"
                type="date"
                placeholder="请选择日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发票号码">
              <el-input v-model="form.invoiceNo" placeholder="请输入发票号码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="报销事由" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请详细描述报销事由" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="报销单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentExpense">
        <el-descriptions-item label="报销编号">{{ currentExpense.expenseNo }}</el-descriptions-item>
        <el-descriptions-item label="报销类型">
          <el-tag :type="typeMap[currentExpense.type]?.tag || 'info'" size="small">
            {{ typeMap[currentExpense.type]?.label }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报销金额" :span="2">
          <span style="color: #F56C6C; font-size: 20px; font-weight: 700">¥{{ currentExpense.amount?.toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentExpense.applicant }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentExpense.department || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发生日期">{{ currentExpense.expenseDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发票号码">{{ currentExpense.invoiceNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="事由" :span="2">{{ currentExpense.reason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentExpense.approver || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ currentExpense.approveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusMap[currentExpense.status]?.tag || 'info'" size="small">
            {{ statusMap[currentExpense.status]?.label }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="驳回原因" v-if="currentExpense.status === 'rejected'">{{ currentExpense.rejectReason || '-' }}</el-descriptions-item>
        <el-descriptions-item v-else label="备注">{{ currentExpense.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentExpense.createdAt || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="approveVisible" title="审批报销单" width="450px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="报销编号">
          <span>{{ approveForm.expenseNo }}</span>
        </el-form-item>
        <el-form-item label="报销金额">
          <span style="color: #F56C6C; font-weight: 600">¥{{ approveForm.amount?.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="审批结果" prop="action">
          <el-radio-group v-model="approveForm.action">
            <el-radio label="approved">通过</el-radio>
            <el-radio label="rejected">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="驳回原因" v-if="approveForm.action === 'rejected'" prop="rejectReason">
          <el-input v-model="approveForm.rejectReason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.opinion" type="textarea" :rows="2" placeholder="审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprove">确认审批</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">

defineOptions({ name: 'ExpenseList' })
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import SearchForm, { type SearchField } from '@/components/SearchForm/index.vue'
import Table, { type TableColumn } from '@/components/Table/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import { getExpenseList, createExpense, updateExpense, deleteExpense, batchDeleteExpenses, approveExpense } from '@/api/system'
import type { Expense } from '@/api/types/system'

const loading = ref(false)
const submitLoading = ref(false)
const list = ref<Expense[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approveVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const currentExpense = ref<Expense | null>(null)

const typeMap: Record<string, { label: string; tag: string }> = {
  travel: { label: '差旅费', tag: 'primary' },
  office: { label: '办公费', tag: 'success' },
  transport: { label: '交通费', tag: 'warning' },
  meal: { label: '餐饮费', tag: 'danger' },
  phone: { label: '通讯费', tag: 'info' },
  other: { label: '其他', tag: '' }
}

const statusMap: Record<string, { label: string; tag: string }> = {
  pending: { label: '待审批', tag: 'warning' },
  approved: { label: '已通过', tag: 'success' },
  rejected: { label: '已驳回', tag: 'danger' }
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  type: '',
  status: ''
})

const searchFields: SearchField[] = [
  { prop: 'keyword', label: '关键词', type: 'input', placeholder: '编号/申请人', inputWidth: '180px' },
  { prop: 'type', label: '类型', type: 'select', inputWidth: '120px', options: [
    { label: '全部', value: '' },
    { label: '差旅费', value: 'travel' },
    { label: '办公费', value: 'office' },
    { label: '交通费', value: 'transport' },
    { label: '餐饮费', value: 'meal' },
    { label: '通讯费', value: 'phone' },
    { label: '其他', value: 'other' }
  ]},
  { prop: 'status', label: '状态', type: 'select', inputWidth: '110px', options: [
    { label: '全部', value: '' },
    { label: '待审批', value: 'pending' },
    { label: '已通过', value: 'approved' },
    { label: '已驳回', value: 'rejected' }
  ]}
]

const columns: TableColumn[] = [
  { prop: 'expenseNo', label: '报销编号', minWidth: '140' },
  { prop: 'type', label: '报销类型', width: '90', slot: 'type' },
  { prop: 'amount', label: '报销金额', width: '110', slot: 'amount' },
  { prop: 'applicant', label: '申请人', width: '80' },
  { prop: 'department', label: '部门', width: '80' },
  { prop: 'expenseDate', label: '发生日期', width: '110' },
  { prop: 'reason', label: '事由', minWidth: '150', showOverflowTooltip: true },
  { prop: 'status', label: '状态', width: '80', slot: 'status' },
  { prop: '', label: '操作', width: '220', slot: 'operation' }
]

const initialForm = {
  id: undefined as number | undefined,
  type: 'travel',
  amount: 0,
  applicant: '',
  department: '',
  expenseDate: '',
  invoiceNo: '',
  reason: '',
  remark: ''
}

const form = reactive({ ...initialForm })

const formRules: FormRules = {
  type: [{ required: true, message: '请选择报销类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入报销金额', trigger: 'blur' }],
  applicant: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  expenseDate: [{ required: true, message: '请选择发生日期', trigger: 'change' }],
  reason: [{ required: true, message: '请填写报销事由', trigger: 'blur' }]
}

const approveForm = reactive({
  expenseNo: '',
  amount: 0,
  action: 'approved',
  rejectReason: '',
  opinion: ''
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getExpenseList({ ...queryParams })
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
  queryParams.type = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  getList()
}

const handleSelectionChange = (selection: Expense[]) => {
  selectedIds.value = selection.map((item) => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, initialForm)
  dialogVisible.value = true
}

const handleEdit = (row: Expense) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDetail = (row: Expense) => {
  currentExpense.value = row
  detailVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value && form.id) {
        await updateExpense(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createExpense(form)
        ElMessage.success('提交成功')
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

const handleDelete = (row: Expense) => {
  ElMessageBox.confirm(`确定删除报销单「${row.expenseNo}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteExpense(row.id)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的报销单')
    return
  }
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 条报销单吗？`, '批量删除', { type: 'warning' })
    .then(async () => {
      await batchDeleteExpenses(selectedIds.value)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

const handleApprove = (row: Expense) => {
  approveForm.expenseNo = row.expenseNo
  approveForm.amount = row.amount
  approveForm.action = 'approved'
  approveForm.rejectReason = ''
  approveForm.opinion = ''
  approveVisible.value = true
  currentExpense.value = row
}

const submitApprove = async () => {
  try {
    await approveExpense(currentExpense.value!.id, {
      action: approveForm.action,
      rejectReason: approveForm.rejectReason,
      opinion: approveForm.opinion
    })
    ElMessage.success(approveForm.action === 'approved' ? '审批通过' : '已驳回')
    approveVisible.value = false
    getList()
  } catch (e: any) {
    ElMessage.error(e?.message || '审批失败')
  }
}

onMounted(() => { getList() })
</script>

<style scoped lang="scss">
.expense-page {
  .search-card { margin-bottom: 16px; }
  .table-header { margin-bottom: 16px; }
}
</style>
