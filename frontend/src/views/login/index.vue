<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="bg-circle circle-1" />
      <div class="bg-circle circle-2" />
      <div class="bg-circle circle-3" />
      <div class="bg-circle circle-4" />
    </div>
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon :size="36" color="#fff"><Box /></el-icon>
        </div>
        <h2>WMS管理系统</h2>
        <p>仓储管理系统登录</p>
      </div>
      <el-form ref="formRef" :model="loginForm" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password>
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-divider>
            <span style="color: #909399; font-size: 13px;">后端未启动时可使用</span>
          </el-divider>
          <el-button type="success" class="login-btn mock-btn" @click="handleMockLogin">
            <el-icon><Connection /></el-icon>体验模式（Mock 数据预览）
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>Copyright &copy; {{ new Date().getFullYear() }} WMS管理系统</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/modules/user'
import { enableMock } from '@/mock'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: 'admin123',
  remember: false
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.login({
        username: loginForm.username,
        password: loginForm.password,
        remember: loginForm.remember
      })
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
      ElMessage.success('登录成功')
    } catch (error: any) {
      ElMessage.error(error?.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}

const handleMockLogin = async () => {
  enableMock()
  try {
    await userStore.login({
      username: 'admin',
      password: 'admin123',
      remember: false
    })
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
    ElMessage.success('已进入体验模式，所有数据均为 Mock 数据')
  } catch {
    ElMessage.error('Mock 登录失败')
  }
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;

  .bg-circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.06);

    &.circle-1 {
      width: 400px; height: 400px;
      top: -120px; right: -80px;
      animation: float 8s ease-in-out infinite;
    }
    &.circle-2 {
      width: 300px; height: 300px;
      bottom: -80px; left: -60px;
      animation: float 10s ease-in-out infinite 1s;
    }
    &.circle-3 {
      width: 200px; height: 200px;
      top: 60%; right: 15%;
      animation: float 7s ease-in-out infinite 0.5s;
    }
    &.circle-4 {
      width: 160px; height: 160px;
      top: 15%; left: 10%;
      animation: float 9s ease-in-out infinite 2s;
    }
  }
}

.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.18);
  position: relative;
  z-index: 1;
  animation: cardEnter 0.6s ease-out;

  .login-header {
    text-align: center;
    margin-bottom: 32px;

    .logo-icon {
      width: 64px;
      height: 64px;
      margin: 0 auto 16px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      animation: logoPulse 3s ease-in-out infinite;
    }

    h2 {
      margin: 0 0 4px;
      font-size: 24px;
      color: #303133;
      font-weight: 600;
    }

    p {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }

  .login-btn {
    width: 100%;
    height: 44px;
    font-size: 15px;
    letter-spacing: 2px;
    transition: all 0.3s ease;

    &:not(.is-loading):hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
    }
  }

  .mock-btn {
    letter-spacing: 1px;
  }

  :deep(.el-input__wrapper) {
    transition: all 0.3s ease;
    &:hover {
      box-shadow: 0 0 0 1px #c0c4cc inset;
    }
  }

  .login-footer {
    text-align: center;
    margin-top: 24px;
    font-size: 12px;
    color: #c0c4cc;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.05); }
}

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes logoPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}
</style>
