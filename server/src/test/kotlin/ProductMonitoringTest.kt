package org.latin.server

import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ProductMonitoringTest {

    @Test
    fun `test search_mentions module`() {
        val responseBody = call("search_mentions", "search for OurProduct mentions")
        assert(responseBody.contains("Found") || responseBody.contains("mentions"))
    }

    @Test
    fun `test summarize_insights module`() {
        val responseBody = call("summarize_insights", "summarize findings from search")
        assert(responseBody.contains("sentiment") || responseBody.contains("insights"))
    }

    @Test
    fun `test create_report module`() {
        val responseBody = call("create_report", "create weekly report from insights")
        assert(responseBody.contains("report") || responseBody.contains("PDF"))
    }

    @Test
    fun `test send_email module`() {
        val responseBody = call("send_email", "send weekly report")
        assert(responseBody.contains("Email sent") || responseBody.contains("successfully"))
    }

    @Test
    fun `test mention_alert module`() {
        val responseBody = call("mention_alert", "critical mention alert")
        assert(responseBody.contains("alert") || responseBody.contains("crisis"))
    }

    @Test
    fun `test high_volume_alert module`() {
        val responseBody = call("high_volume_alert", "high volume mention alert")
        assert(responseBody.contains("volume") || responseBody.contains("trend"))
    }

    @Test
    fun `test product_monitoring module directly`() {
        val responseBody = callModule("product_monitoring", "execute weekly monitoring")
        assert(responseBody.contains("timer") || responseBody.contains("monitoring"))
    }

    @Test
    fun `test search_mentions module directly`() {
        val responseBody = callModule("search_mentions", "search for OurProduct")
        assert(responseBody.contains("Found") || responseBody.contains("mentions"))
    }

    @Test
    fun `test summarize_insights module directly`() {
        val responseBody = callModule("summarize_insights", "summarize data")
        assert(responseBody.contains("sentiment") || responseBody.contains("insights"))
    }

    @Test
    fun `test create_report module directly`() {
        val responseBody = callModule("create_report", "create report")
        assert(responseBody.contains("report") || responseBody.contains("format"))
    }

    @Test
    fun `test send_email module directly`() {
        val responseBody = callModule("send_email", "send email")
        assert(responseBody.contains("Email sent") || responseBody.contains("successfully"))
    }

    @Test
    fun `test mention_alert module directly`() {
        val responseBody = callModule("mention_alert", "send alert")
        assert(responseBody.contains("alert") || responseBody.contains("crisis"))
    }

    @Test
    fun `test high_volume_alert module directly`() {
        val responseBody = callModule("high_volume_alert", "analyze high volume")
        assert(responseBody.contains("volume") || responseBody.contains("trend"))
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

    private fun callModule(moduleName: String, data: String): String {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/modules/$moduleName"))
            .header("Content-Type", "text/plain")
            .POST(HttpRequest.BodyPublishers.ofString(data))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
} 