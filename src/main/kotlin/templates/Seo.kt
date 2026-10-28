package templates

import models.Entry
import models.BuildContext
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun robotsTxt(context: BuildContext): String {
    return """
        User-agent: *
        Allow: /
        
        Sitemap: ${context.site.absoluteUrl("sitemap.xml")}
    """.trimIndent()
}

fun sitemap(posts: List<Entry>, projects: List<Entry>, context: BuildContext): String {
    val buildDate = context.buildDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    val allUrls = buildList {
        listOf("/index.html", "/blog.html", "/projects.html").forEach { page ->
            add(sitemapEntry(page, buildDate, "weekly", context))
        }
        (posts + projects).forEach { entry ->
            val lastModified = entry.lastUpdate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: buildDate
            add(sitemapEntry(entry.url, lastModified, "monthly", context))
        }
    }

    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
${allUrls.joinToString("\n").prependIndent("            ")}
        </urlset>
    """.trimIndent()
}

private fun sitemapEntry(
    path: String,
    lastModified: String,
    changeFrequency: String,
    context: BuildContext,
): String = """
    <url>
        <loc>${xmlText(context.site.absoluteUrl(path))}</loc>
        <lastmod>$lastModified</lastmod>
        <changefreq>$changeFrequency</changefreq>
    </url>
""".trimIndent()

fun rssFeed(posts: List<Entry>, context: BuildContext): String {
    val buildDateRfc822 = DateTimeFormatter.RFC_1123_DATE_TIME
        .format(context.generatedAt.atZone(ZoneOffset.UTC))

    val items = posts.map { post ->
        val pubDate = if (post.date != null) {
            post.date.atStartOfDay(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME)
        } else buildDateRfc822

        """
            <item>
                <title>${cdata(post.title)}</title>
                <link>${xmlText(context.site.absoluteUrl(post.url))}</link>
                <guid isPermaLink="true">${xmlText(context.site.absoluteUrl(post.url))}</guid>
                <description>${cdata(post.subtitle)}</description>
                <pubDate>$pubDate</pubDate>
            </item>
        """.trimIndent()
    }

    return """
        <?xml version="1.0" encoding="UTF-8" ?>
        <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
            <channel>
                <title>${xmlText(context.site.siteName)}</title>
                <link>${xmlText(context.site.absoluteUrl(""))}</link>
                <description>${xmlText(context.site.description)}</description>
                <lastBuildDate>$buildDateRfc822</lastBuildDate>
                <atom:link href="${xmlText(context.site.absoluteUrl("feed.xml"))}" rel="self" type="application/rss+xml" />
${items.joinToString("\n").prependIndent("                ")}
            </channel>
        </rss>
    """.trimIndent()
}

private fun xmlText(value: String): String = value
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&apos;")

private fun cdata(value: String): String =
    "<![CDATA[${value.replace("]]>", "]]]]><![CDATA[>")}]]>"
