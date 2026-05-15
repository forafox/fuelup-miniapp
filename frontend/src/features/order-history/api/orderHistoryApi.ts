import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'
import type { Order } from '@/entities/order/model/types'

async function fetchOrders(): Promise<Order[]> {
  const { data } = await apiClient.get<Order[]>('/orders')
  return data
}

async function fetchOrderById(orderId: string): Promise<Order> {
  const { data } = await apiClient.get<Order>(`/orders/${orderId}`)
  return data
}

export function useOrders() {
  return useQuery({
    queryKey: ['orders'],
    queryFn: fetchOrders,
    staleTime: 30_000,
  })
}

export function useOrder(orderId: string) {
  return useQuery({
    queryKey: ['order', orderId],
    queryFn: () => fetchOrderById(orderId),
    staleTime: 10_000,
    enabled: Boolean(orderId),
  })
}
