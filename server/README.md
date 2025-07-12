


Set environment variable `OPENAI_API_KEY` to your OpenAI API key.

Start server with:
```bash
./gradlew run
```

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

Test Email:
 ```bash
curl -X POST http://0.0.0.0:8080/modules/email -d ''
```

Status:
 ```bash
curl -X GET http://0.0.0.0:8080/status 
```