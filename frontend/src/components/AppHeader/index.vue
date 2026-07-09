<template>
  <div class="app-header">
    <div class="header-left">
      <button class="collapse-btn" @click="appStore.toggleSidebar()" aria-label="切换菜单">
        <el-icon :size="20">
          <Fold v-if="!appStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </button>
      <div class="page-heading">
        <span class="eyebrow">Warehouse Control</span>
        <strong>{{ pageTitle }}</strong>
      </div>
    </div>

    <div class="header-center">
      <el-icon><Search /></el-icon>
      <span>搜索商品、单据、客户</span>
      <kbd>Ctrl K</kbd>
    </div>

    <div class="header-right">
      <div class="signal-pill">
        <span class="pulse" />
        <span>Live</span>
      </div>
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="36" icon="UserFilled" />
          <div class="user-copy">
            <span>{{ userStore.realName }}</span>
            <small>系统管理员</small>
          </div>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>个人中心
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <el-icon><Lock /></el-icon>修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/modules/app'
import { useUserStore } from '@/stores/modules/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()

const pageTitle = computed(() => route.meta.title || '运营总览')

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出当前账号吗？', '退出确认', {
      type: 'warning'
    })
    await userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.app-header {
  height: $header-height;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 0 16px 0 18px;
  border: 1px solid $border-color;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: $shadow-soft;


  .header-left,
  .header-right,
  .header-center,
  .user-info,
  .signal-pill {
    display: flex;
    align-items: center;
  }

  .header-left {
    gap: 14px;
    min-width: 220px;
  }

  .collapse-btn {
    width: 42px;
    height: 42px;
    border: none;
    border-radius: 15px;
    color: #1e293b;
    background: #f8fafc;
    box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.22), 0 8px 22px rgba(15, 23, 42, 0.06);
    cursor: pointer;
    transition: transform 0.22s $ease-out, box-shadow 0.22s ease, color 0.22s ease;

    &:hover {
      color: $primary-color;
      transform: translateY(-2px);
      box-shadow: inset 0 0 0 1px rgba(29, 78, 216, 0.24), 0 12px 24px rgba(29, 78, 216, 0.12);
    }
  }

  .page-heading {
    display: flex;
    flex-direction: column;
    line-height: 1.1;

    .eyebrow {
      color: $text-secondary;
      font-size: 11px;
      text-transform: uppercase;
      letter-spacing: 0.16em;
      margin-bottom: 7px;
    }

    strong {
      color: $text-primary;
      font-size: 20px;
      font-weight: 800;
    }
  }

  .header-center {
    flex: 1;
    max-width: 460px;
    height: 42px;
    gap: 10px;
    padding: 0 12px 0 14px;
    color: $text-secondary;
    border-radius: 999px;
    background: rgba(241, 245, 249, 0.85);
    border: 1px solid rgba(148, 163, 184, 0.18);

    span {
      flex: 1;
      font-size: 13px;
    }

    kbd {
      padding: 3px 7px;
      color: #475569;
      font-size: 11px;
      border-radius: 8px;
      background: #fff;
      box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.24);
    }
  }

  .header-right {
    justify-content: flex-end;
    gap: 12px;
  }

  .signal-pill {
    gap: 8px;
    height: 36px;
    padding: 0 12px;
    color: #047857;
    font-size: 12px;
    font-weight: 700;
    border-radius: 999px;
    background: rgba(22, 163, 74, 0.1);
  }

  .pulse {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: $success-color;
    box-shadow: 0 0 0 0 rgba(22, 163, 74, 0.45);

  }

  .user-info {
    gap: 10px;
    min-width: 150px;
    padding: 6px 8px 6px 6px;
    border-radius: 18px;
    cursor: pointer;
    transition: background 0.2s ease, transform 0.2s $ease-out;

    &:hover {
      background: rgba(241, 245, 249, 0.9);
      transform: translateY(-1px);
    }
  }

  .user-copy {
    display: flex;
    flex-direction: column;
    line-height: 1.1;

    span {
      color: $text-primary;
      font-weight: 700;
      font-size: 13px;
    }

    small {
      margin-top: 5px;
      color: $text-secondary;
      font-size: 11px;
    }
  }
}

@keyframes pulse {
  70% { box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); }
  100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}

@media (max-width: 1080px) {
  .app-header .header-center {
    display: none;
  }
}

@media (max-width: 720px) {
  .app-header {
    .signal-pill,
    .user-copy {
      display: none;
    }

    .header-left {
      min-width: 0;
    }
  }
}
</style>
