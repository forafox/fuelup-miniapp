import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider } from '@tanstack/react-router'
import { MiniAppProvider } from './providers/MiniAppProvider'
import { AuthProvider } from './providers/AuthProvider'
import { router } from './routes'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: 2,
    },
  },
})

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <MiniAppProvider>
        <AuthProvider>
          <RouterProvider router={router} />
        </AuthProvider>
      </MiniAppProvider>
    </QueryClientProvider>
  )
}
