'use client'

import { useState } from 'react'
import { LatinIDEWelcome } from './components/LatinIDEWelcome'
import { LatinIDEWorkspace } from './components/LatinIDEWorkspace'

type ViewMode = 'welcome' | 'editor'

export default function LatinPage() {
  const [currentView, setCurrentView] = useState<ViewMode>('welcome')
  const [selectedTemplate, setSelectedTemplate] = useState<string>('')

  const handleCreateProject = (templateId: string) => {
    setSelectedTemplate(templateId)
    setCurrentView('editor')
  }

  const handleBackToWelcome = () => {
    setCurrentView('welcome')
    setSelectedTemplate('')
  }

  return (
    <>
      {currentView === 'welcome' && (
        <LatinIDEWelcome onCreateProject={handleCreateProject} />
      )}
      {currentView === 'editor' && (
        <LatinIDEWorkspace 
          onBack={handleBackToWelcome}
          template={selectedTemplate}
        />
      )}
    </>
  )
} 