


Set environment variable `OPENAI_API_KEY` to your OpenAI API key.

Start server with:
```bash
./gradlew run
```

Example curl:
 ```bash
curl -X POST http://0.0.0.0:8080/events/validate -d '{"event": "value", "error": true}' 
```
