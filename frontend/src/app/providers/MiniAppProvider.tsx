import { createContext, useContext, useEffect, useState, PropsWithChildren } from 'react'

type MiniAppType = 'telegram' | 'max' | 'unknown'

interface MiniAppContextValue {
  type: MiniAppType
  isReady: boolean
  getInitData: () => string | undefined
  setBackButtonVisible: (visible: boolean) => void
  onBackButtonPressed: (handler: () => void) => () => void
}

const MiniAppContext = createContext<MiniAppContextValue | null>(null)

export function MiniAppProvider({ children }: PropsWithChildren) {
  const [type, setType] = useState<MiniAppType>('unknown')
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    const tg = (window as any).Telegram?.WebApp
    const vk = (window as any).VK?.MiniApps

    if (tg) {
      tg.ready()
      tg.expand()
      setType('telegram')
    } else if (vk) {
      setType('max')
    }

    setIsReady(true)
  }, [])

  const getInitData = () => {
    if (type === 'telegram') {
      return (window as any).Telegram?.WebApp?.initData
    }
    if (type === 'max') {
      return (window as any).__MAX_INIT_DATA__
    }
    return undefined
  }

  const setBackButtonVisible = (visible: boolean) => {
    if (type === 'telegram') {
      const tg = (window as any).Telegram?.WebApp
      visible ? tg?.BackButton?.show() : tg?.BackButton?.hide()
    }
  }

  const onBackButtonPressed = (handler: () => void) => {
    if (type === 'telegram') {
      const tg = (window as any).Telegram?.WebApp
      tg?.BackButton?.onClick(handler)
      return () => tg?.BackButton?.offClick(handler)
    }
    return () => {}
  }

  return (
    <MiniAppContext.Provider value={{ type, isReady, getInitData, setBackButtonVisible, onBackButtonPressed }}>
      {children}
    </MiniAppContext.Provider>
  )
}

export const useMiniApp = () => {
  const ctx = useContext(MiniAppContext)
  if (!ctx) throw new Error('useMiniApp must be used inside MiniAppProvider')
  return ctx
}
