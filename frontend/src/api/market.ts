import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

export interface MarketQuote {
  symbol: string
  name: string
  currency: string
  marketState: string
  price: number
  previousClose: number
  open: number
  dayHigh: number
  dayLow: number
  volume: number
  change: number
  changePercent: number
  quoteTime: string
  source: string
  trendPoints?: MarketTrendPoint[]
}

export interface MarketTrendPoint {
  time: string
  price: number
  averagePrice: number
  volume: number
  amount: number
}

export function getMarketQuotes(symbols: string[]) {
  return request.get<ApiResponse<MarketQuote[]>>('/market/quotes', {
    symbols: symbols.join(',')
  })
}
