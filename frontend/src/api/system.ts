import request from '@/utils/request'
import type { ApiResponse, LoginParams, LoginResult, PageParams, PageResult } from '@/types/api'
import type { SysUser, SysUserQuery, SysRole, SysRoleQuery, MenuItem, SysSetting, Warehouse, Supplier, Customer, Employee, EmployeeQuery, Expense, ExpenseQuery } from './types/system'

export function login(data: LoginParams) {
  return request.post<ApiResponse<LoginResult>>('/auth/login', data)
}

export function logout() {
  return request.post<ApiResponse<null>>('/auth/logout')
}

export function getUserInfo() {
  return request.get<ApiResponse<LoginResult['userInfo']>>('/auth/userinfo')
}

export function getMenuList() {
  return request.get<ApiResponse<MenuItem[]>>('/auth/menus')
}

export function getUserList(params: SysUserQuery) {
  return request.get<ApiResponse<PageResult<SysUser>>>('/users', params)
}

export function createUser(data: Partial<SysUser>) {
  return request.post<ApiResponse<SysUser>>('/users', data)
}

export function updateUser(id: number, data: Partial<SysUser>) {
  return request.put<ApiResponse<SysUser>>(`/users/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete<ApiResponse<null>>(`/users/${id}`)
}

export function resetPassword(id: number) {
  return request.put<ApiResponse<null>>(`/users/${id}/reset-password`)
}

export function getRoleList(params: SysRoleQuery) {
  return request.get<ApiResponse<PageResult<SysRole>>>('/roles', params)
}

export function getAllRoles() {
  return request.get<ApiResponse<SysRole[]>>('/roles/all')
}

export function createRole(data: Partial<SysRole>) {
  return request.post<ApiResponse<SysRole>>('/roles', data)
}

export function updateRole(id: number, data: Partial<SysRole>) {
  return request.put<ApiResponse<SysRole>>(`/roles/${id}`, data)
}

export function deleteRole(id: number) {
  return request.delete<ApiResponse<null>>(`/roles/${id}`)
}

export function getPermissionTree() {
  return request.get<ApiResponse<MenuItem[]>>('/permissions/tree')
}

export function getSysSetting() {
  return request.get<ApiResponse<SysSetting>>('/system/setting')
}

export function updateSysSetting(data: Partial<SysSetting>) {
  return request.put<ApiResponse<SysSetting>>('/system/setting', data)
}

export function getWarehouseList(params: PageParams & { keyword?: string }) {
  return request.get<ApiResponse<PageResult<Warehouse>>>('/warehouses', params)
}

export function createWarehouse(data: Partial<Warehouse>) {
  return request.post<ApiResponse<Warehouse>>('/warehouses', data)
}

export function updateWarehouse(id: number, data: Partial<Warehouse>) {
  return request.put<ApiResponse<Warehouse>>(`/warehouses/${id}`, data)
}

export function deleteWarehouse(id: number) {
  return request.delete<ApiResponse<null>>(`/warehouses/${id}`)
}

export function getSupplierList(params: PageParams & { keyword?: string }) {
  return request.get<ApiResponse<PageResult<Supplier>>>('/suppliers', params)
}

export function createSupplier(data: Partial<Supplier>) {
  return request.post<ApiResponse<Supplier>>('/suppliers', data)
}

export function updateSupplier(id: number, data: Partial<Supplier>) {
  return request.put<ApiResponse<Supplier>>(`/suppliers/${id}`, data)
}

export function deleteSupplier(id: number) {
  return request.delete<ApiResponse<null>>(`/suppliers/${id}`)
}

export function getCustomerList(params: PageParams & { keyword?: string }) {
  return request.get<ApiResponse<PageResult<Customer>>>('/customers', params)
}

export function createCustomer(data: Partial<Customer>) {
  return request.post<ApiResponse<Customer>>('/customers', data)
}

export function updateCustomer(id: number, data: Partial<Customer>) {
  return request.put<ApiResponse<Customer>>(`/customers/${id}`, data)
}

export function deleteCustomer(id: number) {
  return request.delete<ApiResponse<null>>(`/customers/${id}`)
}

export function getEmployeeList(params: EmployeeQuery) {
  return request.get<ApiResponse<PageResult<Employee>>>('/employees', params)
}

export function createEmployee(data: Partial<Employee>) {
  return request.post<ApiResponse<Employee>>('/employees', data)
}

export function updateEmployee(id: number, data: Partial<Employee>) {
  return request.put<ApiResponse<Employee>>(`/employees/${id}`, data)
}

export function deleteEmployee(id: number) {
  return request.delete<ApiResponse<null>>(`/employees/${id}`)
}

export function batchDeleteEmployees(ids: number[]) {
  return request.post<ApiResponse<null>>('/employees/batch-delete', { ids })
}

export function getExpenseList(params: ExpenseQuery) {
  return request.get<ApiResponse<PageResult<Expense>>>('/expenses', params)
}

export function createExpense(data: Partial<Expense>) {
  return request.post<ApiResponse<Expense>>('/expenses', data)
}

export function updateExpense(id: number, data: Partial<Expense>) {
  return request.put<ApiResponse<Expense>>(`/expenses/${id}`, data)
}

export function deleteExpense(id: number) {
  return request.delete<ApiResponse<null>>(`/expenses/${id}`)
}

export function batchDeleteExpenses(ids: number[]) {
  return request.post<ApiResponse<null>>('/expenses/batch-delete', { ids })
}

export function approveExpense(id: number, data: { action: string; rejectReason?: string; opinion?: string }) {
  return request.put<ApiResponse<Expense>>(`/expenses/${id}/approve`, data)
}
