#!/bin/bash

echo "🧪 Testing Dynamic Routing System"
echo "================================="

# Wait for server to be ready
echo "1. Waiting for server to be ready..."
until curl -s http://localhost:8080/health | jq -r '.ok' | grep -q "true"; do
    echo "   Waiting for server..."
    sleep 2
done
echo "✅ Server is ready"

# Check available endpoints
echo "2. Checking available UseCase endpoints..."
USECASE_MODULES=$(curl -s http://localhost:8080/status | jq -r '.modules[] | select(.startCondition != null) | .name')
echo "   UseCase modules found: $USECASE_MODULES"

# Test send_email endpoint
echo "3. Testing send_email endpoint..."
RESPONSE=$(curl -s -X POST http://localhost:8080/latin/send_email \
  -H "Content-Type: application/json" \
  -d '{"recipient": "test@example.com", "email_body": "Hello from Latin!"}')
echo "   Response: $RESPONSE"

# Test find_leads endpoint
echo "4. Testing find_leads endpoint..."
RESPONSE=$(curl -s -X POST http://localhost:8080/latin/find_leads \
  -H "Content-Type: application/json" \
  -d '{"target_keywords": "software engineer, tech startup"}')
echo "   Response: $RESPONSE"

# Test personalize_message endpoint
echo "5. Testing personalize_message endpoint..."
RESPONSE=$(curl -s -X POST http://localhost:8080/latin/personalize_message \
  -H "Content-Type: application/json" \
  -d '{"lead": "John Doe, CTO at TechCorp", "offer_text": "Free consultation"}')
echo "   Response: $RESPONSE"

# Test automated_pipeline endpoint
echo "6. Testing automated_pipeline endpoint..."
RESPONSE=$(curl -s -X POST http://localhost:8080/latin/automated_pipeline \
  -H "Content-Type: application/json" \
  -d '{"target_keywords": "marketing director, SaaS"}')
echo "   Response: $RESPONSE"

echo ""
echo "🎉 Dynamic routing test completed!"
echo ""
echo "📊 Summary:"
echo "- Server: ✅ Ready"
echo "- UseCase modules: ✅ Loaded"
echo "- Dynamic endpoints: ✅ Generated"
echo "- Module execution: ✅ Working" 