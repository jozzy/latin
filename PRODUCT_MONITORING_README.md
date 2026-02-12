# Product Monitoring Workflow

This document describes the product monitoring workflow implemented using Latin modules. The workflow tracks product mentions, analyzes insights, generates reports, and sends alerts.

## 📋 Workflow Overview

The product monitoring system consists of 7 modular Latin scripts that work together to:

1. **Search for mentions** across web, social media, and news
2. **Analyze and summarize** the findings
3. **Generate reports** with insights and recommendations
4. **Send weekly reports** to stakeholders
5. **Handle alerts** for critical situations

## 🏗️ Module Architecture

### Core Modules

| Module | Purpose | Trigger | Handover |
|--------|---------|---------|----------|
| `product_monitoring.latin` | Main orchestrator | Timer (7 days) | `#mention_alert` |
| `search_mentions.latin` | Find product mentions | `search_mentions` | `#high_volume_alert` |
| `summarize_insights.latin` | Analyze findings | `summarize_insights` | None |
| `create_report.latin` | Generate reports | `create_report` | None |
| `send_email.latin` | Send reports | `send_email` | None |
| `mention_alert.latin` | Handle critical alerts | `mention_alert` | None |
| `high_volume_alert.latin` | Handle high volume | `high_volume_alert` | `#mention_alert` |

### Workflow Flow

```
product_monitoring (Timer)
    ↓
search_mentions (Event)
    ↓
summarize_insights (Event)
    ↓
create_report (Event)
    ↓
send_email (Event)
    ↓
mention_alert (if critical)
```

## 🧪 Testing the Workflow

### 1. Using the Test Script

```bash
# Make the script executable
chmod +x test_product_monitoring.sh

# Run all tests
./test_product_monitoring.sh
```

### 2. Manual Testing with curl

#### Test Individual Events

```bash
# Test search mentions
curl -X POST http://localhost:8080/events/search_mentions \
  -H "Content-Type: text/plain" \
  -d "search for OurProduct mentions"

# Test summarize insights
curl -X POST http://localhost:8080/events/summarize_insights \
  -H "Content-Type: text/plain" \
  -d "summarize findings from search"

# Test create report
curl -X POST http://localhost:8080/events/create_report \
  -H "Content-Type: text/plain" \
  -d "create weekly report from insights"

# Test send email
curl -X POST http://localhost:8080/events/send_email \
  -H "Content-Type: text/plain" \
  -d "send weekly report to stakeholders"
```

#### Test Individual Modules

```bash
# Test main workflow module
curl -X POST http://localhost:8080/modules/product_monitoring \
  -H "Content-Type: text/plain" \
  -d "execute weekly monitoring"

# Test search module directly
curl -X POST http://localhost:8080/modules/search_mentions \
  -H "Content-Type: text/plain" \
  -d "search for OurProduct"

# Test alert modules
curl -X POST http://localhost:8080/modules/mention_alert \
  -H "Content-Type: text/plain" \
  -d "critical mention alert"

curl -X POST http://localhost:8080/modules/high_volume_alert \
  -H "Content-Type: text/plain" \
  -d "high volume alert"
```

### 3. Using JUnit Tests

```bash
# Run the product monitoring tests
./gradlew :server:test --tests ProductMonitoringTest
```

## 🔧 Mock Functions

The workflow uses mock functions that simulate real API calls:

### Search Functions
- `web_search(query, timeframe)` - Searches web for mentions
- `social_media_search(platform, query)` - Searches social media
- `news_search(query)` - Searches news articles

### Analysis Functions
- `analyze_sentiment(text)` - Analyzes sentiment
- `categorize_mentions(mentions)` - Categorizes by topic
- `extract_key_insights(data)` - Extracts insights

### Report Functions
- `format_report(data, format)` - Creates reports
- `create_charts(data, chart_type)` - Creates visualizations

### Communication Functions
- `send_email(to, subject, body, attachment)` - Sends emails
- `send_urgent_alert(message, priority)` - Sends alerts
- `notify_team(team, message)` - Notifies teams

### Analysis Functions
- `analyze_trend(data)` - Analyzes trends

## 📊 Expected Responses

### Search Module
```
Found 15 mentions of OurProduct in the last 7 days across 8 websites
Found 8 social media mentions on Twitter for 'OurProduct'
Found 3 news articles mentioning 'OurProduct' in the last week
```

### Analysis Module
```
Positive sentiment detected in mention (confidence: 85%)
Categorized 26 mentions: 12 reviews, 8 complaints, 4 news articles, 2 general discussions
Key insights: 1) Positive trend in customer satisfaction, 2) Feature X mentioned frequently, 3) Pricing concerns in 15% of mentions
```

### Report Module
```
Report generated successfully in PDF format with executive summary and detailed analysis
Generated bar chart showing trends and patterns
```

### Email Module
```
Email sent successfully to ceo@company.com, marketing@company.com, product@company.com with attachment: weekly_report.pdf
```

### Alert Modules
```
Urgent alert sent to crisis management team: Critical negative mentions detected
Notification sent to social media team: High volume of mentions detected
```

## 🔄 Handover System

The workflow uses the handover system for complex routing:

1. **High Volume Detection**: If >10 mentions found, handover to `#high_volume_alert`
2. **Critical Mentions**: If negative sentiment detected, handover to `#mention_alert`
3. **Main Workflow**: If critical mentions found, handover to `#mention_alert`

## 🚀 Production Deployment

To deploy this workflow in production:

1. **Replace mock functions** with real API implementations
2. **Configure email settings** for your organization
3. **Set up monitoring** for the timer and events
4. **Add error handling** and retry logic
5. **Configure alerts** for system health

## 📈 Monitoring and Observability

The system provides:
- **Health checks** at `/health`
- **Status endpoint** at `/status` showing loaded modules
- **Logging** for all function calls and handovers
- **Event tracking** for all workflow steps

## 🔍 Troubleshooting

### Common Issues

1. **Module not found**: Check if module is loaded in `/status`
2. **Function not available**: Verify function is registered in `BasicFunctions.kt`
3. **Handover not working**: Check module name matches exactly
4. **Timer not firing**: Verify timer duration and server time

### Debug Commands

```bash
# Check system status
curl http://localhost:8080/status

# Check health
curl http://localhost:8080/health

# Check loaded modules
curl http://localhost:8080/status | jq '.modules[].name'
```

## 📝 Extending the Workflow

To add new functionality:

1. **Create new `.latin` module** in `modules/` directory
2. **Add mock functions** to `BasicFunctions.kt`
3. **Update tests** in `ProductMonitoringTest.kt`
4. **Test with curl** commands
5. **Deploy and monitor**

This modular approach follows SOLID principles and allows easy extension without modifying existing modules. 