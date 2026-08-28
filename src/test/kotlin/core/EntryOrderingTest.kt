package core

import kotlin.test.Test
import kotlin.test.assertEquals
import testing.testEntry
import java.time.LocalDate

class EntryOrderingTest {
    @Test
    fun `orders newest entries first and resolves date ties by URL`() {
        val entries = listOf(
            testEntry("posts/no-date.html", date = null),
            testEntry("posts/zulu.html", date = LocalDate.parse("2024-03-01")),
            testEntry("posts/alpha.html", date = LocalDate.parse("2024-03-01")),
            testEntry("posts/older.html", date = LocalDate.parse("2023-12-31")),
        )

        assertEquals(
            listOf(
                "posts/alpha.html",
                "posts/zulu.html",
                "posts/older.html",
                "posts/no-date.html",
            ),
            sortEntriesByNewestFirst(entries).map { it.url },
        )
    }
}
