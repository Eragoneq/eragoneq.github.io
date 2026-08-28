package core

import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import testing.testSiteConfig

class SiteValidatorTest {
    @Test
    fun `reports missing internal links and malformed XML`() {
        val site = createTempDirectory("site-validator-test")
        try {
            val config = testSiteConfig("/journal")
            site.resolve("index.html").writeText(
                "<!doctype html><a href=\"/journal/missing.html\">Missing</a>",
            )
            site.resolve("sitemap.xml").writeText("<urlset>")
            site.resolve("feed.xml").writeText("<rss/>")
            site.resolve("robots.txt").writeText(
                "Sitemap: https://example.test/journal/sitemap.xml",
            )

            val report = validateSite(site, config)

            assertFalse(report.isValid)
            assertTrue(report.errors.any { "missing target" in it })
            assertTrue(report.errors.any { "sitemap.xml is invalid" in it })
        } finally {
            site.toFile().deleteRecursively()
        }
    }
}
