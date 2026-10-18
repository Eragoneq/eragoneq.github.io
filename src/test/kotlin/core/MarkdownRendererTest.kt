package core

import kotlin.test.Test
import kotlin.test.assertContains

class MarkdownRendererTest {
    @Test
    fun `windows line endings close code fences before following prose`() {
        val html = renderMarkdown("```kotlin\r\nprintln(42)\r\n```\r\n\r\n## Next section\r\n\r\nOrdinary prose.")
        assertContains(html, "<h2>Next section</h2>")
        assertContains(html, "<p>Ordinary prose.</p>")
        kotlin.test.assertTrue(html.indexOf("</pre>") < html.indexOf("<h2>"))
    }

    @Test
    fun `renders common markdown without a document body wrapper`() {
        val html = renderMarkdown("# Heading\n\nText with **bold** content.")

        assertContains(html, "<h1>Heading</h1>")
        assertContains(html, "<strong>bold</strong>")
        kotlin.test.assertFalse(html.contains("<body>"))
    }
}
