package core

import models.BuildContext
import models.Entry
import models.EntryKind
import models.SiteConfig
import templates.*
import java.io.File
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
    /**
     * Runs the complete build pipeline:
     * 1. Collect entries from markdown content
     * 2. Copy static assets
     * 3. Generate static pages (index, projects, about)
     * 4. Generate individual entry pages (blog posts, project posts)
     *
     * @return a pair of (postCount, projectCount) for reporting
     */
    fun build(): Pair<Int, Int> {
        val context = BuildContext.from(siteConfig.validate(), clock)

        // Collect content entries
        val posts = sortEntriesByNewestFirst(
            collectEntries(contentDir / "posts", "posts", EntryKind.POST, strict),
        )
        val projects = sortEntriesByNewestFirst(
            collectEntries(contentDir / "projects", "projects", EntryKind.PROJECT, strict),
        )

        // Prepare output directory
        outputDir.createDirectories()

        // Copy static assets
        copyStaticAssets()

        // Generate static pages
        writeStaticPages(posts, projects, context)

        // Generate individual entry pages
        writeEntryPages(posts, context)
        writeEntryPages(projects, context)

        // Generate SEO files
        writeSeoFiles(posts, projects, context)

        return posts.size to projects.size
    }

    private fun copyStaticAssets() {
        val staticOutDir = outputDir / "static"

        // Remove existing static output
        if (staticOutDir.exists()) {
            staticOutDir.toFile().deleteRecursively()
        }

        if (!staticSrcDir.exists()) {
            println("  Warning: static source directory not found at $staticSrcDir")
            return
        }

        // Copy the entire static directory tree
        staticSrcDir.toFile().copyRecursively(staticOutDir.toFile(), overwrite = true)

        (staticOutDir / "favicon.svg").toFile().copyTo((outputDir / "favicon.svg").toFile(), overwrite = true)
    }

    private fun writeStaticPages(posts: List<Entry>, projects: List<Entry>, context: BuildContext) {
        writePage(outputDir / "index.html", homePage(posts, projects, context))
        writePage(outputDir / "blog.html", blogPage(posts, context))
        writePage(outputDir / "projects.html", projectsPage(projects, context))
        writePage(outputDir / "about.html", aboutPage(context))
        writePage(outputDir / "contact.html", contactPage(context))
    }

    private fun writeEntryPages(entries: List<Entry>, context: BuildContext) {
        entries.forEachIndexed { index, entry ->
            val outPath = outputDir / entry.outputRelPath
            val number = entries.size - index
            // Newest-first order: the next index holds the older neighbour.
            val older = entries.getOrNull(index + 1)
            val newer = entries.getOrNull(index - 1)
            val html = when (entry.kind) {
                EntryKind.POST -> blogPostPage(entry, number, older, newer, context)
                EntryKind.PROJECT -> projectPostPage(entry, number, older, newer, context)
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
