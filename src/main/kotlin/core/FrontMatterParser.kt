package core

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import models.FrontMatter

/**
 * Result of parsing a Markdown file that may contain YAML front matter.
 */
data class ParsedDocument(
    val frontmatter: FrontMatter,
    val body: String,
)

private val FRONTMATTER_REGEX = Regex("^---\\s*\\n(.*?)\\n---\\s*\\n(.*)", RegexOption.DOT_MATCHES_ALL)

private val yamlConfig = Yaml(configuration = YamlConfiguration(strictMode = false))

/**
 * Extracts YAML front matter (delimited by `---`) from a raw Markdown string.
 *
 * If no front matter is found, returns an empty FrontMatter and the original string.
 */
fun parseFrontMatter(raw: String): ParsedDocument {
    val trimmed = raw.trimStart()

    val match = FRONTMATTER_REGEX.find(trimmed)
    if (match != null) {
        val yamlBlock = match.groupValues[1].trim()
        val body = match.groupValues[2].trimStart('\n', '\r')

        val frontmatter = runCatching {
            yamlConfig.decodeFromString<FrontMatter>(yamlBlock)
        }.getOrElse {
            println("Warning: Failed to parse front matter: ${it.message}")
            FrontMatter()
        }

        return ParsedDocument(frontmatter, body)
    }

    return ParsedDocument(FrontMatter(), raw)
}
