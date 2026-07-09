<template>
  <div class="system-setting">
    <el-card>
      <template #header><span>基本设置</span></template>
      <el-form :model="form" label-width="120px">
        <el-form-item label="系统名称">
          <el-input v-model="form.systemName" placeholder="请输入系统名称" style="width: 400px" />
        </el-form-item>
        <el-form-item label="系统Logo">
          <el-upload action="#" :show-file-list="false">
            <el-button type="primary">
              <el-icon><Upload /></el-icon>上传Logo
            </el-button>
          </el-upload>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <template #header><span>安全设置</span></template>
      <el-form :model="form" label-width="140px">
        <el-form-item label="密码最小长度">
          <el-input-number v-model="form.passwordMinLength" :min="6" :max="20" />
        </el-form-item>
        <el-form-item label="登录最大重试次数">
          <el-input-number v-model="form.loginMaxRetry" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="会话超时(分钟)">
          <el-input-number v-model="form.sessionTimeout" :min="5" :max="1440" />
        </el-form-item>
      </el-form>
    </el-card>

    <div class="setting-footer">
      <el-button type="primary" :loading="saveLoading" @click="handleSave">保存设置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">

defineOptions({ name: 'SystemSetting' })
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSysSetting, updateSysSetting } from '@/api/system'

const saveLoading = ref(false)

const form = reactive({
  systemName: 'WMS管理系统',
  passwordMinLength: 6,
  loginMaxRetry: 5,
  sessionTimeout: 30
})

onMounted(async () => {
  try {
    const res = await getSysSetting()
    if (res.data) Object.assign(form, res.data)
  } catch (e) { /* use defaults */ }
})

const handleSave = async () => {
  saveLoading.value = true
  try {
    await updateSysSetting(form)
    ElMessage.success('设置保存成功')
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    saveLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.system-setting {
  .setting-footer {
    margin-top: 24px;
    text-align: center;
  }
}
</style>
