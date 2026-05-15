import { describe, it, expect, vi, beforeEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { createElement } from 'react'
import { useCreateOrder } from './useCreateOrder'

// мокаем axios через vi.mock
vi.mock('@/shared/api/client', () => ({
  apiClient: {
    post: vi.fn(),
    get: vi.fn(),
  },
}))

function createWrapper() {
  const qc = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return ({ children }: { children: any }) =>
    createElement(QueryClientProvider, { client: qc }, children)
}

describe('useCreateOrder', () => {
  it('initial state: no active order, no status', () => {
    const { result } = renderHook(() => useCreateOrder(), { wrapper: createWrapper() })
    expect(result.current.activeOrder).toBeNull()
    expect(result.current.orderStatus).toBeNull()
    expect(result.current.isPending).toBe(false)
  })
})
