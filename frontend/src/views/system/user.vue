<template>
  <div class="system-user">
    <el-card class="search-card">
      <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增用户
        </el-button>
      </div>

      <Table :data="list" :columns="columns" :loading="loading">
        <template #status="{ row }">
          <el-switch
            :model-value="row.status"
            :active-value="'active'"
            :inactive-value="'inactive'"
            @change="(val: string | number | boolean) => handleStatusChange(row, String(val))"
          />
        </template>
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link size="small" @click="handleResetPwd(row)">重置密码</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </Table>

      <Pagination v-model:modelValue="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="角色">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="r in roleList" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
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
import { getUserList, createUser, updateUser, deleteUser, resetPassword, getAllRoles } from '@/api/system'
import type { SysUser, SysRole } from '@/api/types/system'

const loading = ref(false)
const submitLoading = ref(false)
const list = ref<SysUser[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const roleList = ref<SysRole[]>([])

const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '', status: '' })

const searchFields: SearchField[] = [
  { prop: 'keyword', label: '关键词', type: 'input', placeholder: '用户名/姓名', inputWidth: '200px' },
  { prop: 'status', label: '状态', type: 'select', inputWidth: '120px', options: [
    { label: '全部', value: '' }, { label: '启用', value: 'active' }, { label: '禁用', value: 'inactive' }
  ]}
]

const columns: TableColumn[] = [
  { prop: 'username', label: '用户名', minWidth: '110' },
  { prop: 'realName', label: '姓名', width: '100' },
  { prop: 'phone', label: '手机号', width: '120' },
  { prop: 'roleName', label: '角色', width: '100' },
  { prop: 'status', label: '状态', width: '80', slot: 'status' },
  { prop: 'createdAt', label: '创建时间', width: '160' },
  { prop: '', label: '操作', width: '210', slot: 'operation' }
]

const initialForm: Record<string, any> = { id: undefined, username: '', password: '', realName: '', phone: '', email: '', roleId: undefined, status: 'active' }
const form = reactive({ ...initialForm })

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '不少于6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getUserList({ ...queryParams })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { list.value = [] } finally { loading.value = false }
}

const fetchRoles = async () => {
  try {
    const res = await getAllRoles()
    roleList.value = res.data || []
  } catch (e) { roleList.value = [] }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.keyword = ''; queryParams.status = ''; queryParams.pageNum = 1; getList() }

const handleAdd = () => { isEdit.value = false; Object.assign(form, initialForm); form.password = ''; fetchRoles(); dialogVisible.value = true }
const handleEdit = (row: SysUser) => { isEdit.value = true; Object.assign(form, row); fetchRoles(); dialogVisible.value = true }

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value && form.id) { await updateUser(form.id, form); ElMessage.success('更新成功') }
      else { await createUser(form); ElMessage.success('新增成功') }
      dialogVisible.value = false; getList()
    } catch (e: any) { ElMessage.error(e?.message || '操作失败') } finally { submitLoading.value = false }
  })
}

const handleDelete = (row: SysUser) => {
  ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteUser(row.id); ElMessage.success('删除成功'); getList() }).catch(() => {})
}

const handleResetPwd = (row: SysUser) => {
  ElMessageBox.confirm(`确定重置用户「${row.username}」的密码吗？`, '提示', { type: 'warning' })
    .then(async () => { await resetPassword(row.id); ElMessage.success('密码已重置') }).catch(() => {})
}

const handleStatusChange = async (row: SysUser, val: string) => {
  try {
    await updateUser(row.id, { status: val })
    row.status = val
    ElMessage.success(`用户已${val === 'active' ? '启用' : '禁用'}`)
  } catch (e: any) {
    ElMessage.error(e?.message || '状态变更失败')
  }
}

onMounted(() => { getList() })
</script>

<style scoped lang="scss">
.system-user { .search-card { margin-bottom: 16px; } .table-header { margin-bottom: 16px; } }
</style>
