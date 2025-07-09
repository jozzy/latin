Every `.latin` script you write becomes a **self-contained, universally invokable program**:

1. **HTTP/API Trigger**
    
    ```
    plaintext
    CopyEdit
    POST /latin/troubleshoot_wifi
    { “message”: “Wi-Fi is broken” }
    
    ```
    
    ARC routes that JSON payload into your script’s “Start when…” block.
    
2. **Kafka-Style Topic**
    - You configure ARC to subscribe to a topic, say `machine-errors`.
    - Each incoming event on `machine-errors` is fed into `Start when machine error arrives:` scripts.
3. **Email-Inbound Trigger**
    - ARC can poll an inbox or hook your mail server.
    - Incoming emails matching a pattern (“Subject: Support request”) become events for your “Start when an email arrives:” scripts.
4. **Webhooks & Other Connectors**
    - Any system that can POST JSON—CRMs, IoT platforms, monitoring tools—can invoke your scripts.

---

## 🏗️ How It’s Decoupled

- **Script = Logic Only.** Your `.latin` file doesn’t care about HTTP, Kafka, or email—that’s configured at deployment time in ARC.
- **Connector Layer.** ARC’s adapters translate channel-specific incoming messages into the same event object your script expects.
- **Flexible Binding.** You can bind one script to multiple channels:
    - Email + HTTP
    - Kafka + Webhook

---

## 🔄 Example Mapping

```
plaintext
CopyEdit
Script:   order_reminder.latin
Channels:
  • HTTP at /latin/order_reminder
  • Kafka topic “order-events”
  • Email with subject “Order Reminder”

```

- **HTTP** → Client does `POST /latin/order_reminder { order_id: 123 }`
- **Kafka** → ARC consumes `{"order_id":123}` from topic and runs the same logic
- **Email** → ARC watches a mailbox; email body `order_id: 123` gets parsed and passed in

All three invoke **the identical code path** in your `order_reminder.latin`.

---

### ✅ Benefits

- **Decoupling**: Channel logic lives in ARC config, not in your script.
- **Reusability**: One script, many triggers.
- **Consistency**: Every invocation feeds the same input shape.
- **Scalability**: Add new channels without touching the `.latin` file.

## 📁 Folder Layout

```
arduino
CopyEdit
/troubleshoot_wifi/
├── troubleshoot_wifi.latin
└── .channel_map.json    ← hidden developer config

```

---

## 🔧 Example `.channel_map.json`

```json
json
CopyEdit
{
  "script": "troubleshoot_wifi.latin",
  "bindings": [
    {
      "type": "http",
      "endpoint": "/latin/troubleshoot_wifi",
      "method": "POST"
    },
    {
      "type": "kafka",
      "topic": "machine-errors"
    },
    {
      "type": "email",
      "inbox": "support@company.com",
      "subjectFilter": "Wi-Fi help"
    }
  ]
}

```

- **Type**: channel adapter (HTTP, Kafka, Email, Webhook, etc.)
- **Endpoint/Topic/Inbox**: where ARC listens
- **Filters**: optional constraints (e.g. subject keywords)

---

## ✅ Why This Works

1. **Developer-Friendly**
    - All channel mappings live next to the `.latin` file.
    - Hidden by dot-prefix so business users only see the script.
2. **Decoupled Logic**
    - Your `.latin` remains pure intent—no channel code inside.
    - ARC loads both files at runtime and wires them up.
3. **Version-Controlled**
    - Both script and mapping live in Git, so you can branch, review, and roll back.
4. **Extensible**
    - Need a new channel? Add a new entry to `.channel_map.json`, no script changes.