import type { ReactNode } from "react"

interface StatsCardProps {
  title: string
  value: number | string
  icon: ReactNode
  className?: string
}

export function StatsCard({ title, value, icon, className = "" }: StatsCardProps) {
  return (
    <div
      className={`bg-gray-800 rounded-lg p-4 border border-gray-700 hover:border-blue-500 transition-colors shadow-md hover:shadow-lg transform hover:-translate-y-1 duration-300 ${className}`}
    >
      <div className="flex items-center mb-3">
        <div className="p-2 rounded-md bg-gradient-to-br from-blue-500/20 to-purple-500/20 mr-3">{icon}</div>
      </div>
      <div className="text-2xl font-bold text-white mb-1">{value}</div>
      <div className="text-xs text-gray-400 uppercase tracking-wider">{title}</div>
    </div>
  )
}