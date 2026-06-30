export interface ApiResponse<T = any> {
  code: number
  data: T
  message: string
}

export interface PageParams {
  pageNum: number
  pageSize: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface LoginParams {
  username: string
  password: string
  captcha?: string
  remember?: boolean
}

export interface LoginResult {
  token: string
  userInfo: UserInfo
}

export interface UserInfo {
  id: number
  username: string
  realName: string
  avatar?: string
  phone?: string
  roles: string[]
  permissions: string[]
}
