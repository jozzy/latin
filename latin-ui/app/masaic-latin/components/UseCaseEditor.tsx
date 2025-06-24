'use client'

import { useState } from 'react'
import { ArrowLeft, Play, Save, Eye, MessageSquare, Settings } from 'lucide-react'

interface UseCase {
  id: string;
  name: string;
  description: string;
  steps: string[];
  solution: string;
  alternativeSolution?: string;
  fallbackSolution?: string;
  examples: string[];
  conditions?: string[];
}

interface UseCaseEditorProps {
  onBack: () => void;
  template?: string;
}

export function UseCaseEditor({ onBack, template }: UseCaseEditorProps) {
  const [useCase, setUseCase] = useState<UseCase>({
    id: '',
    name: '',
    description: '',
    steps: [''],
    solution: '',
    alternativeSolution: '',
    fallbackSolution: '',
    examples: [''],
    conditions: []
  })

  const [isTestMode, setIsTestMode] = useState(false)
  const [testInput, setTestInput] = useState('')
  const [testResult, setTestResult] = useState('')

  const handleTest = () => {
    // Simulate testing the use case
    setTestResult(`Based on your use case "${useCase.name}", here's how I would respond:\n\n${useCase.solution}`)
  }

  const addStep = () => {
    setUseCase(prev => ({
      ...prev,
      steps: [...prev.steps, '']
    }))
  }

  const updateStep = (index: number, value: string) => {
    setUseCase(prev => ({
      ...prev,
      steps: prev.steps.map((step, i) => i === index ? value : step)
    }))
  }

  const addExample = () => {
    setUseCase(prev => ({
      ...prev,
      examples: [...prev.examples, '']
    }))
  }

  const updateExample = (index: number, value: string) => {
    setUseCase(prev => ({
      ...prev,
      examples: prev.examples.map((example, i) => i === index ? value : example)
    }))
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-soft-neutral-white to-pure-white">
      {/* Header */}
      <header className="px-8 py-6 border-b border-warm-whisper-grey/30 bg-white/80 backdrop-blur-sm sticky top-0 z-10">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <button
              onClick={onBack}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <div>
              <h1 className="text-xl font-semibold text-gray-900 tracking-masaic">
                {useCase.name || 'New Use Case'}
              </h1>
              <p className="text-sm text-gray-600">
                {template ? `Based on ${template} template` : 'Custom use case'}
              </p>
            </div>
          </div>
          
          <div className="flex items-center space-x-3">
            <button
              onClick={() => setIsTestMode(!isTestMode)}
              className={`px-4 py-2 rounded-lg font-medium transition-all ${
                isTestMode 
                  ? 'bg-primary text-white shadow-md' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              <Eye className="w-4 h-4 mr-2 inline" />
              {isTestMode ? 'Edit Mode' : 'Test Mode'}
            </button>
            <button className="px-4 py-2 bg-masaic-brand text-white rounded-lg font-medium shadow-masaic-float hover:shadow-masaic-depth transition-all">
              <Save className="w-4 h-4 mr-2 inline" />
              Save
            </button>
          </div>
        </div>
      </header>

      <div className="flex flex-1">
        {/* Main Editor */}
        <div className={`transition-all duration-300 ${isTestMode ? 'w-1/2' : 'w-full'}`}>
          <div className="p-8 max-w-4xl mx-auto">
            <div className="animate-masaic-rise">
              {/* Use Case Name */}
              <div className="mb-8">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Use Case Name
                </label>
                <input
                  type="text"
                  value={useCase.name}
                  onChange={(e) => setUseCase(prev => ({ ...prev, name: e.target.value }))}
                  placeholder="e.g., password_reset, billing_inquiry, account_setup"
                  className="w-full px-4 py-3 border border-warm-whisper-grey/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary text-lg"
                />
                <p className="text-xs text-gray-500 mt-1">
                  Use lowercase with underscores. This will be the unique identifier.
                </p>
              </div>

              {/* Description */}
              <div className="mb-8">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description
                </label>
                <textarea
                  value={useCase.description}
                  onChange={(e) => setUseCase(prev => ({ ...prev, description: e.target.value }))}
                  placeholder="Describe the customer's situation or query in plain English..."
                  rows={3}
                  className="w-full px-4 py-3 border border-warm-whisper-grey/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary resize-none"
                />
              </div>

              {/* Steps (Optional) */}
              <div className="mb-8">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Steps (Optional)
                </label>
                <p className="text-sm text-gray-600 mb-4">
                  Break down the process into steps if needed. This helps structure complex scenarios.
                </p>
                {useCase.steps.map((step, index) => (
                  <div key={index} className="mb-3">
                    <input
                      type="text"
                      value={step}
                      onChange={(e) => updateStep(index, e.target.value)}
                      placeholder={`Step ${index + 1}: What should happen first?`}
                      className="w-full px-4 py-3 border border-warm-whisper-grey/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary"
                    />
                  </div>
                ))}
                <button
                  onClick={addStep}
                  className="text-sm text-primary hover:text-primary/80 font-medium"
                >
                  + Add another step
                </button>
              </div>

              {/* Solution */}
              <div className="mb-8">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Solution
                </label>
                <textarea
                  value={useCase.solution}
                  onChange={(e) => setUseCase(prev => ({ ...prev, solution: e.target.value }))}
                  placeholder="How should the agent respond? Write in natural language as if instructing a helpful colleague..."
                  rows={4}
                  className="w-full px-4 py-3 border border-warm-whisper-grey/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary resize-none"
                />
              </div>

              {/* Alternative Solution */}
              <div className="mb-8">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Alternative Solution (Optional)
                </label>
                <textarea
                  value={useCase.alternativeSolution}
                  onChange={(e) => setUseCase(prev => ({ ...prev, alternativeSolution: e.target.value }))}
                  placeholder="If the primary solution doesn't work, what should be tried next?"
                  rows={3}
                  className="w-full px-4 py-3 border border-warm-whisper-grey/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary resize-none"
                />
              </div>

              {/* Examples */}
              <div className="mb-8">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Example Triggers
                </label>
                <p className="text-sm text-gray-600 mb-4">
                  What might customers say that should trigger this use case?
                </p>
                {useCase.examples.map((example, index) => (
                  <div key={index} className="mb-3">
                    <input
                      type="text"
                      value={example}
                      onChange={(e) => updateExample(index, e.target.value)}
                      placeholder={`"I forgot my password" or "Can't access my account"`}
                      className="w-full px-4 py-3 border border-warm-whisper-grey/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary"
                    />
                  </div>
                ))}
                <button
                  onClick={addExample}
                  className="text-sm text-primary hover:text-primary/80 font-medium"
                >
                  + Add another example
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Test Panel */}
        {isTestMode && (
          <div className="w-1/2 border-l border-warm-whisper-grey/30 bg-white">
            <div className="p-8">
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-2 flex items-center">
                  <MessageSquare className="w-5 h-5 mr-2" />
                  Test Your Use Case
                </h3>
                <p className="text-sm text-gray-600">
                  Try different customer inputs to see how your use case responds.
                </p>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Customer Input
                  </label>
                  <textarea
                    value={testInput}
                    onChange={(e) => setTestInput(e.target.value)}
                    placeholder="Type what a customer might say..."
                    rows={3}
                    className="w-full px-4 py-3 border border-warm-whisper-grey/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary resize-none"
                  />
                </div>

                <button
                  onClick={handleTest}
                  className="w-full px-4 py-3 bg-primary text-white rounded-xl font-medium hover:bg-primary/90 transition-colors flex items-center justify-center"
                >
                  <Play className="w-4 h-4 mr-2" />
                  Test Response
                </button>

                {testResult && (
                  <div className="bg-gray-50 rounded-xl p-4">
                    <h4 className="font-medium text-gray-900 mb-2">Agent Response:</h4>
                    <div className="text-sm text-gray-700 whitespace-pre-wrap">
                      {testResult}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
} 