import request from '@/utils/request'
import type {
  Product,
  ProductQuery,
  ProductPageResult,
  Category,
  InboundRecord,
  InboundQuery,
  InboundItem,
  OutboundRecord,
  OutboundQuery,
  OutboundItem
} from './types/product'
import type { ApiResponse, PageResult } from '@/types/api'

export function getProductList(params: ProductQuery) {
  return request.get<ApiResponse<ProductPageResult>>('/products', params)
}

export function getProductDetail(id: number) {
  return request.get<ApiResponse<Product>>(`/products/${id}`)
}

export function createProduct(data: Partial<Product>) {
  return request.post<ApiResponse<Product>>('/products', data)
}

export function updateProduct(id: number, data: Partial<Product>) {
  return request.put<ApiResponse<Product>>(`/products/${id}`, data)
}

export function deleteProduct(id: number) {
  return request.delete<ApiResponse<null>>(`/products/${id}`)
}

export function batchDeleteProducts(ids: number[]) {
  return request.post<ApiResponse<null>>('/products/batch-delete', { ids })
}

export function exportProducts(params: ProductQuery) {
  return request.get('/products/export', params, { responseType: 'blob' })
}

export function getCategoryList() {
  return request.get<ApiResponse<Category[]>>('/categories')
}

export function getDashboardData() {
  return request.get<ApiResponse<{
    productCount: number
    totalStock: number
    todayInbound: number
    todayOutbound: number
    lowStockProducts: Array<{ name: string; sku: string; stockQuantity: number; safeStock: number }>
    trendData: Array<{ date: string; inbound: number; outbound: number }>
  }>>('/dashboard')
}

export function getInboundList(params: InboundQuery) {
  return request.get<ApiResponse<PageResult<InboundRecord>>>('/inbound', params)
}

export function createInbound(data: Omit<Partial<InboundRecord>, 'items'> & { items: Partial<InboundItem>[] }) {
  return request.post<ApiResponse<InboundRecord>>('/inbound', data)
}

export function getInboundDetail(id: number) {
  return request.get<ApiResponse<InboundRecord>>(`/inbound/${id}`)
}

export function getOutboundList(params: OutboundQuery) {
  return request.get<ApiResponse<PageResult<OutboundRecord>>>('/outbound', params)
}

export function createOutbound(data: Omit<Partial<OutboundRecord>, 'items'> & { items: Partial<OutboundItem>[] }) {
  return request.post<ApiResponse<OutboundRecord>>('/outbound', data)
}

export function getOutboundDetail(id: number) {
  return request.get<ApiResponse<OutboundRecord>>(`/outbound/${id}`)
}

export function updateProductStock(id: number, data: { stockQuantity: number; remark?: string }) {
  return request.put<ApiResponse<Product>>(`/products/${id}/stock`, data)
}
