import type { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Masaic Latin IDE - Business Logic Made Visual',
  description: 'Transform business logic into living, testable conversations through visual storytelling.',
}

export default function LatinLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="latin-ide-container">
      {children}
    </div>
  )
} 