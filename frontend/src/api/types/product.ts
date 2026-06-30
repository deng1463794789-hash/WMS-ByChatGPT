import type { PageParams, PageResult } from '@/types/api'

export interface Product {
  id: number
  sku: string
  name: string
  unit: string
  stockQuantity: number
  safeStock: number
  categoryId?: number
  categoryName?: string
  price?: number
  remark?: string
  status?: string
  createdAt?: string
  updatedAt?: string
}

export interface ProductQuery extends PageParams {
  keyword?: string
  categoryId?: number
  stockStatus?: string
  minStock?: number
  maxStock?: number
  sortField?: string
  sortOrder?: string
}

export interface ProductPageResult extends PageResult<Product> {}

export interface Category {
  id: number
  name: string
  parentId: number
  sort: number
  children?: Category[]
}

export interface InboundRecord {
  id: number
  inboundNo: string
  type: string
  productCount: number
  totalQuantity: number
  operator: string
  inboundTime: string
  remark?: string
  status?: string
  items?: InboundItem[]
}

export interface InboundItem {
  id: number
  productId: number
  productName: string
  sku: string
  quantity: number
  remark?: string
}

export interface InboundQuery extends PageParams {
  inboundNo?: string
  type?: string
  startDate?: string
  endDate?: string
}

export interface OutboundRecord {
  id: number
  outboundNo: string
  type: string
  productCount: number
  totalQuantity: number
  operator: string
  outboundTime: string
  remark?: string
  status?: string
  items?: OutboundItem[]
}

export interface OutboundItem {
  id: number
  productId: number
  productName: string
  sku: string
  quantity: number
  remark?: string
}

export interface OutboundQuery extends PageParams {
  outboundNo?: string
  type?: string
  startDate?: string
  endDate?: string
}
