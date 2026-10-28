package templates

import kotlin.test.Test
import kotlin.test.assertContains
import testing.testBuildContext
import testing.testEntry

class PostPagesTest {
    @Test
    fun `missing neighbours are noninteractive while existing ones remain links`() {
        val page = projectPostPage(
            entry = testEntry("projects/current.html"),
            prev = null,
            next = testEntry("projects/next.html"),
            context = testBuildContext(),
        )

        assertContains(page, "<span class=\"post-nav-unavailable\">← Prev</span>")
        assertContains(page, "href=\"/projects/next.html\"")
    }

    @Test
    fun `nested entry navigation is root relative`() {
        val page = blogPostPage(
            entry = testEntry("posts/nested/current.html"),
            prev = testEntry("posts/archive/older.html"),
            next = testEntry("posts/newer.html"),
            context = testBuildContext(basePath = "/journal"),
        )

        assertContains(page, "href=\"/journal/posts/archive/older.html\"")
        assertContains(page, "href=\"/journal/posts/newer.html\"")
    }

    @Test
    fun `single entry navigation links back to root-relative listing`() {
        val page = blogPostPage(
            entry = testEntry("posts/nested/current.html"),
            prev = null,
            next = null,
            context = testBuildContext(basePath = "/journal"),
        )

        assertContains(page, "href=\"/journal/blog.html\"")
    }
}
