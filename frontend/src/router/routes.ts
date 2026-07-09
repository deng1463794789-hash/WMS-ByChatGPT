import type { RouteRecordRaw } from 'vue-router'

export interface MenuRouteRecord {
  path: string
  name?: string
  redirect?: string
  meta?: {
    title?: string
    icon?: string
    hidden?: boolean
    affix?: boolean
  }
  children?: MenuRouteRecord[]
}

const Login = () => import('@/views/login/index.vue')
const DefaultLayout = () => import('@/layouts/DefaultLayout/index.vue')
const Dashboard = () => import('@/views/dashboard/index.vue')
const MarketRealtime = () => import('@/views/market/realtime.vue')
const ProductList = () => import('@/views/product/list.vue')
const ProductDetail = () => import('@/views/product/detail.vue')
const InventoryStock = () => import('@/views/inventory/stock.vue')
const InventoryInbound = () => import('@/views/inventory/inbound.vue')
const InventoryOutbound = () => import('@/views/inventory/outbound.vue')
const WarehouseList = () => import('@/views/warehouse/index.vue')
const SupplierList = () => import('@/views/supplier/index.vue')
const CustomerList = () => import('@/views/customer/index.vue')
const EmployeeList = () => import('@/views/employee/index.vue')
const ExpenseList = () => import('@/views/expense/index.vue')
const SystemUser = () => import('@/views/system/user.vue')
const SystemRole = () => import('@/views/system/role.vue')
const SystemSetting = () => import('@/views/system/setting.vue')

const viewLoaders = [
  Dashboard,
  MarketRealtime,
  ProductList,
  ProductDetail,
  InventoryStock,
  InventoryInbound,
  InventoryOutbound,
  WarehouseList,
  SupplierList,
  CustomerList,
  EmployeeList,
  ExpenseList,
  SystemUser,
  SystemRole,
  SystemSetting
]

let hasPreloaded = false

export function preloadRouteComponents() {
  if (hasPreloaded || typeof window === 'undefined') return
  hasPreloaded = true

  const preload = () => {
    viewLoaders.forEach((loader) => {
      loader().catch(() => undefined)
    })
  }

  const browserWindow = window as Window & {
    requestIdleCallback?: (callback: IdleRequestCallback, options?: IdleRequestOptions) => number
  }

  if (typeof browserWindow.requestIdleCallback === 'function') {
    browserWindow.requestIdleCallback(preload, { timeout: 3000 })
  } else {
    globalThis.setTimeout(preload, 800)
  }
}

export const menuRoutes: MenuRouteRecord[] = [
  {
    path: '/dashboard',
    name: 'DashboardMenu',
    meta: { title: '运营总览', icon: 'HomeFilled' }
  },
  {
    path: '/market',
    name: 'MarketMenu',
    redirect: '/market/realtime',
    meta: { title: '实时数据', icon: 'TrendCharts' },
    children: [
      { path: 'realtime', name: 'MarketRealtimeMenu', meta: { title: '行情大屏', icon: 'DataLine' } }
    ]
  },
  {
    path: '/product',
    name: 'ProductMenu',
    redirect: '/product/list',
    meta: { title: '商品管理', icon: 'GoodsFilled' },
    children: [
      { path: 'list', name: 'ProductListMenu', meta: { title: '商品列表', icon: 'List' } },
      { path: 'detail/:id', name: 'ProductDetailMenu', meta: { title: '商品详情', hidden: true } }
    ]
  },
  {
    path: '/inventory',
    name: 'InventoryMenu',
    redirect: '/inventory/stock',
    meta: { title: '库存管理', icon: 'Box' },
    children: [
      { path: 'stock', name: 'InventoryStockMenu', meta: { title: '库存查询', icon: 'Search' } },
      { path: 'inbound', name: 'InventoryInboundMenu', meta: { title: '入库管理', icon: 'Download' } },
      { path: 'outbound', name: 'InventoryOutboundMenu', meta: { title: '出库管理', icon: 'Upload' } }
    ]
  },
  {
    path: '/warehouse',
    name: 'WarehouseMenu',
    redirect: '/warehouse/list',
    meta: { title: '仓库管理', icon: 'OfficeBuilding' },
    children: [{ path: 'list', name: 'WarehouseListMenu', meta: { title: '仓库列表', icon: 'List' } }]
  },
  {
    path: '/supplier',
    name: 'SupplierMenu',
    redirect: '/supplier/list',
    meta: { title: '供应商管理', icon: 'Van' },
    children: [{ path: 'list', name: 'SupplierListMenu', meta: { title: '供应商列表', icon: 'List' } }]
  },
  {
    path: '/customer',
    name: 'CustomerMenu',
    redirect: '/customer/list',
    meta: { title: '客户管理', icon: 'UserFilled' },
    children: [{ path: 'list', name: 'CustomerListMenu', meta: { title: '客户列表', icon: 'List' } }]
  },
  {
    path: '/employee',
    name: 'EmployeeMenu',
    redirect: '/employee/list',
    meta: { title: '员工管理', icon: 'Avatar' },
    children: [{ path: 'list', name: 'EmployeeListMenu', meta: { title: '员工列表', icon: 'List' } }]
  },
  {
    path: '/expense',
    name: 'ExpenseMenu',
    redirect: '/expense/list',
    meta: { title: '报销管理', icon: 'Money' },
    children: [{ path: 'list', name: 'ExpenseListMenu', meta: { title: '报销列表', icon: 'List' } }]
  },
  {
    path: '/system',
    name: 'SystemMenu',
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      { path: 'user', name: 'SystemUserMenu', meta: { title: '用户管理', icon: 'User' } },
      { path: 'role', name: 'SystemRoleMenu', meta: { title: '角色管理', icon: 'Avatar' } },
      { path: 'setting', name: 'SystemSettingMenu', meta: { title: '系统设置', icon: 'Tools' } }
    ]
  }
]

export const routerRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { hidden: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: DefaultLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: Dashboard, meta: { title: '运营总览', icon: 'HomeFilled', affix: true } },
      { path: 'market', redirect: '/market/realtime', meta: { hidden: true } },
      { path: 'market/realtime', name: 'MarketRealtime', component: MarketRealtime, meta: { title: '实时数据', icon: 'TrendCharts' } },
      { path: 'product', redirect: '/product/list', meta: { hidden: true } },
      { path: 'product/list', name: 'ProductList', component: ProductList, meta: { title: '商品列表', icon: 'List' } },
      { path: 'product/detail/:id', name: 'ProductDetail', component: ProductDetail, meta: { title: '商品详情', hidden: true } },
      { path: 'inventory', redirect: '/inventory/stock', meta: { hidden: true } },
      { path: 'inventory/stock', name: 'InventoryStock', component: InventoryStock, meta: { title: '库存查询', icon: 'Search' } },
      { path: 'inventory/inbound', name: 'InventoryInbound', component: InventoryInbound, meta: { title: '入库管理', icon: 'Download' } },
      { path: 'inventory/outbound', name: 'InventoryOutbound', component: InventoryOutbound, meta: { title: '出库管理', icon: 'Upload' } },
      { path: 'warehouse', redirect: '/warehouse/list', meta: { hidden: true } },
      { path: 'warehouse/list', name: 'WarehouseList', component: WarehouseList, meta: { title: '仓库列表', icon: 'List' } },
      { path: 'supplier', redirect: '/supplier/list', meta: { hidden: true } },
      { path: 'supplier/list', name: 'SupplierList', component: SupplierList, meta: { title: '供应商列表', icon: 'List' } },
      { path: 'customer', redirect: '/customer/list', meta: { hidden: true } },
      { path: 'customer/list', name: 'CustomerList', component: CustomerList, meta: { title: '客户列表', icon: 'List' } },
      { path: 'employee', redirect: '/employee/list', meta: { hidden: true } },
      { path: 'employee/list', name: 'EmployeeList', component: EmployeeList, meta: { title: '员工列表', icon: 'List' } },
      { path: 'expense', redirect: '/expense/list', meta: { hidden: true } },
      { path: 'expense/list', name: 'ExpenseList', component: ExpenseList, meta: { title: '报销列表', icon: 'List' } },
      { path: 'system', redirect: '/system/user', meta: { hidden: true } },
      { path: 'system/user', name: 'SystemUser', component: SystemUser, meta: { title: '用户管理', icon: 'User' } },
      { path: 'system/role', name: 'SystemRole', component: SystemRole, meta: { title: '角色管理', icon: 'Avatar' } },
      { path: 'system/setting', name: 'SystemSetting', component: SystemSetting, meta: { title: '系统设置', icon: 'Tools' } }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard',
    meta: { hidden: true }
  }
]

export const asyncRoutes: RouteRecordRaw[] = routerRoutes
export const allRoutes: RouteRecordRaw[] = routerRoutes