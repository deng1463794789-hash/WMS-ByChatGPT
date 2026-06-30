import type { PageParams } from '@/types/api'

export interface SysUser {
  id: number
  username: string
  realName: string
  phone?: string
  email?: string
  avatar?: string
  roleId?: number
  roleName?: string
  status: string
  createdAt?: string
}

export interface SysUserQuery extends PageParams {
  keyword?: string
  status?: string
}

export interface SysRole {
  id: number
  code: string
  name: string
  description?: string
  permissions?: number[]
  status?: string
  createdAt?: string
}

export interface SysRoleQuery extends PageParams {
  keyword?: string
}

export interface MenuItem {
  id: number
  parentId: number
  name: string
  path: string
  component?: string
  icon?: string
  sort: number
  type: 'menu' | 'button'
  permission?: string
  children?: MenuItem[]
}

export interface SysSetting {
  systemName: string
  logo?: string
  passwordMinLength: number
  loginMaxRetry: number
  sessionTimeout: number
}

export interface Warehouse {
  id: number
  code: string
  name: string
  address?: string
  manager?: string
  phone?: string
  area?: number
  status: string
  createdAt?: string
}

export interface Supplier {
  id: number
  code: string
  name: string
  contact?: string
  phone?: string
  address?: string
  email?: string
  status: string
  createdAt?: string
}

export interface Customer {
  id: number
  code: string
  name: string
  contact?: string
  phone?: string
  address?: string
  email?: string
  status: string
  createdAt?: string
}

export interface Employee {
  id: number
  code: string
  name: string
  gender: string
  phone?: string
  department?: string
  position?: string
  hireDate?: string
  salary?: number
  status: string
  remark?: string
  createdAt?: string
}

export interface EmployeeQuery extends PageParams {
  keyword?: string
  department?: string
  status?: string
}

export interface Expense {
  id: number
  expenseNo: string
  type: string
  amount: number
  applicant: string
  department?: string
  expenseDate?: string
  invoiceNo?: string
  reason?: string
  status: string
  approver?: string
  approveTime?: string
  rejectReason?: string
  remark?: string
  createdAt?: string
}

export interface ExpenseQuery extends PageParams {
  keyword?: string
  type?: string
  status?: string
}
