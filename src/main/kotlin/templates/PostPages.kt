package templates

import kotlinx.html.*
import models.BuildContext
import models.Entry

fun blogPostPage(
    entry: Entry,
    transmissionNo: Int,
    prev: Entry?,
    next: Entry?,
    context: BuildContext,
): String = basePage(entry.slug, NavItem.BLOG, context) {
    articleShell(
        entry = entry,
        kicker = "/blog — post %03d".format(transmissionNo),
        meta = listOfNotNull(
            "DATE" to cardDate(entry.date),
            "TAG" to entry.tag,
            "READ" to "~${readMinutes(entry.content)} min",
        ),
        deco = {
            span(classes = "mg-frame deco") {
                attributes["aria-hidden"] = "true"
                i()
            }
        },
        backHref = context.site.sitePath(NavItem.BLOG.path),
        backLabel = "\u2190 Back to blog",
        prev = prev,
        next = next,
        context = context,
    )
}

fun projectPostPage(
    entry: Entry,
    artifactNo: Int,
    prev: Entry?,
    next: Entry?,
    context: BuildContext,
): String = basePage(entry.slug, NavItem.PROJECTS, context) {
    val statusLabel = StatusBadge.from(entry.status)?.label ?: "\u2014"
    articleShell(
        entry = entry,
        kicker = "/projects — artifact %02d".format(artifactNo),
        meta = buildList {
            add("DATE" to cardDate(entry.date))
            add("STATUS" to statusLabel)
            if (entry.tag.isNotBlank()) {
                add("TAG" to entry.tag)
            }
        },
        deco = {
            span(classes = "mg-scan deco") {
                attributes["aria-hidden"] = "true"
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
    kicker: String,
    meta: List<Pair<String, String>>,
    deco: SECTION.() -> Unit,
    backHref: String,
    backLabel: String,
    prev: Entry?,
    next: Entry?,
    context: BuildContext,
) {
    pageHead(kicker = kicker, heading = entry.title, meta = meta, deco = deco)

    article(classes = "post-shell") {
        div(classes = "post-body prose") {
            unsafe { +entry.content }
        }
        p(classes = "post-end") { +"End of file" }
        div(classes = "post-nav") {
            if (prev == null && next == null) {
                a(href = backHref) { +backLabel }
            } else {
                navLink(prev?.let { context.site.sitePath(it.url) }, "\u2190 Prev")
                span(classes = "mg-dotline") {}
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
