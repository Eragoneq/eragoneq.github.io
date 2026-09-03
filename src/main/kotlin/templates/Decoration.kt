package templates

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import models.BuildContext
import models.SiteConfig
import java.time.LocalDate

enum class NavItem(val label: String, val path: String) {
    INDEX("Index", "/index.html"),
    BLOG("Blog", "/blog.html"),
    PROJECTS("Projects", "/projects.html"),
    ABOUT("About", "/about.html"),
    CONTACT("Contact", "/contact.html"),
}

enum class StatusBadge(val cssClass: String, val label: String) {
    ACTIVE("active", "Active"),
    WIP("wip", "WIP"),
    DONE("done", "Done");

    companion object {
        fun from(raw: String): StatusBadge? = when (raw.trim().lowercase()) {
            "active" -> ACTIVE
            "wip", "in progress", "in-progress" -> WIP
            "done", "complete", "completed", "archived" -> DONE
            else -> null
        }
    }
}

fun basePage(
    pageName: String,
    activeNav: NavItem?,
    context: BuildContext,
    content: MAIN.() -> Unit
): String = "<!DOCTYPE html>\n" + createHTML().html {
    attributes["lang"] = "en"
    head {
        headFragment("${context.site.titlePrefix}$pageName", context.site)
    }
    body {
        headerFragment(activeNav, context.site)
        main {
            content()
        }
        footerFragment(context.year, context.site)
        script(src = context.site.sitePath("static/js/script.js")) {}
    }
}

fun HEAD.headFragment(pageTitle: String, site: SiteConfig) {
    meta(charset = "UTF-8")
    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
    link(rel = "icon", type = "image/svg+xml", href = site.sitePath("favicon.svg"))
    link(rel = "preconnect", href = "https://fonts.googleapis.com")
    link(rel = "preconnect", href = "https://fonts.gstatic.com") {
        attributes["crossorigin"] = ""
    }
    link(
        rel = "stylesheet",
        href = "https://fonts.googleapis.com/css2?family=Doto:wght@100..900&family=Space+Mono:ital,wght@0,400;0,700;1,400;1,700&display=swap"
    )
    link(rel = "stylesheet", href = site.sitePath("static/css/main.css"))
    title(pageTitle)
}

fun BODY.headerFragment(activeNav: NavItem?, site: SiteConfig) {
    header(classes = "site-header") {
        a(classes = "brand", href = site.sitePath(NavItem.INDEX.path)) {
            attributes["aria-label"] = "E:// - home"
            img(classes = "brand-mark", src = site.sitePath("static/img/logo.svg"), alt = "E://") {
                attributes["width"] = "43"
                attributes["height"] = "17"
            }
        }
        nav(classes = "site-nav") {
            NavItem.entries.forEach { item ->
                a(href = site.sitePath(item.path)) {
                    if (item == activeNav) {
                        attributes["aria-current"] = "page"
                    }
                    +item.label
                }
            }
        }
    }
}

fun BODY.footerFragment(year: Int, site: SiteConfig) {
    footer(classes = "site-footer") {
        span(classes = "footer-mark") {
            img(src = site.sitePath("static/img/logo.svg"), alt = "E://")
            p { +"Eragoneq" }
            p { +"\u00A9 $year" }
        }
        span(classes = "mg-dither") { +"░▒▓█▓▒░" }
        span {
            +"LOCAL TIME "
            b(classes = "clock") { +"--:--:--" }
        }
    }
}

fun MAIN.pageHead(
    kicker: String,
    heading: String,
    lede: String? = null,
    meta: List<Pair<String, String>>? = null,
    deco: SECTION.() -> Unit = {}
) {
    section(classes = "page-head") {
        deco()
        p(classes = "kicker") { +kicker }
        h1 {
            +heading
            span(classes = "cursor")
        }
        lede?.let { text ->
            p(classes = "lede") { +text }
        }
        meta?.let { entries ->
            div(classes = "meta-row") {
                entries.forEach { (label, value) ->
                    span {
                        +"$label "
                        b { +value }
                    }
                }
            }
        }
    }
}

fun UL.cardRow(
    href: String,
    date: String,
    titleText: String,
    tag: String = "Note",
    desc: String
) {
    li(classes = "card") {
        a(href = href) {
            span(classes = "card-date") { +date }
            span(classes = "card-title") { +titleText }
            span(classes = "card-tag") { +tag }
            span(classes = "card-desc") { +desc }
        }
    }
}

fun UL.projectCardRow(
    href: String,
    date: String,
    titleText: String,
    desc: String,
    status: StatusBadge?
) {
    li(classes = "card") {
        a(href = href) {
            span(classes = "card-date") { +date }
            span(classes = "card-title") { +titleText }
            span(classes = "card-tag") {
                if (status != null) {
                    i(classes = "status ${status.cssClass}") { +status.label }
                } else {
                    +"-*-"
                }
            }
            span(classes = "card-desc") { +desc }
        }
    }
}

internal fun cardDate(date: LocalDate?, fallback: String = "-*-"): String = if (date != null) "%tF".format(date) else fallback
