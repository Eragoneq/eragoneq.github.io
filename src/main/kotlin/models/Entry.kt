package models

import java.nio.file.Path

import java.time.LocalDate

enum class EntryKind {
    POST,
    PROJECT
}

/**
 * Represents a single content entry (blog post or project).
 * Produced by parsing a Markdown file with YAML front matter.
 */
data class Entry(
    val kind: EntryKind,
    val sourcePath: Path,
    val outputRelPath: Path,
    val url: String,
    val slug: String,
    val title: String,
    val subtitle: String,
    val date: LocalDate?,
    val lastUpdate: LocalDate?,
    val frontmatter: FrontMatter,
    val content: String,
    val status: String = "",
)
