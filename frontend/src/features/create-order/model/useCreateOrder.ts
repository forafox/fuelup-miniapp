import { useState, useEffect, useRef } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'
import type { Order, OrderStatus, OrderStatusEvent } from '@/entities/order'

interface CreateOrderPayload {
  gasStationId: string
  columnNumber: number
  fuelType: string
  requestedAmount: number
  clientFuelPrice: number
  paymentType: 'SBP' | 'SBP_SUBSCRIPTION' | 'BONUS'
  sbpSubscriptionId?: string
  promoCodeId?: string
  useBonus?: boolean
}

export function useCreateOrder() {
  const queryClient = useQueryClient()
  const [orderStatus, setOrderStatus] = useState<OrderStatus | null>(null)
  const [activeOrder, setActiveOrder] = useState<Order | null>(null)
  const sseRef = useRef<EventSource | null>(null)

  const mutation = useMutation({
    mutationFn: (payload: CreateOrderPayload) =>
      apiClient.post<Order>('/orders', payload).then((r) => r.data),

    onSuccess: (order) => {
      setActiveOrder(order)
      setOrderStatus(order.status)
      subscribeToStatusUpdates(order.orderId)
      queryClient.invalidateQueries({ queryKey: ['orders', 'history'] })
    },
  })

  const subscribeToStatusUpdates = (orderId: string) => {
    const token = localStorage.getItem('jwt_token')
    const sse = new EventSource(
      `/api/v1/orders/${orderId}/status-stream?token=${token}`
    )

    sse.onmessage = (event) => {
      const data: OrderStatusEvent = JSON.parse(event.data)
      setOrderStatus(data.status)

      if (data.status === 'COMPLETED' || data.status === 'CANCELLED' || data.status === 'FAILED') {
        sse.close()
        sseRef.current = null
        queryClient.invalidateQueries({ queryKey: ['orders', 'history'] })
      }
    }

    sse.onerror = () => {
      sse.close()
      sseRef.current = null
    }

    sseRef.current = sse
  }

  useEffect(() => {
    return () => sseRef.current?.close()
  }, [])

  return {
    createOrder: mutation.mutate,
    isPending: mutation.isPending,
    isError: mutation.isError,
    error: mutation.error,
    activeOrder,
    orderStatus,
  }
}
