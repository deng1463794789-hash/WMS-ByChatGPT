import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getUserInfo as getUserInfoApi } from '@/api/system'
import { setToken, removeToken, setUserInfo, removeUserInfo, getToken, getUserInfo as getStoredUser } from '@/utils/storage'
import type { UserInfo, LoginParams } from '@/types/api'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getToken() || '')
  const userInfo = ref<UserInfo | null>(getStoredUser())

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const realName = computed(() => userInfo.value?.realName || username.value)
  const roles = computed(() => userInfo.value?.roles || [])
  const permissions = computed(() => userInfo.value?.permissions || [])

  const login = async (params: LoginParams) => {
    const res = await loginApi(params)
    const data = res.data
    token.value = data.token
    userInfo.value = data.userInfo
    setToken(data.token)
    setUserInfo(data.userInfo)
    return data
  }

  const getUserInfo = async () => {
    const res = await getUserInfoApi()
    userInfo.value = res.data
    setUserInfo(res.data)
    return res.data
  }

  const logout = async () => {
    try {
      await logoutApi()
    } catch (e) {
      // ignore
    }
    token.value = ''
    userInfo.value = null
    removeToken()
    removeUserInfo()
  }

  const hasPermission = (perm: string): boolean => {
    return permissions.value.includes(perm) || roles.value.includes('admin')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    realName,
    roles,
    permissions,
    login,
    logout,
    getUserInfo,
    hasPermission
  }
})
