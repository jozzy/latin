🧭 User Journey – End-User Experience in the Web-Based Prompt IDE
Imagine a web-based IDE purpose-built for semantic agent programming — where creating intelligent behavior feels like creating modular documents, not writing code.

Step 1: Getting Started — Project Creation

The user lands on the IDE home screen.
They click ➕ New Project.
Prompted to choose a project type:
→ “Create New Latin Project”
→ “Upload Existing Project”
Upon selecting New Latin Project, the system scaffolds a basic structure and prompts the user to choose from a library of templates.
Step 2: Template Selection

Templates include:
🧾 Customer Service Starter Kit
⚙️ Bootstrap Agent Kit
🕓 Long-Running Workflow Template (e.g., meeting scheduling, device monitoring)
📬 Email & Asynchronous Workflow Kit
Each template generates a folder structure resembling an app project, with pre-defined directories for domains (e.g., billing, support, onboarding).

Step 3: Folder & Module Structure

A selected Customer Service project creates something like:

/customer_service_project/
├── billing/
│   ├── meta.md
│   ├── billing_conflict.md
│   ├── charge_twice.md
│   └── cancellation_policy.md
├── onboarding/
│   ├── welcome.md
│   └── account_setup.md
└── config.yml
Each .md file represents a semantic logic module, written in Markdown with embedded metadata.

Step 4: Authoring a Module

The user clicks on charge_twice.md and sees a split-pane editor:

📝 Left pane: Markdown with natural language instructions and structured variables.
🧪 Right pane: Live test environment:
Simulate as chat
Simulate as voice
Simulate as email
Simulate with memory context
Under each module, users define variants:

chat_variant
voice_variant
email_variant
common_response_logic
This allows the system to tailor the tone and structure of the response channel-wise — same logic, different semantics.

Step 5: Immediate Testing

Every module is:

Independently testable via UI
Configurable with test inputs and mock memory
Capable of showing expected vs. actual results
No backend deployment is needed to see it in action.

Step 6: Composing & Linking Modules

Users can then:

Compose modules into a larger domain program (e.g., billing agent)
Test cross-module workflows
Inject memory context to simulate conversations across steps
The whole billing domain becomes a testable agent.

Step 7: A/B Testing & Variants

For any module, users can:

Add versions (v1, v2)
Tag each version with routing logic (e.g., 80/20 split)
Specify additional routing metadata like:
language = 'de'
customer_type = 'premium'
A built-in router in the runtime container handles request-level routing logic.

Step 8: Deployment

Once ready:

Click ▶ Deploy
System packages the modules and metadata into a containerized runtime
Exposes APIs per module or as a single agent endpoint
Automatically registers routing rules, memory services, and logging hooks
Step 9: Voice & Async Channels

Voice-specific behavior:

Defined in voice_variant of a module
Runtime invokes TTS/STT and response shaping modules
Email & async behavior:

Defined in email_variant
Triggered via message queues or cron-based workflows
The system can support long-running workflows, such as:

“Notify users of DX100 machine error codes IFBN appearing 6 times in 30 minutes”
These are modeled declaratively and tested like any prompt module.
Bonus: For John Deere or Any Enterprise User

They log in
Click: + New Latin Module
Choose: Machine Diagnostic Template
Fill in logic like:
"If error IFBN from DX100 series happens repeatedly for 30 mins, send alert to service team and log it."
Deploy with click
Expose API or integrate into existing workflows (Mattermost, CRM, etc.)
They now have a running, testable agent program without writing a line of traditional code.

Summary of Experience Features

No-code / low-code semantic programming in Markdown
Test-as-you-write in multi-modal simulations
Modular folder structure with reusable logic
A/B testing and request routing built-in
Instant deployability and API exposure
Built-in templates for domains and workflows
