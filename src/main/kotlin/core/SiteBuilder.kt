package core

import models.Entry
import models.EntryKind
import templates.*
import java.nio.file.Path
import java.time.Year
import kotlin.io.path.*

/**
 * Orchestrates the full static site build.
 *
 * @param contentDir  directory containing `posts/` and `projects/` subdirs with `.md` files
 * @param outputDir   target directory for generated HTML files
 * @param staticSrcDir  source directory for static assets (css, js, fonts, img)
 */
class SiteBuilder(
    private val contentDir: Path,
    private val outputDir: Path,
    private val staticSrcDir: Path,
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
        val year = Year.now().value

        // Collect content entries
        val posts = collectEntries(contentDir / "posts", "posts", EntryKind.POST)
        val projects = collectEntries(contentDir / "projects", "projects", EntryKind.PROJECT)


        // Prepare output directory
        outputDir.createDirectories()

        // Copy static assets
        copyStaticAssets()

        // Generate static pages
        writeStaticPages(posts, projects, year)

        // Generate individual entry pages
        writeEntryPages(posts, year)
        writeEntryPages(projects, year)

        // Generate SEO files
        writeSeoFiles(posts, projects)

        return posts.size to projects.size
    }

    /**
     * Copies static assets (CSS, JS, fonts, images) to the output directory.
     */
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
    }

    /**
     * Generates and writes the static pages (index, projects, about).
     */
    private fun writeStaticPages(posts: List<Entry>, projects: List<Entry>, year: Int) {
        writePage(outputDir / "index.html", indexPage(posts, year))
        writePage(outputDir / "projects.html", projectsPage(projects, year))
        writePage(outputDir / "about.html", aboutPage(year))
    }

    /**
     * Generates and writes individual entry pages.
     */
    private fun writeEntryPages(entries: List<Entry>, year: Int) {
        for (entry in entries) {
            val outPath = outputDir / entry.outputRelPath
            val html = when (entry.kind) {
                EntryKind.POST -> blogPostPage(entry, year)
                EntryKind.PROJECT -> projectPostPage(entry, year)
            }
            writePage(outPath, html)
        }
    }

    /**
     * Writes an HTML string to a file, creating parent directories as needed.
     */
    private fun writePage(path: Path, content: String) {
        path.parent.createDirectories()
        path.writeText(content, Charsets.UTF_8)
    }

    /**
     * Generates and writes SEO-related files.
     */
    private fun writeSeoFiles(posts: List<Entry>, projects: List<Entry>) {
        writePage(outputDir / "robots.txt", robotsTxt())
        writePage(outputDir / "sitemap.xml", sitemap(posts, projects))
        writePage(outputDir / "feed.xml", rssFeed(posts))
    }
}
