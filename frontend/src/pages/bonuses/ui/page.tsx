import { useBonusBalance, useBonusTransactions } from '@/features/bonus-history/api/bonusHistoryApi'
import type { BonusTransaction, BonusTransactionType } from '@/entities/bonus/model/types'

const TYPE_LABEL: Record<BonusTransactionType, string> = {
  ACCRUAL: 'Начисление',
  WITHDRAWAL: 'Списание',
  WELCOME_BONUS: 'Приветственный бонус',
  REFERRAL_BONUS: 'Реферальный бонус',
  EXPIRATION: 'Сгорание',
}

export function BonusesPage() {
  const { data: balance, isLoading: balanceLoading } = useBonusBalance()
  const { data: transactions, isLoading: txLoading } = useBonusTransactions()

  return (
    <div className="flex flex-col h-screen bg-background">
      <header className="border-b border-border px-4 py-3">
        <h1 className="text-base font-semibold">Бонусный счёт</h1>
      </header>

      <div className="flex-1 overflow-y-auto">
        <div className="bg-primary m-4 rounded-2xl p-5 text-primary-foreground">
          {balanceLoading ? (
            <div className="h-10 w-28 animate-pulse rounded-lg bg-white/20" />
          ) : (
            <>
              <p className="text-sm opacity-80">Доступно</p>
              <p className="text-3xl font-bold">{balance?.balance ?? 0}</p>
              <p className="text-sm opacity-70 mt-0.5">бонусов</p>
            </>
          )}

          {balance && (
            <div className="mt-3 grid grid-cols-2 gap-2 text-xs opacity-80">
              <div>
                <p>Заработано</p>
                <p className="font-semibold">{balance.totalEarned}</p>
              </div>
              <div>
                <p>Потрачено</p>
                <p className="font-semibold">{balance.totalSpent}</p>
              </div>
            </div>
          )}
        </div>

        <div className="px-4">
          <h2 className="text-sm font-medium text-muted-foreground mb-2">История операций</h2>

          {txLoading && (
            <div className="space-y-2">
              {Array.from({ length: 5 }).map((_, i) => (
                <div key={i} className="h-14 rounded-xl bg-muted animate-pulse" />
              ))}
            </div>
          )}

          {transactions?.length === 0 && (
            <p className="text-center text-sm text-muted-foreground py-8">Операций пока нет</p>
          )}

          <div className="space-y-2 pb-6">
            {transactions?.map((tx) => (
              <TransactionRow key={tx.id} tx={tx} />
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

function TransactionRow({ tx }: { tx: BonusTransaction }) {
  const isCredit = tx.type === 'ACCRUAL' || tx.type === 'WELCOME_BONUS' || tx.type === 'REFERRAL_BONUS'

  return (
    <div className="flex items-center justify-between rounded-xl border border-border bg-card px-4 py-3">
      <div>
        <p className="text-sm font-medium">{TYPE_LABEL[tx.type]}</p>
        {tx.description && (
          <p className="text-xs text-muted-foreground mt-0.5">{tx.description}</p>
        )}
        <p className="text-xs text-muted-foreground">
          {new Date(tx.timestamp).toLocaleDateString('ru-RU')}
        </p>
      </div>
      <span className={`text-sm font-semibold tabular-nums ${isCredit ? 'text-green-600' : 'text-muted-foreground'}`}>
        {isCredit ? '+' : '−'}{Math.abs(tx.amount)}
      </span>
    </div>
  )
}
