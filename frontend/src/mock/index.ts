import { getToken } from '@/utils/storage'

const MOCK_FLAG_KEY = '__wms_mock_mode__'

export function isMockAvailable(): boolean {
  return import.meta.env.VITE_APP_ENABLE_MOCK === 'true'
}

export function isMockEnabled(): boolean {
  return isMockAvailable() && localStorage.getItem(MOCK_FLAG_KEY) === 'true'
}

export function enableMock(): void {
  if (isMockAvailable()) {
    localStorage.setItem(MOCK_FLAG_KEY, 'true')
  }
}

export function disableMock(): void {
  localStorage.removeItem(MOCK_FLAG_KEY)
}

const store: Record<string, any[]> = {
  products: [
    { id: 1, sku: 'SCREW-001', name: '十字螺丝刀套装', unit: '套', stockQuantity: 45, safeStock: 20, categoryId: 1, categoryName: '工具', price: 29.9, remark: '五金工具', status: 'active', createdAt: '2026-05-01 09:00:00', updatedAt: '2026-05-20 14:00:00' },
    { id: 2, sku: 'TAPE-003', name: '电工绝缘胶带', unit: '卷', stockQuantity: 8, safeStock: 30, categoryId: 2, categoryName: '电气', price: 5.5, remark: '', status: 'active', createdAt: '2026-04-15 10:30:00', updatedAt: '2026-05-18 09:00:00' },
    { id: 3, sku: 'GLOVE-002', name: '防割劳保手套', unit: '双', stockQuantity: 120, safeStock: 50, categoryId: 3, categoryName: '劳保', price: 12.0, remark: 'L码', status: 'active', createdAt: '2026-03-20 08:00:00', updatedAt: '2026-05-10 16:00:00' },
    { id: 4, sku: 'HAMMER-005', name: '羊角锤', unit: '把', stockQuantity: 3, safeStock: 15, categoryId: 1, categoryName: '工具', price: 35.0, remark: '8oz', status: 'active', createdAt: '2026-05-03 11:00:00', updatedAt: '2026-05-22 08:30:00' },
    { id: 5, sku: 'WRENCH-010', name: '活动扳手12寸', unit: '把', stockQuantity: 67, safeStock: 25, categoryId: 1, categoryName: '工具', price: 42.0, remark: '', status: 'active', createdAt: '2026-02-10 13:00:00', updatedAt: '2026-05-15 10:00:00' },
    { id: 6, sku: 'CABLE-100', name: '铜芯电缆2.5mm', unit: '米', stockQuantity: 500, safeStock: 100, categoryId: 2, categoryName: '电气', price: 3.8, remark: '国标', status: 'active', createdAt: '2026-01-05 09:30:00', updatedAt: '2026-05-19 11:00:00' },
    { id: 7, sku: 'MASK-020', name: '防尘口罩', unit: '盒', stockQuantity: 15, safeStock: 40, categoryId: 3, categoryName: '劳保', price: 25.0, remark: '50只/盒', status: 'active', createdAt: '2026-04-20 14:00:00', updatedAt: '2026-05-21 09:00:00' },
    { id: 8, sku: 'DRILL-030', name: '手电钻', unit: '台', stockQuantity: 22, safeStock: 10, categoryId: 1, categoryName: '工具', price: 280.0, remark: '650W', status: 'active', createdAt: '2026-05-10 10:00:00', updatedAt: '2026-05-25 15:00:00' }
  ],
  categories: [
    { id: 1, name: '工具', parentId: 0, sort: 1 },
    { id: 2, name: '电气', parentId: 0, sort: 2 },
    { id: 3, name: '劳保', parentId: 0, sort: 3 }
  ],
  warehouses: [
    { id: 1, code: 'WH-A01', name: '主仓库', address: '上海市浦东新区张江路100号', manager: '张三', phone: '13800138001', status: 'active', createdAt: '2025-06-01 09:00:00' },
    { id: 2, code: 'WH-B02', name: '分仓B', address: '上海市闵行区七莘路200号', manager: '李四', phone: '13800138002', status: 'active', createdAt: '2025-08-15 10:00:00' },
    { id: 3, code: 'WH-C03', name: '退货仓', address: '上海市嘉定区安亭镇', manager: '王五', phone: '13800138003', status: 'inactive', createdAt: '2025-10-01 08:30:00' }
  ],
  suppliers: [
    { id: 1, code: 'SUP-001', name: '上海工具制造有限公司', contact: '赵六', phone: '13900139001', address: '上海市松江区', email: 'zhaoliu@tool.com', status: 'active', createdAt: '2025-03-01 09:00:00' },
    { id: 2, code: 'SUP-002', name: '江苏电气配件厂', contact: '孙七', phone: '13900139002', address: '江苏省苏州市工业园区', email: 'sunqi@elec.com', status: 'active', createdAt: '2025-05-10 10:00:00' },
    { id: 3, code: 'SUP-003', name: '浙江劳保用品公司', contact: '周八', phone: '13900139003', address: '浙江省杭州市', email: 'zhouba@labor.com', status: 'inactive', createdAt: '2025-07-20 14:00:00' }
  ],
  customers: [
    { id: 1, code: 'CUS-001', name: '上海建筑有限公司', contact: '吴九', phone: '13700137001', address: '上海市徐汇区', email: 'wujiu@build.com', status: 'active', createdAt: '2025-04-01 09:00:00' },
    { id: 2, code: 'CUS-002', name: '北京安装工程队', contact: '郑十', phone: '13700137002', address: '北京市朝阳区', email: 'zhengshi@eng.com', status: 'active', createdAt: '2025-06-15 10:00:00' },
    { id: 3, code: 'CUS-003', name: '广州装饰公司', contact: '刘一', phone: '13700137003', address: '广州市天河区', email: 'liuyi@deco.com', status: 'inactive', createdAt: '2025-09-01 14:00:00' }
  ],
  employees: [
    { id: 1, code: 'WMS001', name: '赵建国', gender: 'male', phone: '13800138801', department: '仓储部', position: '仓库主管', hireDate: '2024-03-01', salary: 8500, status: 'active', remark: '', createdAt: '2024-03-01 09:00:00' },
    { id: 2, code: 'WMS002', name: '钱晓丽', gender: 'female', phone: '13800138802', department: '物流部', position: '物流专员', hireDate: '2024-05-15', salary: 6200, status: 'active', remark: '', createdAt: '2024-05-15 10:00:00' },
    { id: 3, code: 'WMS003', name: '孙志强', gender: 'male', phone: '13800138803', department: '质检部', position: '质检员', hireDate: '2024-06-01', salary: 5800, status: 'active', remark: '', createdAt: '2024-06-01 08:30:00' },
    { id: 4, code: 'WMS004', name: '李美玲', gender: 'female', phone: '13800138804', department: '仓储部', position: '仓管员', hireDate: '2024-07-10', salary: 5000, status: 'leave', remark: '产假', createdAt: '2024-07-10 09:00:00' },
    { id: 5, code: 'WMS005', name: '周大伟', gender: 'male', phone: '13800138805', department: '采购部', position: '采购经理', hireDate: '2023-11-01', salary: 9500, status: 'active', remark: '', createdAt: '2023-11-01 09:00:00' },
    { id: 6, code: 'WMS006', name: '吴小芳', gender: 'female', phone: '13800138806', department: '行政部', position: '行政专员', hireDate: '2024-02-20', salary: 4800, status: 'active', remark: '', createdAt: '2024-02-20 10:00:00' },
    { id: 7, code: 'WMS007', name: '郑国栋', gender: 'male', phone: '13800138807', department: '仓储部', position: '叉车司机', hireDate: '2023-09-01', salary: 5500, status: 'resigned', remark: '个人原因', createdAt: '2023-09-01 08:00:00' },
    { id: 8, code: 'WMS008', name: '王海燕', gender: 'female', phone: '13800138808', department: '财务部', position: '会计', hireDate: '2024-01-15', salary: 6500, status: 'active', remark: '', createdAt: '2024-01-15 09:30:00' }
  ],
  users: [
    { id: 1, username: 'admin', realName: '系统管理员', phone: '13600136001', email: 'admin@wms.com', roleId: 1, roleName: '超级管理员', status: 'active', createdAt: '2025-01-01 00:00:00' },
    { id: 2, username: 'zhangsan', realName: '张三', phone: '13600136002', email: 'zhangsan@wms.com', roleId: 2, roleName: '仓库管理员', status: 'active', createdAt: '2025-03-01 09:00:00' },
    { id: 3, username: 'lisi', realName: '李四', phone: '13600136003', email: 'lisi@wms.com', roleId: 3, roleName: '普通操作员', status: 'inactive', createdAt: '2025-05-10 10:00:00' }
  ],
  expenses: [
    { id: 1, expenseNo: 'EXP-20260501-001', type: 'travel', amount: 2850.00, applicant: '赵建国', department: '仓储部', expenseDate: '2026-04-25', invoiceNo: 'INV20260425001', reason: '赴南京参加仓储管理交流会议，往返高铁及住宿费', status: 'approved', approver: '系统管理员', approveTime: '2026-04-28 10:30:00', rejectReason: '', remark: '', createdAt: '2026-04-26 09:00:00' },
    { id: 2, expenseNo: 'EXP-20260510-002', type: 'office', amount: 456.80, applicant: '吴小芳', department: '行政部', expenseDate: '2026-05-08', invoiceNo: 'INV20260508002', reason: '采购办公用品，含打印纸、文件夹、签字笔等', status: 'approved', approver: '系统管理员', approveTime: '2026-05-09 14:00:00', rejectReason: '', remark: '', createdAt: '2026-05-08 16:00:00' },
    { id: 3, expenseNo: 'EXP-20260515-003', type: 'transport', amount: 320.00, applicant: '钱晓丽', department: '物流部', expenseDate: '2026-05-12', invoiceNo: 'INV20260512003', reason: '市内配送调度，出租车费用报销', status: 'approved', approver: '系统管理员', approveTime: '2026-05-13 09:00:00', rejectReason: '', remark: '', createdAt: '2026-05-12 18:00:00' },
    { id: 4, expenseNo: 'EXP-20260520-004', type: 'meal', amount: 1680.00, applicant: '周大伟', department: '采购部', expenseDate: '2026-05-18', invoiceNo: 'INV20260518004', reason: '接待供应商来访，商务宴请三桌', status: 'pending', approver: '', approveTime: '', rejectReason: '', remark: '需附菜单明细', createdAt: '2026-05-19 09:00:00' },
    { id: 5, expenseNo: 'EXP-20260522-005', type: 'phone', amount: 298.00, applicant: '孙志强', department: '质检部', expenseDate: '2026-05-01', invoiceNo: 'INV20260501005', reason: '五月份通讯费报销', status: 'rejected', approver: '系统管理员', approveTime: '2026-05-06 11:00:00', rejectReason: '发票抬头有误，请重新开具', remark: '', createdAt: '2026-05-03 10:00:00' },
    { id: 6, expenseNo: 'EXP-20260525-006', type: 'travel', amount: 5200.00, applicant: '钱晓丽', department: '物流部', expenseDate: '2026-05-20', invoiceNo: 'INV20260520006', reason: '赴北京参加全国物流行业峰会，含机票住宿餐饮', status: 'pending', approver: '', approveTime: '', rejectReason: '', remark: '预算超支需领导特批', createdAt: '2026-05-22 08:00:00' }
  ],
  roles: [
    { id: 1, code: 'admin', name: '超级管理员', description: '拥有系统全部权限', permissions: [1, 2, 3, 4, 5, 6, 7], createdAt: '2025-01-01 00:00:00' },
    { id: 2, code: 'warehouse_manager', name: '仓库管理员', description: '管理仓库日常运营', permissions: [1, 2, 3], createdAt: '2025-03-01 09:00:00' },
    { id: 3, code: 'operator', name: '普通操作员', description: '执行仓库基本操作', permissions: [1, 2], createdAt: '2025-05-10 10:00:00' }
  ],
  permissions: [
    { id: 1, parentId: 0, name: '商品管理', path: '/product', icon: 'GoodsFilled', sort: 1, type: 'menu', children: [
      { id: 11, parentId: 1, name: '商品列表', path: 'list', sort: 1, type: 'menu' }
    ]},
    { id: 2, parentId: 0, name: '库存管理', path: '/inventory', icon: 'Box', sort: 2, type: 'menu', children: [
      { id: 21, parentId: 2, name: '库存查询', path: 'stock', sort: 1, type: 'menu' },
      { id: 22, parentId: 2, name: '入库管理', path: 'inbound', sort: 2, type: 'menu' },
      { id: 23, parentId: 2, name: '出库管理', path: 'outbound', sort: 3, type: 'menu' }
    ]}
  ],
  inbound: [
    { id: 1, inboundNo: 'IN-20260501-001', type: 'purchase', productCount: 3, totalQuantity: 150, operator: 'admin', inboundTime: '2026-05-01 09:30:00', remark: '五一节前备货', status: 'completed' },
    { id: 2, inboundNo: 'IN-20260515-002', type: 'return', productCount: 1, totalQuantity: 10, operator: 'zhangsan', inboundTime: '2026-05-15 14:00:00', remark: '客户退货', status: 'completed' },
    { id: 3, inboundNo: 'IN-20260520-003', type: 'transfer', productCount: 5, totalQuantity: 300, operator: 'lisi', inboundTime: '2026-05-20 10:00:00', remark: 'B仓调拨', status: 'pending' }
  ],
  outbound: [
    { id: 1, outboundNo: 'OUT-20260502-001', type: 'sale', productCount: 2, totalQuantity: 80, operator: 'admin', outboundTime: '2026-05-02 11:00:00', remark: '上海建筑公司订单', status: 'completed' },
    { id: 2, outboundNo: 'OUT-20260510-002', type: 'return', productCount: 1, totalQuantity: 5, operator: 'zhangsan', outboundTime: '2026-05-10 16:00:00', remark: '退供应商', status: 'completed' },
    { id: 3, outboundNo: 'OUT-20260522-003', type: 'transfer', productCount: 3, totalQuantity: 120, operator: 'lisi', outboundTime: '2026-05-22 09:00:00', remark: '调往C仓', status: 'pending' }
  ]
}

const systemSetting = {
  systemName: 'WMS管理系统',
  passwordMinLength: 6,
  loginMaxRetry: 5,
  sessionTimeout: 30
}

const typeLabels: Record<string, string> = {
  purchase: '采购入库', return: '退货入库', transfer: '调拨入库', other: '其他入库',
  sale: '销售出库'
}

function paginate<T>(list: T[], params: any) {
  const { pageNum = 1, pageSize = 10 } = params || {}
  const start = (pageNum - 1) * pageSize
  const end = start + pageSize
  return {
    records: list.slice(start, end),
    total: list.length,
    size: pageSize,
    current: pageNum,
    pages: Math.ceil(list.length / pageSize)
  }
}

function makeResult(data: any) {
  return { code: 200, data, message: 'success' }
}

let nextId: Record<string, number> = {
  products: 100, warehouses: 100, suppliers: 100, customers: 100,
  users: 100, roles: 100, inbound: 100, outbound: 100, employees: 100, expenses: 100
}

export function handleMockRequest(url: string, method: string, data?: any, params?: any): any {
  if (!isMockEnabled()) return null

  const token = getToken()

  if (url === '/auth/login' && method === 'post') {
    return makeResult({
      token: 'mock_token_wms_' + Date.now(),
      userInfo: {
        id: 1,
        username: data?.username || 'admin',
        realName: '系统管理员',
        avatar: '',
        phone: '13600136001',
        roles: ['admin'],
        permissions: ['*']
      }
    })
  }

  if (url === '/auth/logout' && method === 'post') {
    return makeResult(null)
  }

  if (url === '/auth/userinfo' && method === 'get') {
    return makeResult({
      id: 1,
      username: 'admin',
      realName: '系统管理员',
      avatar: '',
      phone: '13600136001',
      roles: ['admin'],
      permissions: ['*']
    })
  }

  if (url === '/auth/menus' && method === 'get') {
    return makeResult(store.permissions)
  }

  if (url === '/dashboard' && method === 'get') {
    const lowStock = store.products
      .filter((p: any) => p.stockQuantity <= p.safeStock)
      .map((p: any) => ({ name: p.name, sku: p.sku, stockQuantity: p.stockQuantity, safeStock: p.safeStock }))
    return makeResult({
      productCount: store.products.length,
      totalStock: store.products.reduce((s: number, p: any) => s + p.stockQuantity, 0),
      todayInbound: 23,
      todayOutbound: 41,
      lowStockProducts: lowStock,
      trendData: [
        { date: '2026-05-21', inbound: 12, outbound: 8 },
        { date: '2026-05-22', inbound: 18, outbound: 15 },
        { date: '2026-05-23', inbound: 8, outbound: 22 },
        { date: '2026-05-24', inbound: 25, outbound: 10 },
        { date: '2026-05-25', inbound: 15, outbound: 30 },
        { date: '2026-05-26', inbound: 30, outbound: 18 },
        { date: '2026-05-27', inbound: 22, outbound: 25 }
      ]
    })
  }

  if (url === '/products' && method === 'get') {
    let list = [...store.products]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      list = list.filter((p: any) => p.sku.toLowerCase().includes(kw) || p.name.toLowerCase().includes(kw))
    }
    if (params?.stockStatus === 'sufficient') {
      list = list.filter((p: any) => p.stockQuantity > p.safeStock * 2)
    } else if (params?.stockStatus === 'warning') {
      list = list.filter((p: any) => p.stockQuantity > p.safeStock && p.stockQuantity <= p.safeStock * 2)
    } else if (params?.stockStatus === 'shortage') {
      list = list.filter((p: any) => p.stockQuantity <= p.safeStock)
    }
    return makeResult(paginate(list, params))
  }

  const productStockMatch = url.match(/^\/products\/(\d+)\/stock$/)
  if (productStockMatch && method === 'put') {
    const idx = store.products.findIndex((p: any) => p.id === Number(productStockMatch[1]))
    if (idx > -1) {
      store.products[idx] = { ...store.products[idx], stockQuantity: data?.stockQuantity ?? store.products[idx].stockQuantity, updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') }
      return makeResult(store.products[idx])
    }
    return makeResult(null)
  }

  const productMatch = url.match(/^\/products\/(\d+)$/)
  if (productMatch && method === 'get') {
    const product = store.products.find((p: any) => p.id === Number(productMatch[1]))
    return makeResult(product || null)
  }

  if (url === '/products' && method === 'post') {
    const newProduct = { ...data, id: ++nextId.products, createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '), updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') }
    store.products.unshift(newProduct)
    return makeResult(newProduct)
  }

  if (productMatch && method === 'put') {
    const idx = store.products.findIndex((p: any) => p.id === Number(productMatch[1]))
    if (idx > -1) {
      store.products[idx] = { ...store.products[idx], ...data, updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ') }
      return makeResult(store.products[idx])
    }
    return makeResult(null)
  }

  if (productMatch && method === 'delete') {
    store.products = store.products.filter((p: any) => p.id !== Number(productMatch[1]))
    return makeResult(null)
  }

  if (url === '/products/batch-delete' && method === 'post') {
    const ids: number[] = data?.ids || []
    store.products = store.products.filter((p: any) => !ids.includes(p.id))
    return makeResult(null)
  }

  if (url === '/products/export' && method === 'get') {
    return new Blob(['mock export'], { type: 'application/octet-stream' })
  }

  if (url === '/categories' && method === 'get') {
    return makeResult(store.categories)
  }

  if (url === '/inbound' && method === 'get') {
    let list = [...store.inbound].map((r: any) => ({ ...r, type: typeLabels[r.type] || r.type }))
    if (params?.keyword || params?.inboundNo) {
      const kw = (params.keyword || params.inboundNo || '').toLowerCase()
      list = list.filter((r: any) => r.inboundNo.toLowerCase().includes(kw))
    }
    return makeResult(paginate(list, params))
  }

  if (url === '/inbound' && method === 'post') {
    const newRecord = {
      ...data,
      id: ++nextId.inbound,
      inboundNo: `IN-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-${String(nextId.inbound).padStart(3, '0')}`,
      productCount: data?.items?.length || 0,
      totalQuantity: (data?.items || []).reduce((s: number, i: any) => s + (i.quantity || 0), 0),
      operator: 'admin',
      inboundTime: data?.inboundTime || new Date().toISOString().slice(0, 19).replace('T', ' '),
      status: 'completed'
    }
    store.inbound.unshift(newRecord)
    return makeResult(newRecord)
  }

  const inboundMatch = url.match(/^\/inbound\/(\d+)$/)
  if (inboundMatch && method === 'get') {
    const record = store.inbound.find((r: any) => r.id === Number(inboundMatch[1]))
    return makeResult(record || null)
  }

  if (url === '/outbound' && method === 'get') {
    let list = [...store.outbound].map((r: any) => ({ ...r, type: typeLabels[r.type] || r.type }))
    if (params?.keyword || params?.outboundNo) {
      const kw = (params.keyword || params.outboundNo || '').toLowerCase()
      list = list.filter((r: any) => r.outboundNo.toLowerCase().includes(kw))
    }
    return makeResult(paginate(list, params))
  }

  if (url === '/outbound' && method === 'post') {
    const newRecord = {
      ...data,
      id: ++nextId.outbound,
      outboundNo: `OUT-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-${String(nextId.outbound).padStart(3, '0')}`,
      productCount: data?.items?.length || 0,
      totalQuantity: (data?.items || []).reduce((s: number, i: any) => s + (i.quantity || 0), 0),
      operator: 'admin',
      outboundTime: data?.outboundTime || new Date().toISOString().slice(0, 19).replace('T', ' '),
      status: 'completed'
    }
    store.outbound.unshift(newRecord)
    return makeResult(newRecord)
  }

  const outboundMatch = url.match(/^\/outbound\/(\d+)$/)
  if (outboundMatch && method === 'get') {
    const record = store.outbound.find((r: any) => r.id === Number(outboundMatch[1]))
    return makeResult(record || null)
  }

  if (url === '/warehouses' && method === 'get') {
    let list = [...store.warehouses]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      list = list.filter((w: any) => w.code.toLowerCase().includes(kw) || w.name.toLowerCase().includes(kw))
    }
    return makeResult(paginate(list, params))
  }

  const whMatch = url.match(/^\/warehouses\/(\d+)$/)
  if (url === '/warehouses' && method === 'post') {
    const nw = { ...data, id: ++nextId.warehouses, createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ') }
    store.warehouses.unshift(nw)
    return makeResult(nw)
  }
  if (whMatch && method === 'put') {
    const idx = store.warehouses.findIndex((w: any) => w.id === Number(whMatch[1]))
    if (idx > -1) { store.warehouses[idx] = { ...store.warehouses[idx], ...data }; return makeResult(store.warehouses[idx]) }
    return makeResult(null)
  }
  if (whMatch && method === 'delete') {
    store.warehouses = store.warehouses.filter((w: any) => w.id !== Number(whMatch[1]))
    return makeResult(null)
  }

  if (url === '/suppliers' && method === 'get') {
    let list = [...store.suppliers]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      list = list.filter((s: any) => s.code.toLowerCase().includes(kw) || s.name.toLowerCase().includes(kw) || (s.contact || '').toLowerCase().includes(kw))
    }
    return makeResult(paginate(list, params))
  }

  const supMatch = url.match(/^\/suppliers\/(\d+)$/)
  if (url === '/suppliers' && method === 'post') {
    const ns = { ...data, id: ++nextId.suppliers, createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ') }
    store.suppliers.unshift(ns)
    return makeResult(ns)
  }
  if (supMatch && method === 'put') {
    const idx = store.suppliers.findIndex((s: any) => s.id === Number(supMatch[1]))
    if (idx > -1) { store.suppliers[idx] = { ...store.suppliers[idx], ...data }; return makeResult(store.suppliers[idx]) }
    return makeResult(null)
  }
  if (supMatch && method === 'delete') {
    store.suppliers = store.suppliers.filter((s: any) => s.id !== Number(supMatch[1]))
    return makeResult(null)
  }

  if (url === '/customers' && method === 'get') {
    let list = [...store.customers]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      list = list.filter((c: any) => c.code.toLowerCase().includes(kw) || c.name.toLowerCase().includes(kw) || (c.contact || '').toLowerCase().includes(kw))
    }
    return makeResult(paginate(list, params))
  }

  const cusMatch = url.match(/^\/customers\/(\d+)$/)
  if (url === '/customers' && method === 'post') {
    const nc = { ...data, id: ++nextId.customers, createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ') }
    store.customers.unshift(nc)
    return makeResult(nc)
  }
  if (cusMatch && method === 'put') {
    const idx = store.customers.findIndex((c: any) => c.id === Number(cusMatch[1]))
    if (idx > -1) { store.customers[idx] = { ...store.customers[idx], ...data }; return makeResult(store.customers[idx]) }
    return makeResult(null)
  }
  if (cusMatch && method === 'delete') {
    store.customers = store.customers.filter((c: any) => c.id !== Number(cusMatch[1]))
    return makeResult(null)
  }

  if (url === '/employees' && method === 'get') {
    let list = [...store.employees]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      list = list.filter((e: any) => e.code.toLowerCase().includes(kw) || e.name.toLowerCase().includes(kw) || (e.phone || '').includes(kw))
    }
    if (params?.department) {
      list = list.filter((e: any) => e.department === params.department)
    }
    if (params?.status) {
      list = list.filter((e: any) => e.status === params.status)
    }
    return makeResult(paginate(list, params))
  }

  const empMatch = url.match(/^\/employees\/(\d+)$/)
  if (url === '/employees' && method === 'post') {
    const ne = { ...data, id: ++nextId.employees, createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ') }
    store.employees.unshift(ne)
    return makeResult(ne)
  }
  if (empMatch && method === 'put') {
    const idx = store.employees.findIndex((e: any) => e.id === Number(empMatch[1]))
    if (idx > -1) { store.employees[idx] = { ...store.employees[idx], ...data }; return makeResult(store.employees[idx]) }
    return makeResult(null)
  }
  if (empMatch && method === 'delete') {
    store.employees = store.employees.filter((e: any) => e.id !== Number(empMatch[1]))
    return makeResult(null)
  }
  if (url === '/employees/batch-delete' && method === 'post') {
    const ids: number[] = data?.ids || []
    store.employees = store.employees.filter((e: any) => !ids.includes(e.id))
    return makeResult(null)
  }

  if (url === '/expenses' && method === 'get') {
    let list = [...store.expenses]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      list = list.filter((e: any) => e.expenseNo.toLowerCase().includes(kw) || e.applicant.toLowerCase().includes(kw))
    }
    if (params?.type) {
      list = list.filter((e: any) => e.type === params.type)
    }
    if (params?.status) {
      list = list.filter((e: any) => e.status === params.status)
    }
    return makeResult(paginate(list, params))
  }

  const expMatch = url.match(/^\/expenses\/(\d+)$/)
  const expApproveMatch = url.match(/^\/expenses\/(\d+)\/approve$/)
  if (url === '/expenses' && method === 'post') {
    const ne = {
      ...data,
      id: ++nextId.expenses,
      expenseNo: `EXP-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-${String(nextId.expenses).padStart(3, '0')}`,
      status: 'pending',
      createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    store.expenses.unshift(ne)
    return makeResult(ne)
  }
  if (expMatch && method === 'put') {
    const idx = store.expenses.findIndex((e: any) => e.id === Number(expMatch[1]))
    if (idx > -1) { store.expenses[idx] = { ...store.expenses[idx], ...data }; return makeResult(store.expenses[idx]) }
    return makeResult(null)
  }
  if (expMatch && method === 'delete') {
    store.expenses = store.expenses.filter((e: any) => e.id !== Number(expMatch[1]))
    return makeResult(null)
  }
  if (url === '/expenses/batch-delete' && method === 'post') {
    const ids: number[] = data?.ids || []
    store.expenses = store.expenses.filter((e: any) => !ids.includes(e.id))
    return makeResult(null)
  }
  if (expApproveMatch && method === 'put') {
    const idx = store.expenses.findIndex((e: any) => e.id === Number(expApproveMatch[1]))
    if (idx > -1) {
      store.expenses[idx] = {
        ...store.expenses[idx],
        status: data?.action === 'approved' ? 'approved' : 'rejected',
        approver: '系统管理员',
        approveTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
        rejectReason: data?.rejectReason || ''
      }
      return makeResult(store.expenses[idx])
    }
    return makeResult(null)
  }

  if (url === '/users' && method === 'get') {
    let list = [...store.users]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      list = list.filter((u: any) => u.username.toLowerCase().includes(kw) || u.realName.toLowerCase().includes(kw))
    }
    if (params?.status) {
      list = list.filter((u: any) => u.status === params.status)
    }
    return makeResult(paginate(list, params))
  }

  const userMatch = url.match(/^\/users\/(\d+)$/)
  const resetPwdMatch = url.match(/^\/users\/(\d+)\/reset-password$/)
  if (url === '/users' && method === 'post') {
    const nu = { ...data, id: ++nextId.users, createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '), roleName: store.roles.find((r: any) => r.id === data?.roleId)?.name || '' }
    store.users.unshift(nu)
    return makeResult(nu)
  }
  if (userMatch && method === 'put') {
    const idx = store.users.findIndex((u: any) => u.id === Number(userMatch[1]))
    if (idx > -1) { store.users[idx] = { ...store.users[idx], ...data, roleName: store.roles.find((r: any) => r.id === data?.roleId)?.name || store.users[idx].roleName }; return makeResult(store.users[idx]) }
    return makeResult(null)
  }
  if (userMatch && method === 'delete') {
    store.users = store.users.filter((u: any) => u.id !== Number(userMatch[1]))
    return makeResult(null)
  }
  if (resetPwdMatch && method === 'put') {
    return makeResult(null)
  }

  if (url === '/roles' && method === 'get') {
    let list = [...store.roles]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      list = list.filter((r: any) => r.code.toLowerCase().includes(kw) || r.name.toLowerCase().includes(kw))
    }
    return makeResult(paginate(list, params))
  }

  if (url === '/roles/all' && method === 'get') {
    return makeResult(store.roles.map((r: any) => ({ id: r.id, name: r.name, code: r.code })))
  }

  const roleMatch = url.match(/^\/roles\/(\d+)$/)
  if (url === '/roles' && method === 'post') {
    const nr = { ...data, id: ++nextId.roles, createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ') }
    store.roles.unshift(nr)
    return makeResult(nr)
  }
  if (roleMatch && method === 'put') {
    const idx = store.roles.findIndex((r: any) => r.id === Number(roleMatch[1]))
    if (idx > -1) { store.roles[idx] = { ...store.roles[idx], ...data }; return makeResult(store.roles[idx]) }
    return makeResult(null)
  }
  if (roleMatch && method === 'delete') {
    store.roles = store.roles.filter((r: any) => r.id !== Number(roleMatch[1]))
    return makeResult(null)
  }

  if (url === '/permissions/tree' && method === 'get') {
    return makeResult(store.permissions)
  }

  if (url === '/system/setting' && method === 'get') {
    return makeResult(systemSetting)
  }
  if (url === '/system/setting' && method === 'put') {
    Object.assign(systemSetting, data)
    return makeResult(systemSetting)
  }

  return null
}
