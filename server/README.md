


Set environment variable `OPENAI_API_KEY` to your OpenAI API key.

Start server with:
```bash
./gradlew run
```

SSE Event endpoint: http://0.0.0.0:8080/runs

Test validate events:
 ```bash
curl -X POST http://0.0.0.0:8080/trigger/ping -d '{"event": "value", "status": up}' 
```

Test chat:
 ```bash
curl -X POST http://0.0.0.0:8080/trigger/chat -d 'Hi, how are you?'
```

Test complaint:
 ```bash
curl -X POST http://0.0.0.0:8080/trigger/customer -d 'I am not happy with the service. I am being charged double.'
```

Test Search Mentions:
 ```bash
curl -X POST http://0.0.0.0:8080/trigger/search_mentions -d  'search for OurProduct mentions across all platforms'
```

Test product monitoring:
 ```bash
curl -X POST http://localhost:8080/trigger/product_monitoring 
```

Test Email:
 ```bash
curl -X POST http://0.0.0.0:8080/trigger/send_email -d '{report: "Stocks are going up!"}'
```

Test Greeting:
 ```bash
curl -X POST http://0.0.0.0:8080/trigger/greeting -d ''
```

Status:
 ```bash
curl -X GET http://0.0.0.0:8080/status 
```