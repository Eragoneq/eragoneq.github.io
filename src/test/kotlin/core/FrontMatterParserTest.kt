package core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FrontMatterParserTest {
    @Test
    fun `parses YAML front matter and preserves markdown body`() {
        val document = parseFrontMatter(
            "---\r\ntitle: A title\r\ndate: 2024-02-29\r\n---\r\n# Heading\r\n",
            strict = true,
        )

        assertEquals("A title", document.frontmatter.title)
        assertEquals("2024-02-29", document.frontmatter.date)
        assertEquals("# Heading\r\n", document.body)
    }

    @Test
    fun `strict parsing rejects malformed YAML`() {
        assertFailsWith<IllegalArgumentException> {
            parseFrontMatter("---\ntitle: [broken\n---\nBody", strict = true)
        }
    }

    @Test
    fun `strict parsing rejects an unclosed front matter block`() {
        assertFailsWith<IllegalArgumentException> {
            parseFrontMatter("---\ntitle: Missing delimiter\nBody", strict = true)
        }
    }
}
