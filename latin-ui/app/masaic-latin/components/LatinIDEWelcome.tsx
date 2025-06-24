'use client'

import { useState } from 'react'
import { Plus, Sparkles, MessageCircle, Clock, Mail } from 'lucide-react'

interface Template {
  id: string;
  title: string;
  description: string;
  icon: React.ComponentType<any>;
  color: string;
  example: string;
}

const templates: Template[] = [
  {
    id: 'customer-service',
    title: 'Customer Service Starter Kit',
    description: 'Handle customer inquiries, complaints, and support requests with intelligent responses.',
    icon: MessageCircle,
    color: 'from-blue-500/20 to-blue-600/20',
    example: 'Password resets, billing questions, account issues'
  },
  {
    id: 'bootstrap-agent',
    title: 'Bootstrap Agent Kit',
    description: 'Quick-start template for general business automation and decision-making.',
    icon: Sparkles,
    color: 'from-purple-500/20 to-pink-600/20',
    example: 'Policy checks, approvals, routing decisions'
  },
  {
    id: 'workflow-template',
    title: 'Long-Running Workflow',
    description: 'Design processes that span time - scheduling, monitoring, and multi-step operations.',
    icon: Clock,
    color: 'from-green-500/20 to-emerald-600/20',
    example: 'Meeting scheduling, device monitoring, follow-ups'
  },
  {
    id: 'async-workflow',
    title: 'Email & Async Workflow',
    description: 'Handle email communications and asynchronous business processes.',
    icon: Mail,
    color: 'from-orange-500/20 to-red-600/20',
    example: 'Email responses, notifications, async approvals'
  }
]

interface LatinIDEWelcomeProps {
  onCreateProject: (templateId: string) => void;
}

export function LatinIDEWelcome({ onCreateProject }: LatinIDEWelcomeProps) {
  const [selectedTemplate, setSelectedTemplate] = useState<string | null>(null)

  const handleCreateProject = (templateId: string) => {
    onCreateProject(templateId)
  }

  return (
    <div className="min-h-screen flex flex-col bg-canvas-deep-dark">
      {/* Header */}
      <header className="px-8 py-6 border-b border-soft-neutral-white/10 bg-panel-dark/50 backdrop-blur-sm">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 bg-masaic-brand rounded-lg flex items-center justify-center shadow-masaic-float">
              <Sparkles className="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 className="text-xl font-semibold text-soft-neutral-white tracking-masaic">Latin IDE</h1>
              <p className="text-sm text-warm-whisper-grey">Business Logic Made Visual</p>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 px-8 py-12">
        <div className="max-w-6xl mx-auto">
          {/* Welcome Section */}
          <div className="text-center mb-16 animate-masaic-rise">
            <h2 className="text-4xl font-bold text-soft-neutral-white mb-4 tracking-masaic">
              Welcome to Latin
            </h2>
            <p className="text-xl text-warm-whisper-grey mb-8 max-w-3xl mx-auto leading-relaxed">
              Where business logic becomes living conversations. Create intelligent responses 
              to customer scenarios through <em className="text-soft-neutral-white">visual storytelling</em>, not programming.
            </p>
            <div className="inline-flex items-center px-6 py-3 bg-masaic-brand text-white rounded-xl font-medium shadow-masaic-float hover:shadow-masaic-depth transition-all duration-300 cursor-pointer group animate-masaic-glow-pulse">
              <Plus className="w-5 h-5 mr-2 group-hover:scale-110 transition-transform" />
              Start Creating
            </div>
          </div>

          {/* Template Gallery */}
          <div className="mb-12">
            <h3 className="text-2xl font-semibold text-soft-neutral-white mb-2 tracking-masaic flex items-center">
              <div className="w-6 h-[1px] bg-gradient-to-r from-accent to-transparent mr-4" />
              Choose Your Starting Point
            </h3>
            <p className="text-warm-whisper-grey mb-8">
              Select a template that matches your use case, or start from scratch.
            </p>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {templates.map((template) => (
                <div
                  key={template.id}
                  className={`group relative p-6 rounded-2xl border transition-all duration-300 cursor-pointer
                    ${selectedTemplate === template.id 
                      ? 'border-accent/50 shadow-masaic-depth bg-gradient-to-br from-panel-dark/80 to-canvas-deep-dark ring-1 ring-accent/30' 
                      : 'border-soft-neutral-white/10 hover:border-accent/30 hover:shadow-masaic-float bg-panel-dark/40 backdrop-blur-sm'
                    }`}
                  onClick={() => setSelectedTemplate(template.id)}
                >
                  {/* Masaic Brand Accent */}
                  <div className="absolute top-4 right-4 opacity-20 group-hover:opacity-40 transition-opacity duration-300">
                    <div className="relative w-3 h-3">
                      <div className="absolute inset-0 bg-gradient-to-br from-accent via-primary to-accent rounded-full animate-pulse-faint" />
                      <div className="absolute inset-0.5 bg-canvas-deep-dark rounded-full" />
                      <div className="absolute inset-1 bg-gradient-to-br from-primary to-accent rounded-full" />
                    </div>
                  </div>

                  <div className="flex items-start space-x-4">
                    <div className={`p-3 rounded-xl bg-gradient-to-br ${template.color} group-hover:scale-105 transition-transform shadow-sm`}>
                      <template.icon className="w-6 h-6 text-soft-neutral-white" />
                    </div>
                    <div className="flex-1">
                      <h4 className="text-lg font-semibold text-soft-neutral-white mb-2 group-hover:text-accent transition-colors">
                        {template.title}
                      </h4>
                      <p className="text-warm-whisper-grey text-sm mb-3 leading-relaxed">
                        {template.description}
                      </p>
                      <div className="text-xs text-cool-silver-grey bg-canvas-deep-dark/60 px-3 py-2 rounded-lg border border-soft-neutral-white/5">
                        <strong className="text-soft-neutral-white">Examples:</strong> {template.example}
                      </div>
                    </div>
                  </div>
                  
                  {selectedTemplate === template.id && (
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        handleCreateProject(template.id)
                      }}
                      className="absolute bottom-4 right-4 px-4 py-2 bg-masaic-brand text-white rounded-lg text-sm font-medium hover:bg-primary/90 transition-all shadow-masaic-float hover:shadow-masaic-depth animate-masaic-rise"
                    >
                      Create Project
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Quick Demo Section */}
          <div className="bg-gradient-to-r from-panel-dark/60 to-canvas-deep-dark/80 rounded-2xl p-8 text-center backdrop-blur-sm border border-soft-neutral-white/10">
            <h3 className="text-xl font-semibold text-soft-neutral-white mb-3 tracking-masaic flex items-center justify-center">
              <div className="w-4 h-[1px] bg-gradient-to-r from-transparent via-accent to-transparent mr-3" />
              See Latin in Action
              <div className="w-4 h-[1px] bg-gradient-to-r from-transparent via-accent to-transparent ml-3" />
            </h3>
            <p className="text-warm-whisper-grey mb-6">
              Watch how a simple customer service scenario becomes a powerful, testable conversation.
            </p>
            <button className="inline-flex items-center px-6 py-3 bg-panel-dark/80 text-soft-neutral-white rounded-xl font-medium shadow-masaic-float hover:shadow-masaic-depth transition-all duration-300 border border-soft-neutral-white/20 hover:border-accent/50 backdrop-blur-sm group">
              <Sparkles className="w-5 h-5 mr-2 group-hover:text-accent transition-colors" />
              View Interactive Demo
            </button>
          </div>
        </div>  
      </main>
    </div>
  )
} 