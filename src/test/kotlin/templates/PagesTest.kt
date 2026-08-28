package templates

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue
import testing.testBuildContext
import testing.testEntry

class PagesTest {
    @Test
    fun `home page renders with injected build date`() {
        val page = homePage(emptyList(), emptyList(), testBuildContext())

        assertTrue(page.startsWith("<!DOCTYPE html>"))
        assertContains(page, "LAST UPDATE")
        assertContains(page, "2024-02-29")
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
}
