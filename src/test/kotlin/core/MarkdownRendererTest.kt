package core

import kotlin.test.Test
import kotlin.test.assertContains

class MarkdownRendererTest {
    @Test
    fun `renders common markdown without a document body wrapper`() {
        val html = renderMarkdown("# Heading\n\nText with **bold** content.")

        assertContains(html, "<h1>Heading</h1>")
        assertContains(html, "<strong>bold</strong>")
        kotlin.test.assertFalse(html.contains("<body>"))
    }
}
