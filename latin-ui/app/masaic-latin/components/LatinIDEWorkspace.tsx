'use client'

import { useState, useEffect } from 'react'
import { 
  ArrowLeft, 
  Folder, 
  FolderOpen, 
  File, 
  Plus, 
  Play, 
  Save, 
  Eye, 
  MessageSquare, 
  Settings,
  ChevronRight,
  ChevronDown,
  Hash,
  Code,
  TestTube,
  Zap,
  X,
  Users,
  Target,
  Shuffle
} from 'lucide-react'

interface FileNode {
  id: string;
  name: string;
  type: 'folder' | 'file';
  content?: string;
  children?: FileNode[];
  isOpen?: boolean;
  isSelected?: boolean;
  channel?: 'voice' | 'chat' | 'email' | 'web';
  testStatus?: 'untested' | 'passed' | 'failed' | 'testing';
  isDeployed?: boolean;
  lastTested?: Date;
}

interface LatinIDEWorkspaceProps {
  onBack: () => void;
  template?: string;
}

const initialProjectStructure: FileNode[] = [
  {
    id: 'billing-conflict',
    name: 'billing-conflict',
    type: 'folder',
    isOpen: true,
    children: [
      {
        id: 'billing-conflict-voice',
        name: 'voice',
        type: 'folder',
        isOpen: false,
        children: [
          {
            id: 'billing-conflict-voice-main.latin',
            name: 'main.latin',
            type: 'file',
            channel: 'voice',
            testStatus: 'passed',
            content: `### UseCase: billing_conflict
#### Description
Customer disputes a charge on their bill over voice call.

#### Voice-Specific Behavior
- Use empathetic tone and slower pace
- Confirm understanding by repeating key details
- Offer to email summary of resolution

#### Solution
"I completely understand your concern about this charge. Let me look into this right away for you. [pause] I can see the charge you're referring to from [date]. This appears to be for [service]. However, I want to make sure we resolve this properly for you."

#### Examples
- "I don't understand this charge on my bill"
- "There's a weird fee I didn't expect"
- "Can you explain this billing item?"`
          }
        ]
      },
      {
        id: 'billing-conflict-chat',
        name: 'chat',
        type: 'folder',
        isOpen: false,
        children: [
          {
            id: 'billing-conflict-chat-main.latin',
            name: 'main.latin',
            type: 'file',
            channel: 'chat',
            testStatus: 'untested',
            content: `### UseCase: billing_conflict
#### Description
Customer disputes a charge on their bill via chat.

#### Chat-Specific Behavior
- Provide quick, scannable responses
- Use bullet points for clarity
- Include relevant links and screenshots

#### Solution
I can help you understand this charge! Let me review your account quickly.

✅ **Found the charge:** [Amount] on [Date]
📋 **For service:** [Service name]
🔍 **Details:** [Brief explanation]

Would you like me to:
• Explain this charge in detail
• Process a refund if it's an error
• Transfer you to billing specialist

#### Examples
- "What is this charge for?"
- "I don't recognize this fee"
- "This billing looks wrong"`
          }
        ]
      }
    ]
  },
  {
    id: 'password-reset',
    name: 'password-reset',
    type: 'folder',
    isOpen: false,
    children: [
      {
        id: 'password-reset-chat',
        name: 'chat',
        type: 'folder',
        children: [
          {
            id: 'password-reset-chat-main.latin',
            name: 'main.latin',
            type: 'file',
            channel: 'chat',
            testStatus: 'failed',
            content: `### UseCase: password_reset
#### Description
Customer needs help resetting their password via chat.

#### Solution
I'll help you reset your password right away! Please provide your registered email address and I'll send you a secure reset link.

#### Examples
- "I forgot my password"
- "Can't log into my account"
- "Need to reset password"`
          }
        ]
      },
      {
        id: 'password-reset-email',
        name: 'email',
        type: 'folder',
        children: [
          {
            id: 'password-reset-email-main.latin',
            name: 'main.latin',
            type: 'file',
            channel: 'email',
            testStatus: 'passed',
            content: `### UseCase: password_reset
#### Description
Customer requests password reset via email.

#### Email-Specific Behavior
- Formal tone with clear instructions
- Include security reminders
- Provide contact info for further help

#### Solution
Dear [Customer Name],

I've received your request to reset your password. For your security, please follow these steps:

1. Click the secure link below (valid for 24 hours)
2. Enter your new password (minimum 8 characters)
3. Confirm your new password

[SECURE RESET LINK]

If you didn't request this reset, please contact us immediately.

Best regards,
Customer Support Team

#### Examples
- "Password reset request"
- "Cannot access account"`
          }
        ]
      }
    ]
  },
  {
    id: 'account-setup',
    name: 'account-setup',
    type: 'folder',
    isOpen: false,
    children: [
      {
        id: 'account-setup-voice',
        name: 'voice',
        type: 'folder',
        children: [
          {
            id: 'account-setup-voice-main.latin',
            name: 'main.latin',
            type: 'file',
            channel: 'voice',
            testStatus: 'testing',
            content: `### UseCase: account_setup
#### Description
New customer needs help setting up their account over phone.

#### Voice-Specific Behavior
- Welcome them warmly to the service
- Walk through setup step-by-step
- Confirm each step before proceeding

#### Solution
"Welcome to our service! I'm excited to help you get set up today. Let's walk through this together step by step, and please feel free to ask questions at any time..."

#### Examples
- "I need help setting up my account"
- "I'm new and don't know where to start"
- "Can you walk me through the setup?"`
          }
        ]
      }
    ]
  }
]

export function LatinIDEWorkspace({ onBack, template }: LatinIDEWorkspaceProps) {
  const [projectStructure, setProjectStructure] = useState<FileNode[]>(initialProjectStructure)
  const [selectedFile, setSelectedFile] = useState<FileNode | null>(null)
  const [selectedFiles, setSelectedFiles] = useState<Set<string>>(new Set())
  const [isTestMode, setIsTestMode] = useState(false)
  const [testInput, setTestInput] = useState('')
  const [testResult, setTestResult] = useState('')
  const [channelFilter, setChannelFilter] = useState<'all' | 'voice' | 'chat' | 'email' | 'web'>('all')
  const [testsRunning, setTestsRunning] = useState(false)
  const [showDeployModal, setShowDeployModal] = useState(false)
  const [selectedStrategy, setSelectedStrategy] = useState<string>('')
  const [customStrategy, setCustomStrategy] = useState<string>('')
  const [deploymentPercentage, setDeploymentPercentage] = useState<number>(100)

  useEffect(() => {
    // Auto-select the first use case file
    const firstFile = projectStructure[0]?.children?.find(child => 
      child.type === 'folder' && child.name === 'billing'
    )?.children?.find(child => 
      child.type === 'file' && child.name === 'billing_conflict.md'
    )
    
    if (firstFile) {
      setSelectedFile(firstFile)
    }
  }, [])

  const toggleFolder = (nodeId: string) => {
    const updateNode = (nodes: FileNode[]): FileNode[] => {
      return nodes.map(node => {
        if (node.id === nodeId && node.type === 'folder') {
          return { ...node, isOpen: !node.isOpen }
        }
        if (node.children) {
          return { ...node, children: updateNode(node.children) }
        }
        return node
      })
    }
    setProjectStructure(updateNode(projectStructure))
  }

  const selectFile = (file: FileNode, ctrlKey = false) => {
    if (file.type === 'file') {
      if (ctrlKey) {
        // Multi-select
        const newSelected = new Set(selectedFiles)
        if (newSelected.has(file.id)) {
          newSelected.delete(file.id)
        } else {
          newSelected.add(file.id)
        }
        setSelectedFiles(newSelected)
        // Keep the last selected file as the primary selection for content viewing
        if (!newSelected.has(file.id)) {
          // If we just deselected this file, find another selected file to show
          const remainingFiles = Array.from(newSelected)
          if (remainingFiles.length > 0) {
            const findNode = (nodes: FileNode[], id: string): FileNode | null => {
              for (const node of nodes) {
                if (node.id === id) return node
                if (node.children) {
                  const found = findNode(node.children, id)
                  if (found) return found
                }
              }
              return null
            }
            setSelectedFile(findNode(projectStructure, remainingFiles[0]))
          } else {
            setSelectedFile(null)
          }
        } else {
          setSelectedFile(file)
        }
      } else {
        // Single select
        setSelectedFile(file)
        setSelectedFiles(new Set([file.id]))
      }
    }
  }

  const selectFolder = (folder: FileNode) => {
    if (folder.type === 'folder' && folder.children) {
      // Select all files in the folder (filtered by current channel filter)
      const getAllFiles = (nodes: FileNode[]): FileNode[] => {
        let files: FileNode[] = []
        for (const node of nodes) {
          if (node.type === 'file' && shouldShowFile(node)) {
            files.push(node)
          } else if (node.type === 'folder' && node.children) {
            files = files.concat(getAllFiles(node.children))
          }
        }
        return files
      }
      
      const folderFiles = getAllFiles(folder.children)
      const fileIds = folderFiles.map(f => f.id)
      
      // Check if all files in folder are already selected
      const allSelected = fileIds.every(id => selectedFiles.has(id))
      
      if (allSelected) {
        // Deselect all files in folder
        const newSelected = new Set(selectedFiles)
        fileIds.forEach(id => newSelected.delete(id))
        setSelectedFiles(newSelected)
      } else {
        // Select all files in folder
        const newSelected = new Set([...selectedFiles, ...fileIds])
        setSelectedFiles(newSelected)
        // Set the first file as the primary selection for viewing
        if (folderFiles.length > 0) {
          setSelectedFile(folderFiles[0])
        }
      }
    }
  }

  const shouldShowFile = (file: FileNode): boolean => {
    if (channelFilter === 'all') return true
    if (file.type === 'folder') return true
    return file.channel === channelFilter
  }

  const filterTree = (nodes: FileNode[]): FileNode[] => {
    return nodes.map(node => {
      if (node.type === 'folder' && node.children) {
        const filteredChildren = filterTree(node.children).filter(shouldShowFile)
        return { ...node, children: filteredChildren }
      }
      return node
    }).filter(shouldShowFile)
  }

  const handleTest = () => {
    setTestsRunning(true)
    
    if (selectedFiles.size > 1) {
      // Multi-file testing
      setTestResult(`Testing ${selectedFiles.size} use cases...\n\n${Array.from(selectedFiles).map(id => `✓ ${id.split('-').pop()}`).join('\n')}\n\nAll tests completed successfully!`)
    } else if (selectedFile) {
      // Single file testing
      setTestResult(`Testing "${selectedFile.name}"...\n\nBased on the input: "${testInput}"\n\nThe agent would respond with the solution defined in this use case.\n\n✅ Test passed - Ready for deployment!`)
    }
    
    setTimeout(() => {
      setTestsRunning(false)
      // Update test status to passed
      updateTestStatus(Array.from(selectedFiles), 'passed')
    }, 2000)
  }

  const updateTestStatus = (fileIds: string[], status: 'passed' | 'failed' | 'testing') => {
    const updateNodes = (nodes: FileNode[]): FileNode[] => {
      return nodes.map(node => {
        if (fileIds.includes(node.id)) {
          return { ...node, testStatus: status }
        }
        if (node.children) {
          return { ...node, children: updateNodes(node.children) }
        }
        return node
      })
    }
    setProjectStructure(updateNodes(projectStructure))
  }

  const handleDeploy = () => {
    setShowDeployModal(true)
  }

  const executeDeployment = () => {
    const selectedFilesList = Array.from(selectedFiles)
    console.log('Deploying files:', selectedFilesList)
    console.log('Strategy:', selectedStrategy || customStrategy)
    console.log('Percentage:', deploymentPercentage)
    
    // Update deployment status
    updateTestStatus(selectedFilesList, 'passed')
    setShowDeployModal(false)
    
    // Reset modal state
    setSelectedStrategy('')
    setCustomStrategy('')
    setDeploymentPercentage(100)
  }

  const getTestStatusIcon = (status?: string) => {
    switch (status) {
      case 'passed': return <div className="w-2 h-2 bg-positive-trend rounded-full" />
      case 'failed': return <div className="w-2 h-2 bg-negative-trend rounded-full" />
      case 'testing': return <div className="w-2 h-2 bg-opportunity rounded-full animate-pulse" />
      default: return <div className="w-2 h-2 bg-warm-whisper-grey/30 rounded-full" />
    }
  }

  const getChannelIcon = (channel?: string) => {
    switch (channel) {
      case 'voice': return '🎤'
      case 'chat': return '💬'
      case 'email': return '📧'
      case 'web': return '🌐'
      default: return ''
    }
  }

  const renderFileTree = (nodes: FileNode[], depth = 0) => {
    const filteredNodes = filterTree(nodes)
    
    return filteredNodes.map(node => {
      // Check if folder has any selected files
      const getFolderSelectionState = (folderNode: FileNode): 'none' | 'partial' | 'all' => {
        if (folderNode.type !== 'folder' || !folderNode.children) return 'none'
        
        const getAllFiles = (nodes: FileNode[]): FileNode[] => {
          let files: FileNode[] = []
          for (const n of nodes) {
            if (n.type === 'file' && shouldShowFile(n)) {
              files.push(n)
            } else if (n.type === 'folder' && n.children) {
              files = files.concat(getAllFiles(n.children))
            }
          }
          return files
        }
        
        const folderFiles = getAllFiles(folderNode.children)
        const selectedCount = folderFiles.filter(f => selectedFiles.has(f.id)).length
        
        if (selectedCount === 0) return 'none'
        if (selectedCount === folderFiles.length) return 'all'
        return 'partial'
      }
      
      const folderState = node.type === 'folder' ? getFolderSelectionState(node) : 'none'
      
      return (
        <div key={node.id} className="select-none">
          <div
            className={`flex items-center py-2 px-2 rounded-lg cursor-pointer transition-all duration-200 group relative
              ${selectedFiles.has(node.id) 
                ? 'bg-dt-magenta/20 text-dt-magenta border-l-4 border-l-dt-magenta shadow-sm' 
                : selectedFile?.id === node.id
                ? 'bg-soft-neutral-white/10 text-soft-neutral-white border-l-2 border-l-soft-neutral-white'
                : folderState === 'all'
                ? 'bg-dt-magenta/10 text-dt-magenta border-l-2 border-l-dt-magenta/50'
                : folderState === 'partial'
                ? 'bg-dt-magenta/5 text-soft-neutral-white border-l-2 border-l-dt-magenta/30'
                : 'hover:bg-soft-neutral-white/5 text-soft-neutral-white hover:border-l-2 hover:border-l-soft-neutral-white/20'
              }`}
            style={{ paddingLeft: `${depth * 16 + 8}px` }}
            onClick={(e) => {
              if (node.type === 'folder') {
                if (e.shiftKey) {
                  // Shift+click to select folder contents
                  selectFolder(node)
                } else {
                  // Regular click to toggle folder
                  toggleFolder(node.id)
                }
              } else {
                selectFile(node, e.ctrlKey || e.metaKey)
              }
            }}
          >
          {node.type === 'folder' ? (
            <>
              {node.isOpen ? (
                <ChevronDown className="w-4 h-4 mr-1 text-warm-whisper-grey" />
              ) : (
                <ChevronRight className="w-4 h-4 mr-1 text-warm-whisper-grey" />
              )}
              {node.isOpen ? (
                <FolderOpen className="w-4 h-4 mr-2 text-dt-magenta" />
              ) : (
                <Folder className="w-4 h-4 mr-2 text-warm-whisper-grey" />
              )}
            </>
          ) : (
            <>
              <File className={`w-4 h-4 mr-2 ml-5 ${
                node.name.endsWith('.latin') 
                  ? 'text-dt-magenta' 
                  : 'text-warm-whisper-grey'
              }`} />
            </>
          )}
          
          <span className="text-sm font-medium flex-1">{node.name}</span>
          
          {/* Selection indicator for folders */}
          {node.type === 'folder' && folderState !== 'none' && (
            <div className="flex items-center space-x-1 mr-2">
              {folderState === 'all' ? (
                <div className="w-3 h-3 bg-dt-magenta rounded border border-dt-magenta flex items-center justify-center">
                  <div className="w-1.5 h-1.5 bg-white rounded-sm" />
                </div>
              ) : (
                <div className="w-3 h-3 bg-dt-magenta/50 rounded border border-dt-magenta/50" />
              )}
            </div>
          )}
          
          {/* Channel and Status Indicators */}
          <div className="flex items-center space-x-1 ml-2">
            {node.channel && (
              <span className="text-xs opacity-60" title={node.channel}>
                {getChannelIcon(node.channel)}
              </span>
            )}
            {node.type === 'file' && getTestStatusIcon(node.testStatus)}
          </div>
        </div>
        {node.type === 'folder' && node.isOpen && node.children && (
          <div className="ml-2">
            {renderFileTree(node.children, depth + 1)}
          </div>
        )}
      </div>
      )
    })
  }

  return (
    <div className="min-h-screen bg-canvas-deep-dark flex flex-col">
      {/* Header */}
      <header className="px-6 py-4 border-b border-soft-neutral-white/10 bg-panel-dark/50 backdrop-blur-sm">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <button
              onClick={onBack}
              className="p-2 hover:bg-soft-neutral-white/10 rounded-lg transition-colors text-soft-neutral-white"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 bg-masaic-brand rounded-lg flex items-center justify-center shadow-masaic-float">
                <Code className="w-4 h-4 text-white" />
              </div>
              <div>
                <h1 className="text-lg font-semibold text-soft-neutral-white tracking-masaic">
                  {template || 'Customer Service Project'}
                </h1>
                <p className="text-sm text-warm-whisper-grey">
                  Business logic workspace
                </p>
              </div>
            </div>
          </div>
          
          <div className="flex items-center space-x-3">
            <button
              onClick={() => setIsTestMode(!isTestMode)}
              className={`px-4 py-2 rounded-lg font-medium transition-all ${
                isTestMode 
                  ? 'bg-dt-magenta text-white shadow-masaic-float' 
                  : 'bg-panel-dark text-soft-neutral-white hover:bg-soft-neutral-white/10 border border-soft-neutral-white/20'
              }`}
            >
              <TestTube className="w-4 h-4 mr-2 inline" />
              {isTestMode ? 'Code Mode' : 'Test Mode'}
            </button>
            <button className="px-4 py-2 bg-masaic-brand text-white rounded-lg font-medium shadow-masaic-float hover:shadow-masaic-depth transition-all">
              <Save className="w-4 h-4 mr-2 inline" />
              Save
            </button>
          </div>
        </div>
      </header>

      <div className="flex flex-1 overflow-hidden">
        {/* File Explorer */}
        <div className="w-80 border-r border-soft-neutral-white/10 bg-panel-dark/30 backdrop-blur-sm flex flex-col">
          <div className="p-4 border-b border-soft-neutral-white/10">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-sm font-semibold text-soft-neutral-white tracking-masaic">
                PROJECT EXPLORER
              </h3>
              <div className="flex items-center space-x-1">
                <button 
                  onClick={() => {
                    // Select all visible files
                    const getAllVisibleFiles = (nodes: FileNode[]): string[] => {
                      let fileIds: string[] = []
                      for (const node of nodes) {
                        if (node.type === 'file' && shouldShowFile(node)) {
                          fileIds.push(node.id)
                        } else if (node.type === 'folder' && node.children) {
                          fileIds = fileIds.concat(getAllVisibleFiles(node.children))
                        }
                      }
                      return fileIds
                    }
                    
                    const allVisibleFiles = getAllVisibleFiles(projectStructure)
                    setSelectedFiles(new Set(allVisibleFiles))
                  }}
                                     className="px-2 py-1 text-xs bg-dt-magenta/20 text-dt-magenta rounded hover:bg-dt-magenta/30 transition-colors"
                  title="Select all visible files"
                >
                  All
                </button>
                <button className="p-1 hover:bg-soft-neutral-white/10 rounded text-soft-neutral-white">
                  <Plus className="w-4 h-4" />
                </button>
              </div>
            </div>
            
            {/* Channel Filter */}
            <div className="mb-4">
              <label className="block text-xs font-medium text-warm-whisper-grey mb-2">
                Channel Filter
              </label>
              <select
                value={channelFilter}
                onChange={(e) => setChannelFilter(e.target.value as any)}
                className="w-full px-3 py-2 bg-canvas-deep-dark/50 border border-soft-neutral-white/20 rounded-lg text-soft-neutral-white text-sm focus:outline-none focus:ring-2 focus:ring-dt-magenta/50"
              >
                <option value="all">All Channels</option>
                <option value="voice">🎤 Voice Only</option>
                <option value="chat">💬 Chat Only</option>
                <option value="email">📧 Email Only</option>
                <option value="web">🌐 Web Only</option>
              </select>
            </div>

            {/* Multi-Selection Instructions */}
            {selectedFiles.size === 0 && (
              <div className="bg-panel-dark/40 rounded-lg p-3 mb-4 border border-soft-neutral-white/10">
                <div className="text-xs font-medium text-soft-neutral-white mb-2">
                  💡 Multi-Selection Tips:
                </div>
                <div className="text-xs text-warm-whisper-grey space-y-1">
                  <div>• <kbd className="px-1 py-0.5 bg-canvas-deep-dark rounded text-xs">Ctrl</kbd> + Click files for multi-select</div>
                  <div>• <kbd className="px-1 py-0.5 bg-canvas-deep-dark rounded text-xs">Shift</kbd> + Click folders to select all</div>
                  <div>• Test multiple use cases together</div>
                </div>
              </div>
            )}

            {/* Selection Info */}
            {selectedFiles.size > 0 && (
                              <div className="bg-dt-magenta/15 rounded-lg p-3 mb-4 border border-dt-magenta/30">
                 <div className="text-xs font-medium text-dt-magenta mb-2 flex items-center">
                   <div className="w-2 h-2 bg-dt-magenta rounded-full mr-2 animate-pulse" />
                  {selectedFiles.size} file{selectedFiles.size > 1 ? 's' : ''} selected
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={handleTest}
                    disabled={testsRunning}
                    className="flex-1 px-3 py-2 bg-masaic-brand text-white rounded-lg text-xs font-medium hover:bg-masaic-brand/90 transition-colors disabled:opacity-50 flex items-center justify-center shadow-sm"
                  >
                    <TestTube className="w-3 h-3 mr-1" />
                    {testsRunning ? 'Testing...' : 'Test'}
                  </button>
                  {/* Show deploy button only if all selected files are tested and passed */}
                  {Array.from(selectedFiles).every(id => {
                    const findNode = (nodes: FileNode[]): FileNode | null => {
                      for (const node of nodes) {
                        if (node.id === id) return node
                        if (node.children) {
                          const found = findNode(node.children)
                          if (found) return found
                        }
                      }
                      return null
                    }
                    const node = findNode(projectStructure)
                    return node?.testStatus === 'passed'
                  }) && (
                    <button
                      onClick={handleDeploy}
                      className="flex-1 px-3 py-2 bg-masaic-brand text-white rounded-lg text-xs font-medium hover:bg-masaic-brand/90 transition-colors flex items-center justify-center shadow-masaic-float"
                    >
                      <Zap className="w-3 h-3 mr-1" />
                      Deploy
                    </button>
                  )}
                </div>
              </div>
            )}
          </div>
          
          <div className="flex-1 p-4 overflow-y-auto">
            {renderFileTree(projectStructure)}
          </div>
        </div>

        {/* Main Content Area */}
        <div className={`flex-1 flex ${isTestMode ? 'divide-x divide-soft-neutral-white/10' : ''}`}>
          {/* Code Editor */}
          <div className={`${isTestMode ? 'w-1/2' : 'w-full'} flex flex-col`}>
            {selectedFile && (
              <>
                {/* File Tab */}
                <div className="px-6 py-3 border-b border-soft-neutral-white/10 bg-panel-dark/20">
                  <div className="flex items-center space-x-2">
                    <Hash className="w-4 h-4 text-dt-magenta" />
                    <span className="text-sm font-medium text-soft-neutral-white">
                      {selectedFile.name}
                    </span>
                    <div className="w-2 h-2 bg-dt-magenta rounded-full animate-pulse-faint" />
                  </div>
                </div>

                {/* Content Area */}
                <div className="flex-1 p-6 overflow-y-auto">
                  <div className="bg-canvas-deep-dark/50 rounded-xl border border-soft-neutral-white/10 p-6">
                    <pre className="text-sm text-soft-neutral-white whitespace-pre-wrap font-mono leading-relaxed">
                      {selectedFile.content}
                    </pre>
                  </div>
                </div>
              </>
            )}
          </div>

          {/* Test Panel */}
          {isTestMode && (
            <div className="w-1/2 bg-panel-dark/30 backdrop-blur-sm">
              <div className="p-6">
                <div className="mb-6">
                  <h3 className="text-lg font-semibold text-soft-neutral-white mb-2 flex items-center">
                    <Zap className="w-5 h-5 mr-2 text-dt-magenta" />
                    Test Your Logic
                  </h3>
                  <p className="text-sm text-warm-whisper-grey">
                    {selectedFiles.size > 1 
                      ? `Testing ${selectedFiles.size} use cases together`
                      : selectedFile 
                      ? `Testing: ${selectedFile.name.replace('.latin', '')}`
                      : 'Select a use case to test'
                    }
                  </p>
                  
                  {/* Currently Testing */}
                  {selectedFiles.size > 0 && (
                    <div className="mt-3 p-3 bg-canvas-deep-dark/50 rounded-lg border border-soft-neutral-white/10">
                      <div className="text-xs font-medium text-warm-whisper-grey mb-2">Use Cases Under Test:</div>
                      <div className="space-y-1">
                        {Array.from(selectedFiles).map(fileId => {
                          const findNode = (nodes: FileNode[]): FileNode | null => {
                            for (const node of nodes) {
                              if (node.id === fileId) return node
                              if (node.children) {
                                const found = findNode(node.children)
                                if (found) return found
                              }
                            }
                            return null
                          }
                          const node = findNode(projectStructure)
                          return (
                            <div key={fileId} className="flex items-center justify-between text-xs">
                              <span className="text-soft-neutral-white">
                                {node?.name.replace('.latin', '')} 
                                {node?.channel && ` (${getChannelIcon(node.channel)} ${node.channel})`}
                              </span>
                              {getTestStatusIcon(node?.testStatus)}
                            </div>
                          )
                        })}
                      </div>
                    </div>
                  )}
                </div>

                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-soft-neutral-white mb-2">
                      Customer Input
                    </label>
                    <textarea
                      value={testInput}
                      onChange={(e) => setTestInput(e.target.value)}
                      placeholder="Type what a customer might say..."
                      rows={3}
                      className="w-full px-4 py-3 bg-canvas-deep-dark/50 border border-soft-neutral-white/20 rounded-xl text-soft-neutral-white placeholder-warm-whisper-grey focus:outline-none focus:ring-2 focus:ring-dt-magenta/50 focus:border-dt-magenta resize-none"
                    />
                  </div>

                  <button
                    onClick={handleTest}
                    disabled={testsRunning || selectedFiles.size === 0}
                    className="w-full px-4 py-3 bg-masaic-brand text-white rounded-xl font-medium hover:bg-masaic-brand/90 transition-colors flex items-center justify-center shadow-masaic-float hover:shadow-masaic-depth disabled:opacity-50"
                  >
                    <Play className="w-4 h-4 mr-2" />
                    {testsRunning ? 'Testing...' : 'Test Response'}
                  </button>

                  {testResult && (
                    <div className="bg-panel-dark/60 rounded-xl p-4 border border-soft-neutral-white/10">
                      <h4 className="font-medium text-soft-neutral-white mb-2 flex items-center">
                        <MessageSquare className="w-4 h-4 mr-2 text-dt-magenta" />
                        Test Results:
                      </h4>
                      <div className="text-sm text-warm-whisper-grey whitespace-pre-wrap">
                        {testResult}
                      </div>
                      
                      {/* Deploy Button for Passed Tests */}
                      {testResult.includes('✅') && !testsRunning && (
                        <button
                          onClick={handleDeploy}
                          className="mt-4 w-full px-4 py-3 bg-positive-trend text-white rounded-xl font-medium hover:bg-positive-trend/90 transition-colors flex items-center justify-center shadow-masaic-float"
                        >
                          <Zap className="w-4 h-4 mr-2" />
                          Deploy
                        </button>
                      )}
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Deployment Strategy Modal */}
      {showDeployModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-panel-dark border border-soft-neutral-white/20 rounded-xl shadow-masaic-depth max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
            {/* Modal Header */}
            <div className="flex items-center justify-between p-6 border-b border-soft-neutral-white/10">
              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-masaic-brand rounded-lg flex items-center justify-center shadow-masaic-float">
                  <Zap className="w-4 h-4 text-white" />
                </div>
                <div>
                  <h2 className="text-lg font-semibold text-soft-neutral-white tracking-masaic">
                    Deploy Strategy
                  </h2>
                  <p className="text-sm text-warm-whisper-grey">
                    Choose how to deploy your {selectedFiles.size} use case{selectedFiles.size > 1 ? 's' : ''}
                  </p>
                </div>
              </div>
              <button
                onClick={() => setShowDeployModal(false)}
                className="p-2 hover:bg-soft-neutral-white/10 rounded-lg transition-colors text-warm-whisper-grey hover:text-soft-neutral-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Modal Content */}
            <div className="p-6 space-y-6">
              {/* Deployment Strategies */}
              <div className="space-y-4">
                <h3 className="text-sm font-semibold text-soft-neutral-white mb-3">
                  Select Deployment Strategy:
                </h3>
                
                {/* Strategy Options */}
                <div className="space-y-3">
                  {/* Full Deployment */}
                  <div 
                    className={`p-4 rounded-lg border cursor-pointer transition-all ${
                      selectedStrategy === 'full' 
                        ? 'border-dt-magenta bg-dt-magenta/10' 
                        : 'border-soft-neutral-white/20 hover:border-soft-neutral-white/40 hover:bg-soft-neutral-white/5'
                    }`}
                    onClick={() => {
                      setSelectedStrategy('full')
                      setDeploymentPercentage(100)
                      setCustomStrategy('')
                    }}
                  >
                    <div className="flex items-center space-x-3">
                      <Users className="w-5 h-5 text-dt-magenta" />
                      <div className="flex-1">
                        <div className="font-medium text-soft-neutral-white">Route All Users</div>
                        <div className="text-sm text-warm-whisper-grey">Deploy to 100% of users immediately</div>
                      </div>
                      <div className="w-4 h-4 rounded-full border-2 border-dt-magenta flex items-center justify-center">
                        {selectedStrategy === 'full' && <div className="w-2 h-2 bg-dt-magenta rounded-full" />}
                      </div>
                    </div>
                  </div>

                  {/* Percentage Deployment */}
                  <div 
                    className={`p-4 rounded-lg border cursor-pointer transition-all ${
                      selectedStrategy === 'percentage' 
                        ? 'border-dt-magenta bg-dt-magenta/10' 
                        : 'border-soft-neutral-white/20 hover:border-soft-neutral-white/40 hover:bg-soft-neutral-white/5'
                    }`}
                    onClick={() => {
                      setSelectedStrategy('percentage')
                      setCustomStrategy('')
                    }}
                  >
                    <div className="flex items-center space-x-3">
                      <Target className="w-5 h-5 text-dt-magenta" />
                      <div className="flex-1">
                        <div className="font-medium text-soft-neutral-white">Gradual Rollout</div>
                        <div className="text-sm text-warm-whisper-grey">Deploy to a percentage of users, keep previous version for the rest</div>
                      </div>
                      <div className="w-4 h-4 rounded-full border-2 border-dt-magenta flex items-center justify-center">
                        {selectedStrategy === 'percentage' && <div className="w-2 h-2 bg-dt-magenta rounded-full" />}
                      </div>
                    </div>
                    {selectedStrategy === 'percentage' && (
                      <div className="mt-4 pt-4 border-t border-soft-neutral-white/10">
                        <label className="block text-sm font-medium text-soft-neutral-white mb-2">
                          Deployment Percentage: {deploymentPercentage}%
                        </label>
                        <input
                          type="range"
                          min="1"
                          max="100"
                          value={deploymentPercentage}
                          onChange={(e) => setDeploymentPercentage(Number(e.target.value))}
                          className="w-full h-2 bg-canvas-deep-dark rounded-lg appearance-none cursor-pointer slider"
                          style={{
                            background: `linear-gradient(to right, #8B5CF6 0%, #8B5CF6 ${deploymentPercentage}%, #2A2A2A ${deploymentPercentage}%, #2A2A2A 100%)`
                          }}
                        />
                        <div className="flex justify-between text-xs text-warm-whisper-grey mt-1">
                          <span>1%</span>
                          <span>50%</span>
                          <span>100%</span>
                        </div>
                      </div>
                    )}
                  </div>

                  {/* A/B Testing */}
                  <div 
                    className={`p-4 rounded-lg border cursor-pointer transition-all ${
                      selectedStrategy === 'ab-test' 
                        ? 'border-dt-magenta bg-dt-magenta/10' 
                        : 'border-soft-neutral-white/20 hover:border-soft-neutral-white/40 hover:bg-soft-neutral-white/5'
                    }`}
                    onClick={() => {
                      setSelectedStrategy('ab-test')
                      setDeploymentPercentage(50)
                      setCustomStrategy('')
                    }}
                  >
                    <div className="flex items-center space-x-3">
                      <Shuffle className="w-5 h-5 text-dt-magenta" />
                      <div className="flex-1">
                        <div className="font-medium text-soft-neutral-white">A/B Test</div>
                        <div className="text-sm text-warm-whisper-grey">Split traffic 50/50 between new and previous version</div>
                      </div>
                      <div className="w-4 h-4 rounded-full border-2 border-dt-magenta flex items-center justify-center">
                        {selectedStrategy === 'ab-test' && <div className="w-2 h-2 bg-dt-magenta rounded-full" />}
                      </div>
                    </div>
                  </div>

                  {/* Custom Strategy */}
                  <div 
                    className={`p-4 rounded-lg border cursor-pointer transition-all ${
                      selectedStrategy === 'custom' 
                        ? 'border-dt-magenta bg-dt-magenta/10' 
                        : 'border-soft-neutral-white/20 hover:border-soft-neutral-white/40 hover:bg-soft-neutral-white/5'
                    }`}
                    onClick={() => {
                      setSelectedStrategy('custom')
                      setDeploymentPercentage(100)
                    }}
                  >
                    <div className="flex items-center space-x-3">
                      <Code className="w-5 h-5 text-dt-magenta" />
                      <div className="flex-1">
                        <div className="font-medium text-soft-neutral-white">Custom Logic</div>
                        <div className="text-sm text-warm-whisper-grey">Write your own deployment strategy</div>
                      </div>
                      <div className="w-4 h-4 rounded-full border-2 border-dt-magenta flex items-center justify-center">
                        {selectedStrategy === 'custom' && <div className="w-2 h-2 bg-dt-magenta rounded-full" />}
                      </div>
                    </div>
                    {selectedStrategy === 'custom' && (
                      <div className="mt-4 pt-4 border-t border-soft-neutral-white/10">
                        <label className="block text-sm font-medium text-soft-neutral-white mb-2">
                          Custom Deployment Logic:
                        </label>
                        <textarea
                          value={customStrategy}
                          onChange={(e) => setCustomStrategy(e.target.value)}
                          placeholder="Example: Route users from premium tier to new version, route users with support tickets to previous version..."
                          rows={4}
                          className="w-full px-4 py-3 bg-canvas-deep-dark/50 border border-soft-neutral-white/20 rounded-lg text-soft-neutral-white text-sm placeholder-warm-whisper-grey focus:outline-none focus:ring-2 focus:ring-dt-magenta/50 focus:border-dt-magenta resize-none"
                        />
                      </div>
                    )}
                  </div>
                </div>
              </div>

              {/* Selected Files Preview */}
              <div className="bg-canvas-deep-dark/30 rounded-lg p-4 border border-soft-neutral-white/10">
                <h4 className="text-sm font-medium text-soft-neutral-white mb-3">Files to Deploy:</h4>
                <div className="space-y-2">
                  {Array.from(selectedFiles).map(fileId => {
                    const findNode = (nodes: FileNode[]): FileNode | null => {
                      for (const node of nodes) {
                        if (node.id === fileId) return node
                        if (node.children) {
                          const found = findNode(node.children)
                          if (found) return found
                        }
                      }
                      return null
                    }
                    const node = findNode(projectStructure)
                    return (
                      <div key={fileId} className="flex items-center justify-between text-sm">
                        <span className="text-soft-neutral-white">
                          {node?.name.replace('.latin', '')} 
                          {node?.channel && ` (${getChannelIcon(node.channel)} ${node.channel})`}
                        </span>
                        <div className="w-2 h-2 bg-positive-trend rounded-full" />
                      </div>
                    )
                  })}
                </div>
              </div>
            </div>

            {/* Modal Footer */}
            <div className="flex items-center justify-between p-6 border-t border-soft-neutral-white/10">
              <button
                onClick={() => setShowDeployModal(false)}
                className="px-4 py-2 text-warm-whisper-grey hover:text-soft-neutral-white transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={executeDeployment}
                disabled={!selectedStrategy && !customStrategy.trim()}
                className="px-6 py-2 bg-masaic-brand text-white rounded-lg font-medium hover:bg-masaic-brand/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-2 shadow-masaic-float"
              >
                <Zap className="w-4 h-4" />
                <span>Deploy Now</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
} 