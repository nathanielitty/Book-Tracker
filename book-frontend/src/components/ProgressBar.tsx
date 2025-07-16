interface ProgressBarProps {
  progress: number
  total: number
  className?: string
}

export function ProgressBar({ progress, total, className = "" }: ProgressBarProps) {
  const percentage = Math.min(100, Math.round((progress / total) * 100))

  return (
    <div className={`${className}`}>
      <div className="flex justify-between items-center mb-2">
        <h2 className="text-2xl font-bold text-white">📊 Reading Goal Progress</h2>
        <div className="text-lg font-semibold text-white">
          {progress} / {total} books
        </div>
      </div>

      <div className="h-8 w-full bg-gray-800 rounded-full overflow-hidden border border-gray-700">
        <div
          className="h-full bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-end pr-3 transition-all duration-1000 ease-out"
          style={{ width: `${percentage}%` }}
        >
          <span className="text-xs font-bold text-white">{percentage}%</span>
        </div>
      </div>

      <div className="mt-4 p-4 bg-gray-800/50 rounded-lg border border-gray-700">
        <p className="text-gray-300">
          {percentage >= 100
            ? "🎉 Congratulations! You've reached your reading goal for the year!"
            : `You're ${percentage}% of the way to your goal. Keep it up!`}
        </p>
      </div>
    </div>
  )
}