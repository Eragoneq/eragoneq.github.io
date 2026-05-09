package templates

import kotlinx.html.*
import models.SiteConfig.SITE_NAME

import kotlinx.html.stream.createHTML

/**
 * Base layout wrapper to reduce boilerplate.
 */
fun basePage(
    pageTitle: String,
    year: Int,
    extraCss: String? = null,
    content: MAIN.() -> Unit
): String = createHTML().html {
    attributes["lang"] = "en"
    head {
        headFragment(pageTitle, extraCss)
    }
    body {
        headerFragment()
        main {
            content()
        }
        footerFragment(year)
    }
}

/**
 * Emits the contents of `<head>` — meta tags, fonts, global stylesheet.
 *
 * @param pageTitle  full `<title>` text (e.g. "Blog ⬡ Eragoneq")
 * @param extraCss   optional page-specific stylesheet path (e.g. "/static/css/blog.css")
 */
fun HEAD.headFragment(pageTitle: String, extraCss: String? = null) {
    meta(charset = "UTF-8")
    link(rel = "icon", type = "image/x-icon", href = "/static/img/logo.svg")
    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
    link(rel = "preconnect", href = "https://fonts.googleapis.com")
    link(rel = "preconnect", href = "https://fonts.gstatic.com") {
        attributes["crossorigin"] = ""
    }
    link(
        rel = "stylesheet",
        href = "https://fonts.googleapis.com/css2?family=Bitcount+Grid+Double:wght,CRSV,ELSH,ELXP@100..900,0..1,0..100,0..100&family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&display=swap"
    )
    link(rel = "stylesheet", href = "/static/css/style.css")
    link(rel = "stylesheet", href = "/static/css/footer.css")
    extraCss?.let {
        link(rel = "stylesheet", href = it)
    }
    title(pageTitle)
}

/**
 * Emits the site `<header>` with the site name and navigation links.
 */
fun BODY.headerFragment() {
    header {
        h1(classes = "bitcount") { +SITE_NAME }
        nav {
            a(classes = "bitcount", href = "/about.html") { +"About" }
            a(classes = "bitcount", href = "/index.html") { +"Blog" }
            a(classes = "bitcount", href = "/projects.html") { +"Projects" }
        }
    }
}

/**
 * Emits the site `<footer>` with branding, social links, and copyright.
 *
 * @param year  current year for the copyright notice
 */
fun BODY.footerFragment(year: Int) {
    footer {
        div(classes = "micro-branding") {
            img(src = "/static/img/logo.svg", alt = "Eragoneq's Logo")
            p(classes = "underline") { +"Eragoneq" }
            p { +"© $year" }
        }
        p { +"Made with \u2665" }
        p(classes = "socials") {
            a(href = "https://github.com/Eragoneq", target = "_blank") {
                img(
                    src = "https://upload.wikimedia.org/wikipedia/commons/9/91/Octicons-mark-github.svg",
                    alt = "GitHub",
                )
            }
            a(href = "https://ko-fi.com/U7U7UOZ1B") {
                img(
                    src = "https://storage.ko-fi.com/cdn/brandasset/v2/kofi_symbol.png",
                    alt = "Buy Me a Coffee at ko-fi.com",
                )
            }
        }
    }
}
