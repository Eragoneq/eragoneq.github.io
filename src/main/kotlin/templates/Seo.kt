package templates

import models.Entry
import models.SiteConfig
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Generates the robots.txt file content.
 */
fun robotsTxt(): String {
    return """
        User-agent: *
        Allow: /
        
        Sitemap: ${SiteConfig.BASE_URL}/sitemap.xml
    """.trimIndent()
}

/**
 * Generates the sitemap.xml file content.
 */
fun sitemap(posts: List<Entry>, projects: List<Entry>): String {
    val buildDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    val staticPages = listOf(
        "/index.html",
        "/about.html",
        "/projects.html"
    )

    val allUrls = mutableListOf<String>()
    
    // Add static pages
    for (page in staticPages) {
        allUrls.add("""
            <url>
                <loc>${SiteConfig.BASE_URL}$page</loc>
                <lastmod>$buildDate</lastmod>
                <changefreq>weekly</changefreq>
            </url>
        """.trimIndent())
    }
    
    // Add posts
    for (post in posts) {
        val lastMod = post.lastUpdate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: buildDate
        allUrls.add("""
            <url>
                <loc>${SiteConfig.BASE_URL}/${post.url}</loc>
                <lastmod>$lastMod</lastmod>
                <changefreq>monthly</changefreq>
            </url>
        """.trimIndent())
    }

    // Add projects
    for (project in projects) {
        val lastMod = project.lastUpdate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: buildDate
        allUrls.add("""
            <url>
                <loc>${SiteConfig.BASE_URL}/${project.url}</loc>
                <lastmod>$lastMod</lastmod>
                <changefreq>monthly</changefreq>
            </url>
        """.trimIndent())
    }

    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
${allUrls.joinToString("\n").prependIndent("            ")}
        </urlset>
    """.trimIndent()
}

/**
 * Generates the RSS feed (feed.xml) content for blog posts.
 */
fun rssFeed(posts: List<Entry>): String {
    val buildDateRfc822 = DateTimeFormatter.RFC_1123_DATE_TIME
        .format(java.time.ZonedDateTime.now(ZoneOffset.UTC))

    val items = posts.map { post ->
        val pubDate = if (post.date != null) {
            post.date.atStartOfDay(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME)
        } else buildDateRfc822

        """
            <item>
                <title><![CDATA[${post.title}]]></title>
                <link>${SiteConfig.BASE_URL}/${post.url}</link>
                <guid isPermaLink="true">${SiteConfig.BASE_URL}/${post.url}</guid>
                <description><![CDATA[${post.subtitle}]]></description>
                <pubDate>$pubDate</pubDate>
            </item>
        """.trimIndent()
    }

    return """
        <?xml version="1.0" encoding="UTF-8" ?>
        <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
            <channel>
                <title>${SiteConfig.SITE_NAME}</title>
                <link>${SiteConfig.BASE_URL}</link>
                <description>Eragoneq's Personal Blog and Projects</description>
                <lastBuildDate>$buildDateRfc822</lastBuildDate>
                <atom:link href="${SiteConfig.BASE_URL}/feed.xml" rel="self" type="application/rss+xml" />
${items.joinToString("\n").prependIndent("                ")}
            </channel>
        </rss>
    """.trimIndent()
}
