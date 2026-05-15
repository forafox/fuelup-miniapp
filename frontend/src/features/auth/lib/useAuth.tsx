import { useState, useCallback } from 'react'
import { authApi } from '../api/authApi'

type AuthState = boolean | null

export function useAuth() {
  const [isAuth, setIsAuth] = useState<AuthState>(null)

  const signIn = useCallback(async (initData: string | undefined, platform: 'TELEGRAM' | 'MAX') => {
    if (!initData) {
      setIsAuth(false)
      return
    }
    try {
      const result = await authApi.signIn(initData, platform)
      localStorage.setItem('jwt_token', result.token)
      setIsAuth(true)
    } catch {
      setIsAuth(false)
    }
  }, [])

  const signOut = useCallback(() => {
    localStorage.removeItem('jwt_token')
    setIsAuth(false)
  }, [])

  return { isAuth, signIn, signOut }
}
