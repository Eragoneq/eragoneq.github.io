package templates

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import models.Entry
import models.SiteConfig
import java.time.format.DateTimeFormatter

private val DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")

/**
 * Individual blog post page.
 */
fun blogPostPage(entry: Entry, year: Int): String = basePage("${entry.title}${SiteConfig.TITLE_SUFFIX}", year, "/static/css/post.css") {
            // Rendered markdown content (already HTML)
            unsafe { +entry.content }
            hr {}
            div(classes = "post-meta") {
                p { +"[TBA]" }
                p {
                    if (entry.date != null) {
                        time {
                            attributes["datetime"] = entry.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            +"[${entry.date.format(DISPLAY_FORMAT)}]"
                        }
                    } else {
                        +"[Unknown Date]"
                    }
                }
                p {
                    a(href = "/index.html") { +"\u2190 Back to blog" }
                }
            }
}

/**
 * Individual project post page.
 */
fun projectPostPage(entry: Entry, year: Int): String = basePage("${entry.title}${SiteConfig.TITLE_SUFFIX}", year, "/static/css/post.css") {
            // Rendered markdown content (already HTML)
            unsafe { +entry.content }
            hr {}
            div(classes = "post-meta") {
                if (entry.status.isNotBlank()) {
                    p { +"[${entry.status}]" }
                } else {
                    p { +"[PROJECT]" }
                }
                if (entry.date != null) {
                    p {
                        time {
                            attributes["datetime"] = entry.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            +"[${entry.date.format(DISPLAY_FORMAT)}]"
                        }
                    }
                } else {
                    p { +"[$year]" }
                }
                p {
                    a(href = "/projects.html") { +"\u2190 Back to projects" }
                }
            }
}
