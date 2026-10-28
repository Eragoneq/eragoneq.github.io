package core

import models.BuildContext
import models.Entry
import models.EntryKind
import models.SiteConfig
import templates.*
import java.nio.file.Path
import java.time.Clock
import java.time.LocalDate
import kotlin.io.path.*

class SiteBuilder(
    private val contentDir: Path,
    private val outputDir: Path,
    private val staticSrcDir: Path,
    private val siteConfig: SiteConfig,
    private val clock: Clock = Clock.systemUTC(),
    private val strict: Boolean = false,
) {
    fun build(): Pair<Int, Int> {
        val context = BuildContext.from(siteConfig.validate(), clock)

        val posts = sortEntriesByNewestFirst(
            collectEntries(contentDir / "posts", "posts", EntryKind.POST, strict),
        )
        val projects = sortEntriesByNewestFirst(
            collectEntries(contentDir / "projects", "projects", EntryKind.PROJECT, strict),
        )

        outputDir.createDirectories()

        copyStaticAssets()

        writeStaticPages(posts, projects, context)

        writeEntryPages(posts, context)
        writeEntryPages(projects, context)

        writeSeoFiles(posts, projects, context)

        return posts.size to projects.size
    }

    private fun copyStaticAssets() {
        val staticOutDir = outputDir / "static"

        if (staticOutDir.exists()) {
            staticOutDir.toFile().deleteRecursively()
        }

        if (!staticSrcDir.exists()) {
            println("  Warning: static source directory not found at $staticSrcDir")
            return
        }

        staticSrcDir.toFile().copyRecursively(staticOutDir.toFile(), overwrite = true)

        (staticOutDir / "favicon.svg").toFile().copyTo((outputDir / "favicon.svg").toFile(), overwrite = true)
    }

    private fun writeStaticPages(posts: List<Entry>, projects: List<Entry>, context: BuildContext) {
        writePage(outputDir / "index.html", homePage(posts, projects, context))
        writePage(outputDir / "blog.html", blogPage(posts, context))
        writePage(outputDir / "projects.html", projectsPage(projects, context))
        writePage(outputDir / "about.html", homeSectionRedirect("about", context))
        writePage(outputDir / "contact.html", homeSectionRedirect("contact", context))
    }

    private fun writeEntryPages(entries: List<Entry>, context: BuildContext) {
        entries.forEachIndexed { index, entry ->
            val outPath = outputDir / entry.outputRelPath
            // Newest-first order: the next index holds the older neighbour.
            val older = entries.getOrNull(index + 1)
            val newer = entries.getOrNull(index - 1)
            val html = when (entry.kind) {
                EntryKind.POST -> blogPostPage(entry, older, newer, context)
                EntryKind.PROJECT -> projectPostPage(entry, older, newer, context)
            }
            writePage(outPath, html)
        }
    }

    private fun writePage(path: Path, content: String) {
        path.parent.createDirectories()
        path.writeText(content, Charsets.UTF_8)
    }

    private fun writeSeoFiles(posts: List<Entry>, projects: List<Entry>, context: BuildContext) {
        writePage(outputDir / "robots.txt", robotsTxt(context))
        writePage(outputDir / "sitemap.xml", sitemap(posts, projects, context))
        writePage(outputDir / "feed.xml", rssFeed(posts, context))
    }
}

internal fun sortEntriesByNewestFirst(entries: List<Entry>): List<Entry> =
    entries.sortedWith(
        compareByDescending<Entry> { it.date ?: LocalDate.MIN }
            .thenBy { it.url },
    )
