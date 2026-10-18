package templates

import kotlinx.html.*
import models.BuildContext
import models.Entry

fun blogPostPage(
    entry: Entry,
    prev: Entry?,
    next: Entry?,
    context: BuildContext,
): String = basePage(entry.slug, NavItem.BLOG, context) {
    articleShell(
        entry = entry,
        meta = listOfNotNull(
            "DATE" to cardDate(entry.date),
            "TAG" to entry.tag,
            "READ" to "~${readMinutes(entry.content)} min",
        ),
        backHref = context.site.sitePath(NavItem.BLOG.path),
        backLabel = "\u2190 Back to blog",
        prev = prev,
        next = next,
        context = context,
    )
}

fun projectPostPage(
    entry: Entry,
    prev: Entry?,
    next: Entry?,
    context: BuildContext,
): String = basePage(entry.slug, NavItem.PROJECTS, context) {
    val statusLabel = StatusBadge.from(entry.status)?.label
    articleShell(
        entry = entry,
        meta = buildList {
            add("DATE" to cardDate(entry.date))
            statusLabel?.let { add("STATUS" to it) }
            if (entry.tag.isNotBlank()) {
                add("TAG" to entry.tag)
            }
        },
        backHref = context.site.sitePath(NavItem.PROJECTS.path),
        backLabel = "\u2190 Back to projects",
        prev = prev,
        next = next,
        context = context,
    )
}

private fun MAIN.articleShell(
    entry: Entry,
    meta: List<Pair<String, String>>,
    backHref: String,
    backLabel: String,
    prev: Entry?,
    next: Entry?,
    context: BuildContext,
) {
    pageHead(heading = entry.title, meta = meta)

    article(classes = "post-shell") {
        div(classes = "post-body prose") {
            unsafe { +entry.content }
        }
        div(classes = "post-nav") {
            if (prev == null && next == null) {
                a(href = backHref) { +backLabel }
            } else {
                navLink(prev?.let { context.site.sitePath(it.url) }, "\u2190 Prev")
                navLink(next?.let { context.site.sitePath(it.url) }, "Next \u2192")
            }
        }
    }
}

private fun FlowContent.navLink(href: String?, label: String) {
    if (href != null) {
        a(href = href) { +label }
    } else {
        span { +label }
    }
}

private fun readMinutes(contentHtml: String): Int {
    val words = contentHtml.split(Regex("\\s+")).count { it.isNotBlank() }
    return maxOf(1, (words + 199) / 200)
}
