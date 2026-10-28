package core

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import models.FrontMatter

data class ParsedDocument(
    val frontmatter: FrontMatter,
    val body: String,
)

private val FRONTMATTER_REGEX = Regex(
    "^---[ \\t]*\\r?\\n(.*?)\\r?\\n---[ \\t]*(?:\\r?\\n(.*))?$",
    RegexOption.DOT_MATCHES_ALL,
)

private val permissiveYaml = Yaml(configuration = YamlConfiguration(strictMode = false))
private val strictYaml = Yaml(configuration = YamlConfiguration(strictMode = true))

fun parseFrontMatter(raw: String, strict: Boolean = false): ParsedDocument {
    val trimmed = raw.trimStart()

    val match = FRONTMATTER_REGEX.find(trimmed)
    if (match != null) {
        val yamlBlock = match.groupValues[1].trim()
        val body = match.groupValues.getOrElse(2) { "" }.trimStart('\n', '\r')

        val frontmatter = runCatching {
            val yaml = if (strict) strictYaml else permissiveYaml
            yaml.decodeFromString<FrontMatter>(yamlBlock)
        }.getOrElse { error ->
            if (strict) {
                throw IllegalArgumentException("Failed to parse front matter: ${error.message}", error)
            }
            println("Warning: Failed to parse front matter: ${error.message}")
            FrontMatter()
        }

        return ParsedDocument(frontmatter, body)
    }

    if (strict && trimmed.lineSequence().firstOrNull()?.trimEnd() == "---") {
        throw IllegalArgumentException("Front matter has no valid closing --- delimiter")
    }

    return ParsedDocument(FrontMatter(), raw)
}
