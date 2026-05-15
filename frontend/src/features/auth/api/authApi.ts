import { apiClient } from '@/shared/api/client'

interface AuthResponse {
  token: string
  customer: {
    id: string
    firstName: string
    lastName: string | null
    username: string | null
    onboardingStatus: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED'
    bonusBalance: number
  }
}

export const authApi = {
  signIn: (initData: string, platform: 'TELEGRAM' | 'MAX') =>
    apiClient.post<AuthResponse>('/auth/messenger', { initData, platform }).then((r) => r.data),
}
