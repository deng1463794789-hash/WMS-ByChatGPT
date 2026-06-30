import type { RouteRecordRaw } from 'vue-router'

import Login from '@/views/login/index.vue'
import DefaultLayout from '@/layouts/DefaultLayout/index.vue'
import Dashboard from '@/views/dashboard/index.vue'
import ProductList from '@/views/product/list.vue'
import ProductDetail from '@/views/product/detail.vue'
import InventoryStock from '@/views/inventory/stock.vue'
import InventoryInbound from '@/views/inventory/inbound.vue'
import InventoryOutbound from '@/views/inventory/outbound.vue'
import WarehouseList from '@/views/warehouse/index.vue'
import SupplierList from '@/views/supplier/index.vue'
import CustomerList from '@/views/customer/index.vue'
import EmployeeList from '@/views/employee/index.vue'
import ExpenseList from '@/views/expense/index.vue'
import SystemUser from '@/views/system/user.vue'
import SystemRole from '@/views/system/role.vue'
import SystemSetting from '@/views/system/setting.vue'

export const constantRoutes: RouteRecordRaw[] = [
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
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  }
]

export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/product',
    name: 'Product',
    component: DefaultLayout,
    redirect: '/product/list',
    meta: { title: '商品管理', icon: 'GoodsFilled' },
    children: [
      {
        path: 'list',
        name: 'ProductList',
        component: ProductList,
        meta: { title: '商品列表', icon: 'List' }
      },
      {
        path: 'detail/:id',
        name: 'ProductDetail',
        component: ProductDetail,
        meta: { title: '商品详情', hidden: true }
      }
    ]
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: DefaultLayout,
    redirect: '/inventory/stock',
    meta: { title: '库存管理', icon: 'Box' },
    children: [
      {
        path: 'stock',
        name: 'InventoryStock',
        component: InventoryStock,
        meta: { title: '库存查询', icon: 'Search' }
      },
      {
        path: 'inbound',
        name: 'InventoryInbound',
        component: InventoryInbound,
        meta: { title: '入库管理', icon: 'Download' }
      },
      {
        path: 'outbound',
        name: 'InventoryOutbound',
        component: InventoryOutbound,
        meta: { title: '出库管理', icon: 'Upload' }
      }
    ]
  },
  {
    path: '/warehouse',
    name: 'Warehouse',
    component: DefaultLayout,
    redirect: '/warehouse/list',
    meta: { title: '仓库管理', icon: 'OfficeBuilding' },
    children: [
      {
        path: 'list',
        name: 'WarehouseList',
        component: WarehouseList,
        meta: { title: '仓库列表', icon: 'List' }
      }
    ]
  },
  {
    path: '/supplier',
    name: 'Supplier',
    component: DefaultLayout,
    redirect: '/supplier/list',
    meta: { title: '供应商管理', icon: 'Van' },
    children: [
      {
        path: 'list',
        name: 'SupplierList',
        component: SupplierList,
        meta: { title: '供应商列表', icon: 'List' }
      }
    ]
  },
  {
    path: '/customer',
    name: 'Customer',
    component: DefaultLayout,
    redirect: '/customer/list',
    meta: { title: '客户管理', icon: 'UserFilled' },
    children: [
      {
        path: 'list',
        name: 'CustomerList',
        component: CustomerList,
        meta: { title: '客户列表', icon: 'List' }
      }
    ]
  },
  {
    path: '/employee',
    name: 'Employee',
    component: DefaultLayout,
    redirect: '/employee/list',
    meta: { title: '员工管理', icon: 'Avatar' },
    children: [
      {
        path: 'list',
        name: 'EmployeeList',
        component: EmployeeList,
        meta: { title: '员工列表', icon: 'List' }
      }
    ]
  },
  {
    path: '/expense',
    name: 'Expense',
    component: DefaultLayout,
    redirect: '/expense/list',
    meta: { title: '报销管理', icon: 'Money' },
    children: [
      {
        path: 'list',
        name: 'ExpenseList',
        component: ExpenseList,
        meta: { title: '报销列表', icon: 'List' }
      }
    ]
  },
  {
    path: '/system',
    name: 'System',
    component: DefaultLayout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: SystemUser,
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: SystemRole,
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: 'setting',
        name: 'SystemSetting',
        component: SystemSetting,
        meta: { title: '系统设置', icon: 'Tools' }
      }
    ]
  }
]

export const allRoutes: RouteRecordRaw[] = [...constantRoutes, ...asyncRoutes]
