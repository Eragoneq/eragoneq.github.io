package templates

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import testing.testBuildContext
import testing.testEntry

class PagesTest {
    @Test
    fun `home page renders with injected build date`() {
        val page = homePage(emptyList(), emptyList(), testBuildContext())

        assertTrue(page.startsWith("<!DOCTYPE html>"))
        assertContains(page, "Last rebuilt")
        assertContains(page, "2024-02-29")
        assertContains(page, "23:59 UTC")
        assertTrue(page.indexOf("Last rebuilt") > page.indexOf("<footer"))
        assertFalse(page.contains("SYS "))
    }

    @Test
    fun `entry cards and assets include the configured base path`() {
        val page = homePage(
            posts = listOf(testEntry("posts/nested/entry.html")),
            projects = emptyList(),
            context = testBuildContext(basePath = "/journal"),
        )

        assertContains(page, "href=\"/journal/posts/nested/entry.html\"")
        assertContains(page, "href=\"/journal/static/css/main.css\"")
        assertContains(page, "src=\"/journal/static/js/script.js\"")
    }

    @Test
    fun `former personal pages forward to the correct homepage section with a base path`() {
        val context = testBuildContext(basePath = "/journal")
        assertContains(homeSectionRedirect("about", context), "0;url=/journal/index.html#about")
        assertContains(homeSectionRedirect("contact", context), "href=\"/journal/index.html#contact\"")
    }
}
