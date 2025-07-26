


Set environment variable `OPENAI_API_KEY` to your OpenAI API key.

Start server with:
```bash
./gradlew run
```

SSE Event endpoint: http://0.0.0.0:8080/runs

Test validate events:
 ```bash
curl -X POST http://0.0.0.0:8080/events/ping -d '{"event": "value", "status": up}' 
```

Test chat:
 ```bash
curl -X POST http://0.0.0.0:8080/events/chat -d 'Hi, how are you?'
```

Test complaint:
 ```bash
curl -X POST http://0.0.0.0:8080/events/customer -d 'I am not happy with the service. I am being charged double.'
```

Test Search Mentions:
 ```bash
curl -X POST http://0.0.0.0:8080/events/search_mentions -d  'search for OurProduct mentions across all platforms'
```

Test product monitoring:
 ```bash
curl -X POST http://localhost:8080/modules/product_monitoring 
```

Test Email:
 ```bash
curl -X POST http://0.0.0.0:8080/events/send_email -d '{report: "Stocks are going up!"}'
```

Test Greeting:
 ```bash
curl -X POST http://0.0.0.0:8080/events/greeting -d ''
```

Status:
 ```bash
curl -X GET http://0.0.0.0:8080/status 
```