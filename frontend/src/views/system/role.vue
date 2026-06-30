<template>
  <div class="system-role">
    <el-card class="search-card">
      <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增角色
        </el-button>
      </div>

      <Table :data="list" :columns="columns" :loading="loading">
        <template #operation="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link size="small" @click="handlePermission(row)">权限</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </Table>

      <Pagination v-model:modelValue="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permVisible" title="权限配置" width="450px" destroy-on-close>
      <el-tree
        ref="treeRef"
        :data="permTree"
        show-checkbox
        node-key="id"
        default-expand-all
        :default-checked-keys="checkedPerms"
        :props="{ label: 'name' }"
      />
      <template #footer>
        <el-button @click="permVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermission">保存</el-button>
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
import { getRoleList, createRole, updateRole, deleteRole, getPermissionTree } from '@/api/system'
import type { SysRole, MenuItem } from '@/api/types/system'

const loading = ref(false)
const submitLoading = ref(false)
const list = ref<SysRole[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const permVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const treeRef = ref()
const permTree = ref<MenuItem[]>([])
const checkedPerms = ref<number[]>([])
const currentRole = ref<SysRole | null>(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const searchFields: SearchField[] = [
  { prop: 'keyword', label: '关键词', type: 'input', placeholder: '编码/名称', inputWidth: '200px' }
]

const columns: TableColumn[] = [
  { prop: 'code', label: '角色编码', minWidth: '120' },
  { prop: 'name', label: '角色名称', minWidth: '120' },
  { prop: 'description', label: '描述', minWidth: '200', showOverflowTooltip: true },
  { prop: 'createdAt', label: '创建时间', width: '160' },
  { prop: '', label: '操作', width: '200', slot: 'operation' }
]

const initialForm = { id: undefined as number | undefined, code: '', name: '', description: '' }
const form = reactive({ ...initialForm })

const formRules: FormRules = {
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getRoleList({ ...queryParams })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { list.value = [] } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.keyword = ''; queryParams.pageNum = 1; getList() }

const handleAdd = () => { isEdit.value = false; Object.assign(form, initialForm); dialogVisible.value = true }
const handleEdit = (row: SysRole) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value && form.id) { await updateRole(form.id, form); ElMessage.success('更新成功') }
      else { await createRole(form); ElMessage.success('新增成功') }
      dialogVisible.value = false; getList()
    } catch (e: any) { ElMessage.error(e?.message || '操作失败') } finally { submitLoading.value = false }
  })
}

const handleDelete = (row: SysRole) => {
  ElMessageBox.confirm(`确定删除角色「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteRole(row.id); ElMessage.success('删除成功'); getList() }).catch(() => {})
}

const handlePermission = async (row: SysRole) => {
  currentRole.value = row
  checkedPerms.value = row.permissions || []
  try {
    const res = await getPermissionTree()
    permTree.value = res.data || []
  } catch (e) { permTree.value = [] }
  permVisible.value = true
}

const savePermission = async () => {
  const checkedKeys = treeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = treeRef.value?.getHalfCheckedKeys() || []
  try {
    await updateRole(currentRole.value!.id, { permissions: checkedKeys } as any)
    ElMessage.success('权限配置已保存')
    permVisible.value = false
    getList()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  }
}

onMounted(() => { getList() })
</script>

<style scoped lang="scss">
.system-role { .search-card { margin-bottom: 16px; } .table-header { margin-bottom: 16px; } }
</style>
