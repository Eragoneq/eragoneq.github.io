package templates

import kotlin.test.Test
import kotlin.test.assertContains
import testing.testBuildContext
import testing.testEntry

class PostPagesTest {
    @Test
    fun `nested entry navigation is root relative`() {
        val page = blogPostPage(
            entry = testEntry("posts/nested/current.html"),
            transmissionNo = 2,
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
            transmissionNo = 1,
            prev = null,
            next = null,
            context = testBuildContext(basePath = "/journal"),
        )

        assertContains(page, "href=\"/journal/blog.html\"")
    }
}
