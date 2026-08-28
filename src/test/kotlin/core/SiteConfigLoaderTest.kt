package core

import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SiteConfigLoaderTest {
    @Test
    fun `loads and validates site configuration`() {
        val root = createTempDirectory("site-config-test")
        try {
            val configFile = root.resolve("site.yml")
            configFile.writeText(
                """
                    site_name: Example
                    title_prefix: "Example // "
                    base_url: https://example.test
                    base_path: /journal
                    description: Example site
                    home_post_limit: 2
                    home_project_limit: 4
                """.trimIndent(),
            )

            val config = loadSiteConfig(configFile)

            assertEquals("/journal", config.basePath)
            assertEquals(2, config.homePostLimit)
        } finally {
            root.toFile().deleteRecursively()
        }
    }

    @Test
    fun `rejects unknown configuration fields`() {
        val root = createTempDirectory("site-config-test")
        try {
            val configFile = root.resolve("site.yml")
            configFile.writeText(
                """
                    site_name: Example
                    title_prefix: "Example // "
                    base_url: https://example.test
                    description: Example site
                    unexpected: value
                """.trimIndent(),
            )

            assertFailsWith<IllegalArgumentException> { loadSiteConfig(configFile) }
        } finally {
            root.toFile().deleteRecursively()
        }
    }
}
