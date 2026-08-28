package core

import models.EntryKind
import kotlin.io.path.createDirectory
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class EntryCollectorTest {
    @Test
    fun `strict collection rejects invalid front matter dates`() {
        val root = createTempDirectory("entry-collector-test")
        try {
            val posts = root.resolve("posts").createDirectory()
            posts.resolve("invalid.md").writeText(
                "---\ntitle: Invalid date\ndate: someday\n---\nBody",
            )

            val error = assertFailsWith<ContentValidationException> {
                collectEntries(posts, "posts", EntryKind.POST, strict = true)
            }
            kotlin.test.assertContains(error.message.orEmpty(), "date must use")

            val permissiveEntries = collectEntries(posts, "posts", EntryKind.POST)
            assertEquals(1, permissiveEntries.size)
            assertNull(permissiveEntries.single().date)
        } finally {
            root.toFile().deleteRecursively()
        }
    }
}
