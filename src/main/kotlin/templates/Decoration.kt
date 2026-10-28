package templates

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import models.BuildContext
import models.SiteConfig
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class NavItem(val label: String, val path: String) {
    INDEX("Home", "/index.html"),
    BLOG("Blog", "/blog.html"),
    PROJECTS("Projects", "/projects.html"),
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
        a(href = "#main-content", classes = "skip-link") { +"Skip to content" }
        headerFragment(activeNav, context.site)
        main {
            id = "main-content"
            attributes["tabindex"] = "-1"
            content()
        }
        footerFragment(context)
        script(src = context.site.sitePath("static/js/script.js")) {}
    }
}

fun HEAD.headFragment(pageTitle: String, site: SiteConfig) {
    meta(charset = "UTF-8")
    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
    link(rel = "icon", type = "image/svg+xml", href = site.sitePath("favicon.svg"))
    script {
        unsafe {
            +"""try{var theme=localStorage.getItem('site-theme');if(['soft','warm','original','light'].includes(theme))document.documentElement.dataset.theme=theme}catch(e){}"""
        }
    }
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
        div(classes = "theme-control") {
            attributes["hidden"] = "hidden"
            button(type = ButtonType.button, classes = "theme-trigger") {
                attributes["aria-label"] = "Appearance"
                attributes["aria-expanded"] = "false"
                attributes["aria-controls"] = "theme-menu"
                unsafe {
                    +"""<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" aria-hidden="true" focusable="false"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M2 12h2M20 12h2M4.93 4.93l1.42 1.42M17.65 17.65l1.42 1.42M19.07 4.93l-1.42 1.42M6.35 17.65l-1.42 1.42"/></svg>"""
                }
            }
            div(classes = "theme-menu") {
                id = "theme-menu"
                attributes["hidden"] = "hidden"
                attributes["role"] = "group"
                attributes["aria-label"] = "Choose appearance"
                listOf("soft" to "Soft dark", "warm" to "Warm dark", "original" to "Original dark", "light" to "Light").forEach { (theme, label) ->
                    button(type = ButtonType.button, classes = "theme-option") {
                        attributes["data-theme-option"] = theme
                        attributes["aria-pressed"] = "false"
                        +label
                    }
                }
            }
        }
    }
}

fun BODY.footerFragment(context: BuildContext) {
    footer(classes = "site-footer") {
        span(classes = "footer-mark") {
            img(src = context.site.sitePath("static/img/logo.svg"), alt = "E://")
            span { +"Eragoneq" }
            span { +"© ${context.year}" }
        }
        span(classes = "mg-dither footer-dither") {
            attributes["aria-hidden"] = "true"
            +"░▒▓█▓▒░"
        }
        div(classes = "footer-meta") {
            a(href = context.site.sitePath("feed.xml")) { +"RSS feed" }
            span(classes = "footer-build") {
                +"Last rebuilt "
                time {
                    dateTime = context.generatedAt.toString()
                    +DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm 'UTC'")
                        .withZone(ZoneOffset.UTC).format(context.generatedAt)
                }
            }
        }
    }
}

fun MAIN.pageHead(
    heading: String,
    lede: String? = null,
    meta: List<Pair<String, String>>? = null,
    listing: Boolean = false,
) {
    section(classes = if (listing) "page-head page-head--listing" else "page-head") {
        h1 {
            +heading
        }
        lede?.let { text ->
            p(classes = "lede") { +text }
        }
        val visibleMeta = meta.orEmpty().filter { it.second.isNotBlank() }
        if (visibleMeta.isNotEmpty()) {
            div(classes = "meta-row") {
                visibleMeta.forEach { (label, value) ->
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
    tag: String = "",
    desc: String
) {
    li(classes = "card") {
        a(href = href) {
            if (date.isNotBlank()) span(classes = "card-date") { +date }
            span(classes = "card-title") { +titleText }
            if (tag.isNotBlank()) span(classes = "card-tag") { +tag }
            if (desc.isNotBlank()) span(classes = "card-desc") { +desc }
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
            if (date.isNotBlank()) span(classes = "card-date") { +date }
            span(classes = "card-title") { +titleText }
            if (status != null) span(classes = "card-tag") {
                i(classes = "status ${status.cssClass}") { +status.label }
            }
            if (desc.isNotBlank()) span(classes = "card-desc") { +desc }
        }
    }
}

internal fun cardDate(date: LocalDate?, fallback: String = ""): String = if (date != null) "%tF".format(date) else fallback
