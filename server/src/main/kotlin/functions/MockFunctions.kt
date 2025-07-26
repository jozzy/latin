package org.latin.server.functions

import org.eclipse.lmos.arc.agents.dsl.FunctionDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.extensions.info

fun FunctionDefinitionContext.buildMockFunctions() {
// Product Monitoring Mock Functions
    function(
        name = "web_search",
        description = "Searches web for product mentions",
        params = types(
            string("query", "Search query for product mentions"),
            string("timeframe", "Time period to search (e.g., '7 days')"),
        ),
    ) { (query, timeframe) ->
        info("Searching web for: $query in timeframe: $timeframe")
        "Found 15 mentions of $query in the last $timeframe across 8 websites"
    }

    function(
        name = "social_media_search",
        description = "Searches social media platforms for mentions",
        params = types(
            string("platform", "Social media platform to search"),
            string("query", "Search query"),
        ),
    ) { (platform, query) ->
        info("Searching $platform for: $query")
        "Found 8 social media mentions on $platform for '$query'"
    }

    function(
        name = "news_search",
        description = "Searches news articles for product mentions",
        params = types(
            string("query", "Search query for news articles"),
        ),
    ) { (query) ->
        info("Searching news for: $query")
        "Found 3 news articles mentioning '$query' in the last week"
    }

    function(
        name = "analyze_sentiment",
        description = "Analyzes sentiment of mentions",
        params = types(
            string("text", "Text to analyze for sentiment"),
        ),
    ) { (text) ->
        val textStr = text.toString()
        val preview = if (textStr.length > 50) "${textStr.substring(0, 50)}..." else text

        info("Analyzing sentiment for: $preview")
        "Positive sentiment detected in mention (confidence: 85%)"
    }

    function(
        name = "categorize_mentions",
        description = "Categorizes mentions by topic",
        params = types(
            string("mentions", "List of mentions to categorize"),
        ),
    ) { (mentions) ->
        info("Categorizing mentions")
        "Categorized 26 mentions: 12 reviews, 8 complaints, 4 news articles, 2 general discussions"
    }

    function(
        name = "extract_key_insights",
        description = "Extracts key insights from mentions",
        params = types(
            string("data", "Mention data to analyze"),
        ),
    ) { (data) ->
        info("Extracting key insights from mention data")
        "Key insights: 1) Positive trend in customer satisfaction, 2) Feature X mentioned frequently, 3) Pricing concerns in 15% of mentions"
    }

    function(
        name = "format_report",
        description = "Creates a professional report from data",
        params = types(
            string("data", "Data to include in report"),
            string("format", "Report format (PDF, HTML, etc.)"),
        ),
    ) { (data, format) ->
        info("Formatting report in $format format")
        "Report generated successfully in $format format with executive summary and detailed analysis"
    }

    function(
        name = "create_charts",
        description = "Creates charts and visualizations",
        params = types(
            string("data", "Data for chart generation"),
            string("chart_type", "Type of chart to create"),
        ),
    ) { (data, chart_type) ->
        info("Creating $chart_type chart from data")
        "Generated $chart_type chart showing trends and patterns"
    }

    function(
        name = "send_email",
        description = "Sends email with attachment",
        params = types(
            string("to", "Recipient email addresses"),
            string("subject", "Email subject"),
            string("body", "Email body"),
            string("attachment", "Path to attachment file"),
        ),
    ) { (to, subject, body, attachment) ->
        info("Sending email to: $to with subject: $subject")
        "Email sent successfully to $to with attachment: $attachment"
    }

    function(
        name = "send_urgent_alert",
        description = "Sends urgent alert to crisis management team",
        params = types(
            string("message", "Alert message"),
            string("priority", "Alert priority level"),
        ),
    ) { (message, priority) ->
        info("Sending urgent alert: $message with priority: $priority")
        "Urgent alert sent to crisis management team: $message"
    }

    function(
        name = "analyze_trend",
        description = "Analyzes if mention volume indicates viral moment",
        params = types(
            string("data", "Mention data to analyze"),
        ),
    ) { (data) ->
        info("Analyzing trend in mention data")
        "Trend analysis: Mention volume increased 300% in last 24 hours - potential viral moment detected"
    }

    function(
        name = "notify_team",
        description = "Notifies team about high volume mentions",
        params = types(
            string("team", "Team to notify"),
            string("message", "Notification message"),
        ),
    ) { (team, message) ->
        info("Notifying $team: $message")
        "Notification sent to $team: $message"
    }
}
