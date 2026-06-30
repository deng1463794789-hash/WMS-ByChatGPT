const TOKEN_KEY = 'wms_token'
const USER_KEY = 'wms_user'

export function getToken(): string {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUserInfo(): any {
  const user = localStorage.getItem(USER_KEY)
  return user ? JSON.parse(user) : null
}

export function setUserInfo(user: any): void {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function removeUserInfo(): void {
  localStorage.removeItem(USER_KEY)
}

export function clearAll(): void {
  localStorage.clear()
}
