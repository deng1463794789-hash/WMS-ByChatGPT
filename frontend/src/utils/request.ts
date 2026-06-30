import axios, { type AxiosAdapter, type AxiosInstance, type AxiosRequestConfig, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from './storage'
import { isMockEnabled, handleMockRequest } from '@/mock'
import type { ApiResponse } from '@/types/api'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 15000
})

const originalAdapter = axios.getAdapter(service.defaults.adapter) as AxiosAdapter

service.defaults.adapter = (config: InternalAxiosRequestConfig) => {
  if (isMockEnabled()) {
    const mockResult = handleMockRequest(
      config.url || '',
      (config.method || 'get').toLowerCase(),
      config.data,
      config.params
    )
    if (mockResult !== null && mockResult !== undefined) {
      if (mockResult instanceof Blob) {
        return Promise.resolve({
          data: mockResult,
          status: 200,
          statusText: 'OK',
          headers: {},
          config,
          request: {}
        } as AxiosResponse)
      }
      return Promise.resolve({
        data: mockResult,
        status: 200,
        statusText: 'OK',
        headers: {},
        config,
        request: {}
      } as AxiosResponse)
    }
  }
  return originalAdapter(config)
}

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    if (response.config.responseType === 'blob' || response.data instanceof Blob) {
      return response.data
    }
    const res = response.data
    if (res.code !== 200 && res.code !== 0) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return response.data as any
  },
  (error) => {
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

const request = {
  get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, { params, ...config })
  },
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config)
  },
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config)
  },
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, config)
  }
}

export default request
