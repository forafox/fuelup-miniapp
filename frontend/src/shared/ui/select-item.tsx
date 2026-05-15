import { PropsWithChildren } from 'react'
import { cn } from '@/shared/lib/cn'

interface SelectItemProps extends PropsWithChildren {
  id: string
  title: string
  selected: boolean
  onClick: () => void
  disabled?: boolean
}

export function SelectItem({ id, title, selected, onClick, disabled, children }: SelectItemProps) {
  return (
    <button
      id={id}
      role="option"
      aria-selected={selected}
      onClick={onClick}
      disabled={disabled}
      className={cn(
        'flex w-full items-center justify-between rounded-xl border px-3 py-2.5 text-left transition-colors',
        selected
          ? 'border-primary bg-primary/10 text-primary'
          : 'border-border bg-background hover:bg-accent',
        disabled && 'cursor-not-allowed opacity-50'
      )}
    >
      {children}
    </button>
  )
}
