package templates

import kotlin.test.Test
import kotlin.test.assertContains
import testing.testBuildContext
import testing.testEntry
import java.time.LocalDate

class SeoTest {
    @Test
    fun `SEO output uses the injected timestamp and configured URLs`() {
        val context = testBuildContext(basePath = "/journal")
        val post = testEntry(
            url = "posts/entry.html",
            date = LocalDate.parse("2024-01-15"),
        )

        val sitemap = sitemap(listOf(post), emptyList(), context)
        val feed = rssFeed(listOf(post), context)
        val robots = robotsTxt(context)

        assertContains(sitemap, "https://example.test/journal/index.html")
        assertContains(sitemap, "<lastmod>2024-02-29</lastmod>")
        assertContains(sitemap, "<lastmod>2024-01-15</lastmod>")
        assertContains(feed, "Thu, 29 Feb 2024 23:59:58 GMT")
        assertContains(feed, "https://example.test/journal/posts/entry.html")
        assertContains(robots, "Sitemap: https://example.test/journal/sitemap.xml")
    }
}
