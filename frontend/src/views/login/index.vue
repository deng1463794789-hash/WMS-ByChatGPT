<template>
  <div class="login-page">
    <section class="brand-panel">
      <div class="brand-noise" />
      <div class="brand-content">
        <div class="brand-logo">
          <el-icon :size="28"><Box /></el-icon>
        </div>
        <p class="brand-kicker">WMS Command Center</p>
        <h1>让仓库运营更清晰、更稳定、更高效。</h1>
        <p class="brand-desc">
          集中管理商品、库存、出入库、客户与供应商数据，实时掌握关键业务状态。
        </p>
        <div class="brand-metrics">
          <div>
            <strong>24h</strong>
            <span>库存流转监控</span>
          </div>
          <div>
            <strong>99%</strong>
            <span>操作链路可追溯</span>
          </div>
          <div>
            <strong>API</strong>
            <span>真实后端持久化</span>
          </div>
        </div>
      </div>
    </section>

    <section class="login-panel">
      <div class="login-card">
        <div class="login-header">
          <span class="welcome-badge">安全登录</span>
          <h2>欢迎回到 WMS</h2>
          <p>使用管理员账号登录后进入仓储管理后台。</p>
        </div>

        <el-form ref="formRef" :model="loginForm" :rules="rules" size="large" class="login-form" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <label class="field-label">用户名</label>
            <el-input v-model="loginForm.username" placeholder="请输入用户名" clearable>
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <label class="field-label">密码</label>
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password>
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="form-row">
            <el-checkbox v-model="loginForm.remember">记住登录状态</el-checkbox>
            <span class="help-link">忘记密码？</span>
          </div>

          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            {{ loading ? '正在登录...' : '登录系统' }}
          </el-button>
        </el-form>

        <div class="login-footer">
          Copyright &copy; {{ new Date().getFullYear() }} WMS 管理系统
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/modules/user'

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
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不少于 6 位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value || loading.value) return
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
      ElMessage.error(error?.message || '登录失败，请检查账号或后端服务')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(420px, 1.08fr) minmax(420px, 0.92fr);
  background:
    radial-gradient(circle at 12% 12%, rgba(37, 99, 235, 0.14), transparent 30%),
    linear-gradient(135deg, #f8fbff 0%, #edf4f8 52%, #e8eef6 100%);
  overflow: hidden;
}

.brand-panel {
  position: relative;
  min-height: 100vh;
  padding: 52px;
  color: #fff;
  background:
    linear-gradient(145deg, rgba(15, 27, 45, 0.98), rgba(30, 64, 175, 0.94)),
    radial-gradient(circle at 80% 22%, rgba(20, 184, 166, 0.45), transparent 32%);
  overflow: hidden;
}

.brand-panel::before,
.brand-panel::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.brand-panel::before {
  width: 360px;
  height: 360px;
  right: -110px;
  top: 80px;
}

.brand-panel::after {
  width: 210px;
  height: 210px;
  left: 70px;
  bottom: 70px;
}

.brand-noise {
  position: absolute;
  inset: 0;
  opacity: 0.16;
  background-image:
    linear-gradient(90deg, rgba(255,255,255,0.08) 1px, transparent 1px),
    linear-gradient(rgba(255,255,255,0.08) 1px, transparent 1px);
  background-size: 34px 34px;
  mask-image: linear-gradient(to bottom, #000, transparent 88%);
}

.brand-content {
  position: relative;
  z-index: 1;
  max-width: 640px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-logo {
  width: 58px;
  height: 58px;
  display: grid;
  place-items: center;
  margin-bottom: 32px;
  border-radius: 20px;
  background: #1d4ed8;
  box-shadow: 0 20px 38px rgba(37, 99, 235, 0.32);
}

.brand-kicker {
  margin: 0 0 14px;
  color: rgba(226, 232, 240, 0.74);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.brand-content h1 {
  margin: 0;
  font-size: clamp(40px, 5vw, 66px);
  line-height: 1.04;
  letter-spacing: -0.06em;
}

.brand-desc {
  max-width: 560px;
  margin: 24px 0 0;
  color: rgba(226, 232, 240, 0.74);
  font-size: 16px;
  line-height: 1.9;
}

.brand-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 46px;
}

.brand-metrics div {
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.09);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.brand-metrics strong {
  display: block;
  font-size: 26px;
  font-weight: 900;
  letter-spacing: -0.04em;
}

.brand-metrics span {
  display: block;
  margin-top: 8px;
  color: rgba(226, 232, 240, 0.68);
  font-size: 12px;
}

.login-panel {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 42px;
}

.login-card {
  width: min(100%, 470px);
  padding: 42px;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.78);
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.12);
  animation: cardEnter 0.42s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.login-header {
  margin-bottom: 30px;
}

.welcome-badge {
  display: inline-flex;
  margin-bottom: 16px;
  padding: 7px 11px;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 800;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
}

.login-header h2 {
  margin: 0;
  color: #0f172a;
  font-size: 32px;
  font-weight: 900;
  letter-spacing: -0.04em;
}

.login-header p {
  margin: 12px 0 0;
  color: #64748b;
  line-height: 1.7;
}

.login-form :deep(.el-form-item) {
  display: block;
  margin-bottom: 18px;
}

.field-label {
  display: block;
  margin-bottom: 8px;
  color: #334155;
  font-size: 13px;
  font-weight: 800;
}

.login-form :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 16px !important;
  background: #f8fafc;
}

.form-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 0 22px;
}

.help-link {
  color: #1d4ed8;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 15px;
  letter-spacing: 0.08em;
}


.login-footer {
  margin-top: 28px;
  text-align: center;
  color: #94a3b8;
  font-size: 12px;
}

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(18px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 980px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .brand-panel {
    min-height: auto;
    padding: 36px 28px;
  }

  .brand-content {
    justify-content: flex-start;
  }

  .brand-content h1 {
    font-size: 36px;
  }

  .brand-metrics {
    grid-template-columns: 1fr;
    margin-top: 26px;
  }

  .login-panel {
    min-height: auto;
    padding: 28px;
  }
}

@media (max-width: 560px) {
  .login-card {
    padding: 28px;
    border-radius: 24px;
  }
}
</style>
