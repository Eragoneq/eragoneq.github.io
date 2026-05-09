package core

import models.Entry
import models.EntryKind
import models.FrontMatter
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.io.path.*
import kotlinx.coroutines.*

/**
 * Date formats to attempt when parsing front matter dates.
 */
private val DATE_FORMATS = listOf(
    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    DateTimeFormatter.ofPattern("dd-MM-yyyy"),
    DateTimeFormatter.ofPattern("yyyy/MM/dd"),
)

/**
 * Parses a raw front matter date string into a LocalDate.
 */
fun parseEntryDate(rawDate: String?): LocalDate? {
    if (rawDate == null) return null
    return tryParseDateString(rawDate)
}

private fun tryParseDateString(text: String): LocalDate? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null

    for (fmt in DATE_FORMATS) {
        try {
            return LocalDate.parse(trimmed, fmt)
        } catch (_: DateTimeParseException) {
            // try next format
        }
    }
    return null
}

/**
 * Extracts the title from front matter, falling back to the filename stem.
 */
fun titleForEntry(frontmatter: FrontMatter, mdPath: Path, kind: EntryKind): String {
    val title = if (kind == EntryKind.PROJECT) {
        frontmatter.name ?: frontmatter.title
    } else {
        frontmatter.title
    }
    
    if (!title.isNullOrBlank()) {
        return title
    }
    return mdPath.nameWithoutExtension.replace("-", " ")
        .replaceFirstChar { it.uppercaseChar() }
}

/**
 * Extracts the subtitle from front matter.
 */
fun subtitleForEntry(frontmatter: FrontMatter, kind: EntryKind): String {
    val subtitle = if (kind == EntryKind.PROJECT) {
        frontmatter.description ?: frontmatter.subtitle
    } else {
        frontmatter.subtitle ?: frontmatter.description
    }
    return subtitle ?: ""
}

/**
 * Walks a source directory for `.md` files, parses each one, and returns
 * a sorted list of [Entry] objects.
 *
 * @param sourceDir  directory containing markdown files (e.g. `content/posts`)
 * @param outputSubdir  output subdirectory name (e.g. "posts")
 * @param kind  entry kind — "post" or "project"
 */
fun collectEntries(sourceDir: Path, outputSubdir: String, kind: EntryKind): List<Entry> {
    if (!sourceDir.exists()) return emptyList()

    val fileList = sourceDir.toFile().walkTopDown()
        .filter { it.isFile && it.extension == "md" }
        .toList()

    val entries = runBlocking(Dispatchers.Default) {
        fileList.map { file ->
            async {
                runCatching {
                    val mdPath = file.toPath()
                    val relPath = sourceDir.relativize(mdPath)
                    
                    val parentFolder = if (relPath.parent != null) relPath.parent.toString() else ""
                    val newFileName = "${mdPath.nameWithoutExtension}.html"
                    
                    val htmlRelPath = Path(outputSubdir).resolve(parentFolder).resolve(newFileName).normalize()

                    val rawMarkdown = file.readText(Charsets.UTF_8)
                    val (frontmatter, body) = parseFrontMatter(rawMarkdown)
                    val renderedHtml = renderMarkdown(body)

                    val dateValue = parseEntryDate(frontmatter.date)
                    val lastUpdate = parseEntryDate(frontmatter.lastUpdate ?: frontmatter.date)

                    Entry(
                        kind = kind,
                        sourcePath = mdPath,
                        outputRelPath = htmlRelPath,
                        url = htmlRelPath.invariantSeparatorsPathString,
                        slug = mdPath.nameWithoutExtension,
                        title = titleForEntry(frontmatter, mdPath, kind),
                        subtitle = subtitleForEntry(frontmatter, kind),
                        date = dateValue,
                        frontmatter = frontmatter,
                        content = renderedHtml,
                        status = if (kind == EntryKind.PROJECT) {
                            frontmatter.status?.trim() ?: ""
                        } else "",
                        lastUpdate = lastUpdate,
                    )
                }.onFailure { 
                    println("Warning: Failed to process ${file.name}. Error: ${it.message}")
                }.getOrNull()
            }
        }.awaitAll().filterNotNull().sortedBy { it.url }
    }

    return entries
}
