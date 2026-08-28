package core

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import testing.testSiteConfig

class SiteBuilderTest {
    @Test
    fun `build produces ordered pages deterministic SEO and valid links`() {
        val root = createTempDirectory("site-builder-test")
        try {
            val content = root.resolve("content")
            val posts = content.resolve("posts")
            posts.resolve("nested").createDirectories()
            posts.resolve("older.md").writeText(markdown("Older", "2024-01-01"))
            posts.resolve("nested/newer.md").writeText(markdown("Newer", "2024-02-01"))

            val static = root.resolve("static")
            static.resolve("css").createDirectories()
            static.resolve("js").createDirectories()
            static.resolve("img").createDirectories()
            static.resolve("css/main.css").writeText("body {}")
            static.resolve("js/script.js").writeText("")
            static.resolve("img/logo.svg").writeText("<svg xmlns=\"http://www.w3.org/2000/svg\"/>")
            static.resolve("img/favico.svg").writeText("<svg xmlns=\"http://www.w3.org/2000/svg\"/>")

            val output = root.resolve("public")
            val config = testSiteConfig("/journal")
            val counts = SiteBuilder(
                contentDir = content,
                outputDir = output,
                staticSrcDir = static,
                siteConfig = config,
                clock = Clock.fixed(Instant.parse("2024-02-29T23:59:58Z"), ZoneOffset.UTC),
                strict = true,
            ).build()

            assertEquals(2 to 0, counts)
            val blogPage = output.resolve("blog.html").readText()
            assertTrue(blogPage.indexOf("Newer") < blogPage.indexOf("Older"))
            assertContains(
                output.resolve("posts/nested/newer.html").readText(),
                "href=\"/journal/posts/older.html\"",
            )
            assertContains(output.resolve("sitemap.xml").readText(), "<lastmod>2024-02-29</lastmod>")
            assertContains(output.resolve("feed.xml").readText(), "Thu, 29 Feb 2024 23:59:58 GMT")

            val report = validateSite(output, config)
            assertTrue(report.isValid, report.errors.joinToString("\n"))
        } finally {
            root.toFile().deleteRecursively()
        }
    }

    private fun markdown(title: String, date: String): String =
        "---\ntitle: $title\nsubtitle: Test entry\ndate: $date\n---\n# $title\n"
}
