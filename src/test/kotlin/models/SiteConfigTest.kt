package models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import testing.testSiteConfig

class SiteConfigTest {
    @Test
    fun `builds root-relative and absolute URLs with a project base path`() {
        val config = testSiteConfig("/journal")

        assertEquals("/journal/posts/entry.html", config.sitePath("posts/entry.html"))
        assertEquals("/journal/static/main.css", config.sitePath("/static/main.css"))
        assertEquals(
            "https://example.test/journal/feed.xml",
            config.absoluteUrl("feed.xml"),
        )
    }

    @Test
    fun `rejects a base URL containing a deployment path`() {
        val invalid = testSiteConfig().copy(baseUrl = "https://example.test/journal")

        assertFailsWith<IllegalArgumentException> { invalid.validate() }
    }

    @Test
    fun `rejects an unsafe base path`() {
        val invalid = testSiteConfig().copy(basePath = "/journal/../admin")

        assertFailsWith<IllegalArgumentException> { invalid.validate() }
    }
}
