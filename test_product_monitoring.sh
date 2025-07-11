#!/bin/bash

echo "🧪 Testing Product Monitoring Workflow"
echo "======================================"

# Wait for server to be ready
echo "Waiting for server to be ready..."
sleep 5

# Test 1: Main product monitoring module
echo ""
echo "📊 Test 1: Main Product Monitoring Module"
echo "----------------------------------------"
curl -X POST http://localhost:8080/modules/product_monitoring \
  -H "Content-Type: text/plain" \
  -d "execute weekly monitoring"

# Test 2: Search mentions event
echo ""
echo "🔍 Test 2: Search Mentions Event"
echo "--------------------------------"
curl -X POST http://localhost:8080/events/search_mentions \
  -H "Content-Type: text/plain" \
  -d "search for OurProduct mentions across all platforms"

# Test 3: Summarize insights event
echo ""
echo "📈 Test 3: Summarize Insights Event"
echo "-----------------------------------"
curl -X POST http://localhost:8080/events/summarize_insights \
  -H "Content-Type: text/plain" \
  -d "summarize findings from search results"

# Test 4: Create report event
echo ""
echo "📋 Test 4: Create Report Event"
echo "------------------------------"
curl -X POST http://localhost:8080/events/create_report \
  -H "Content-Type: text/plain" \
  -d "create weekly report from insights"

# Test 5: Send email event
echo ""
echo "📧 Test 5: Send Email Event"
echo "---------------------------"
curl -X POST http://localhost:8080/events/send_email \
  -H "Content-Type: text/plain" \
  -d "send weekly report to stakeholders"

# Test 6: Mention alert event
echo ""
echo "🚨 Test 6: Mention Alert Event"
echo "------------------------------"
curl -X POST http://localhost:8080/events/mention_alert \
  -H "Content-Type: text/plain" \
  -d "critical mention alert - negative sentiment detected"

# Test 7: High volume alert event
echo ""
echo "📈 Test 7: High Volume Alert Event"
echo "----------------------------------"
curl -X POST http://localhost:8080/events/high_volume_alert \
  -H "Content-Type: text/plain" \
  -d "high volume mention alert - viral moment detected"

# Test 8: Direct module testing
echo ""
echo "🔧 Test 8: Direct Module Testing"
echo "--------------------------------"
echo "Testing search_mentions module directly:"
curl -X POST http://localhost:8080/modules/search_mentions \
  -H "Content-Type: text/plain" \
  -d "search for OurProduct"

echo ""
echo "Testing summarize_insights module directly:"
curl -X POST http://localhost:8080/modules/summarize_insights \
  -H "Content-Type: text/plain" \
  -d "summarize data"

echo ""
echo "Testing create_report module directly:"
curl -X POST http://localhost:8080/modules/create_report \
  -H "Content-Type: text/plain" \
  -d "create report"

echo ""
echo "Testing send_email module directly:"
curl -X POST http://localhost:8080/modules/send_email \
  -H "Content-Type: text/plain" \
  -d "send email"

echo ""
echo "Testing mention_alert module directly:"
curl -X POST http://localhost:8080/modules/mention_alert \
  -H "Content-Type: text/plain" \
  -d "send alert"

echo ""
echo "Testing high_volume_alert module directly:"
curl -X POST http://localhost:8080/modules/high_volume_alert \
  -H "Content-Type: text/plain" \
  -d "analyze high volume"

# Test 9: System status
echo ""
echo "📊 Test 9: System Status"
echo "------------------------"
curl http://localhost:8080/status

echo ""
echo "🏥 Health Check:"
curl http://localhost:8080/health

echo ""
echo "✅ Product Monitoring Workflow Testing Complete!"
echo "===============================================" 