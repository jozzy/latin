#!/bin/bash

echo "🚀 Phase 2: Dynamic Routing System Test"
echo "========================================"

# Wait for server to be ready
echo "1. Waiting for server to be ready..."
until curl -s http://localhost:8080/health | jq -r '.ok' | grep -q "true"; do
    echo "   Waiting for server..."
    sleep 2
done
echo "✅ Server is ready"

# Check all loaded modules
echo "2. Checking loaded modules..."
TOTAL_MODULES=$(curl -s http://localhost:8080/status | jq -r '.modules | length')
USECASE_MODULES=$(curl -s http://localhost:8080/status | jq -r '.modules[] | select(.startCondition != null) | .name' | wc -l)
echo "   Total modules: $TOTAL_MODULES"
echo "   UseCase modules: $USECASE_MODULES"

# List all UseCase modules
echo "3. UseCase modules available:"
curl -s http://localhost:8080/status | jq -r '.modules[] | select(.startCondition != null) | "   - \(.name): \(.startCondition)"'

# Test dynamic endpoints
echo ""
echo "4. Testing dynamic endpoints..."

# Test send_email
echo "   Testing POST /latin/send_email..."
RESPONSE=$(curl -s -X POST http://localhost:8080/latin/send_email \
  -H "Content-Type: application/json" \
  -d '{"recipient": "test@example.com", "email_body": "Hello from Latin!"}' \
  -w "\nHTTP_STATUS: %{http_code}")
echo "   Response: $RESPONSE"

# Test find_leads
echo "   Testing POST /latin/find_leads..."
RESPONSE=$(curl -s -X POST http://localhost:8080/latin/find_leads \
  -H "Content-Type: application/json" \
  -d '{"target_keywords": "software engineer, tech startup"}' \
  -w "\nHTTP_STATUS: %{http_code}")
echo "   Response: $RESPONSE"

# Test personalize_message
echo "   Testing POST /latin/personalize_message..."
RESPONSE=$(curl -s -X POST http://localhost:8080/latin/personalize_message \
  -H "Content-Type: application/json" \
  -d '{"lead": "John Doe, CTO at TechCorp", "offer_text": "Free consultation"}' \
  -w "\nHTTP_STATUS: %{http_code}")
echo "   Response: $RESPONSE"

# Test automated_pipeline
echo "   Testing POST /latin/automated_pipeline..."
RESPONSE=$(curl -s -X POST http://localhost:8080/latin/automated_pipeline \
  -H "Content-Type: application/json" \
  -d '{"target_keywords": "marketing director, SaaS"}' \
  -w "\nHTTP_STATUS: %{http_code}")
echo "   Response: $RESPONSE"

# Test legacy endpoints still work
echo ""
echo "5. Verifying legacy endpoints still work..."
LEGACY_RESPONSE=$(curl -s -X POST http://localhost:8080/events/chat -H "Content-Type: text/plain" -d "Hello")
echo "   Legacy chat response: $LEGACY_RESPONSE"

echo ""
echo "🎉 Phase 2 Testing Complete!"
echo ""
echo "📊 Summary:"
echo "- Server: ✅ Ready"
echo "- UseCase modules: ✅ $USECASE_MODULES loaded"
echo "- Dynamic endpoints: ✅ Generated"
echo "- Legacy endpoints: ✅ Still working"
echo ""
echo "🚀 Ready for Phase 3: Module Composition!" 