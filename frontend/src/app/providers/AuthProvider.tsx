import { useEffect, PropsWithChildren } from 'react'
import { useMiniApp } from './MiniAppProvider'
import { useAuth } from '@/features/auth'
import LoaderIcon from '@/shared/assets/loader.svg?react'

export function AuthProvider({ children }: PropsWithChildren): JSX.Element {
  const { isAuth, signIn } = useAuth()
  const miniApp = useMiniApp()

  useEffect(() => {
    if (!miniApp.isReady) return

    const initData = miniApp.getInitData()
    const platform = miniApp.type === 'max' ? 'MAX' : 'TELEGRAM'
    signIn(initData, platform)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [miniApp.isReady])

  if (isAuth === true) return <>{children}</>

  return (
    <div className="flex min-h-screen items-center justify-center">
      {isAuth === false ? (
        <p className="text-destructive">Не удалось пройти аутентификацию</p>
      ) : (
        <LoaderIcon className="h-16 w-16 animate-spin text-primary" />
      )}
    </div>
  )
}
