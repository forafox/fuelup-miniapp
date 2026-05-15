import { createRouter, createRoute, createRootRoute } from '@tanstack/react-router'
import { IndexPage } from '@/pages/index/ui/page'
import { CrmPage } from '@/pages/crm/ui/page'
import { OrderPage } from '@/pages/order/ui/page'
import { BonusesPage } from '@/pages/bonuses/ui/page'

const rootRoute = createRootRoute()

const indexRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  component: IndexPage,
})

const bonusesRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/bonuses',
  component: BonusesPage,
})

const orderRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/orders/$orderId',
  component: OrderPage,
})

const crmRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/crm',
  component: CrmPage,
})

const routeTree = rootRoute.addChildren([
  indexRoute,
  bonusesRoute,
  orderRoute,
  crmRoute,
])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
