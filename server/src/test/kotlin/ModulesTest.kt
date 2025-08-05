package org.latin.server

import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ModulesTest {

    @Test
    fun `test greeting module`() {
        val responseBody = call("chat", "Hi, how are you?")
        assert(responseBody == "Hello, I am MAI!")
    }

    @Test
    fun `test validate_events module - OK`() {
        val responseBody = call("ping", "{\"event\": \"value\", \"status\": up}")
        assert(responseBody == "OK")
    }

    @Test
    fun `test validate_events module - NOT_OK`() {
        val responseBody = call("ping", "{\"event\": \"value\", \"status\": down}")
        assert(responseBody == "NOT_OK")
    }

    private fun call(endpoint: String, data: String): String {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/events/$endpoint"))
            .header("Content-Type", "text/plain")
            .POST(HttpRequest.BodyPublishers.ofString(data))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}
