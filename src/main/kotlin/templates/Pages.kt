package templates

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import models.BuildContext
import models.Entry

fun homePage(posts: List<Entry>, projects: List<Entry>, context: BuildContext): String =
    basePage("Home", NavItem.INDEX, context) {
        section(classes = "home-intro") {
            h1 { +"Hi, I'm Eragoneq." }
            p {
                +"I'm Michał, a professional and hobbyist software developer with a focus on cybersecurity and privacy. "
                +"This is my personal website, where I share my work, thoughts and projects."
            }
            nav(classes = "intro-links") {
                attributes["aria-label"] = "Explore the homepage"
                a(href = "#projects") { +"View projects →" }
                a(href = "#writing") { +"Read writing →" }
            }
        }

        homeSection("contact", "Contact") {
            ul(classes = "contact-links") {
                contactLink("E-mail", "eragoneq@pm.me", "mailto:eragoneq@pm.me")
                contactLink("GitHub", "@Eragoneq", "https://github.com/Eragoneq")
                contactLink("Twitter (X)", "@eragoneq", "https://x.com/eragoneq")
                contactLink("Mastodon", "@eragoneq@infosec.exchange", "https://infosec.exchange/@eragoneq")
            }
        }

        homeSection("about", "About me") {
            div(classes = "home-prose") {
                p {
                    +"I'm a general software enthusiast, Linux desktop user, Minecrafter and tea enjoyer. "
                    +"This site is a personal project and part of a larger effort to collect my work and skills online."
                }
                p {
                    +"I keep the organized and curated content here. I'm also working on a "
                    a(href = "https://garden.eragoneq.fyi") { +"Digital Garden" }
                    +" for more informal notes and information."
                }
            }
        }

        homeSection("projects", "Projects") {
            if (projects.isEmpty()) {
                emptyEntriesNote()
            } else {
                ul(classes = "card-list") {
                    projects.take(context.site.homeProjectLimit).forEach { project ->
                        projectCardRow(
                            href = context.site.sitePath(project.url),
                            date = cardDate(project.date),
                            titleText = project.title,
                            desc = project.subtitle,
                            status = StatusBadge.from(project.status),
                        )
                    }
                }
            }
            p(classes = "section-more") {
                a(href = context.site.sitePath(NavItem.PROJECTS.path)) { +"All projects →" }
            }
        }

        homeSection("writing", "Writing") {
            if (posts.isEmpty()) {
                emptyEntriesNote()
            } else {
                ul(classes = "card-list") {
                    posts.take(context.site.homePostLimit).forEach { post ->
                        cardRow(
                            href = context.site.sitePath(post.url),
                            date = cardDate(post.date),
                            titleText = post.title,
                            tag = post.tag,
                            desc = post.subtitle,
                        )
                    }
                }
            }
            p(classes = "section-more") {
                a(href = context.site.sitePath(NavItem.BLOG.path)) { +"All posts →" }
            }
        }
    }

fun blogPage(posts: List<Entry>, context: BuildContext): String =
    basePage("Blog", NavItem.BLOG, context) {
        pageHead(
            heading = "Blog posts",
            lede = "My notes on projects, code and all the random stuff in between.",
            listing = true,
        )
        if (posts.isEmpty()) {
            emptyEntriesNote()
        } else {
            ul(classes = "card-list") {
                posts.forEach { post ->
                    cardRow(
                        href = context.site.sitePath(post.url),
                        date = cardDate(post.date),
                        titleText = post.title,
                        tag = post.tag,
                        desc = post.subtitle,
                    )
                }
            }
        }
    }

fun projectsPage(projects: List<Entry>, context: BuildContext): String =
    basePage("Projects", NavItem.PROJECTS, context) {
        pageHead(heading = "Projects", listing = true)
        if (projects.isEmpty()) {
            emptyEntriesNote()
        } else {
            ul(classes = "card-list") {
                projects.forEach { project ->
                    projectCardRow(
                        href = context.site.sitePath(project.url),
                        date = cardDate(project.date),
                        titleText = project.title,
                        desc = project.subtitle,
                        status = StatusBadge.from(project.status),
                    )
                }
            }
        }
    }

/** Keep existing bookmarks working after consolidating the personal pages. */
fun homeSectionRedirect(section: String, context: BuildContext): String {
    val destination = context.site.sitePath("index.html") + "#$section"
    return "<!DOCTYPE html>\n" + createHTML().html {
        attributes["lang"] = "en"
        head {
            meta(charset = "UTF-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
            meta { attributes["http-equiv"] = "refresh"; content = "0;url=$destination" }
            meta(name = "robots", content = "noindex")
            link(rel = "canonical", href = context.site.absoluteUrl("index.html"))
            title("Moved to the homepage")
        }
        body {
            p { a(href = destination) { +"Continue to the homepage" } }
        }
    }
}

private fun MAIN.homeSection(sectionId: String, heading: String, content: DIV.() -> Unit) {
    section(classes = "home-section") {
        id = sectionId
        attributes["aria-labelledby"] = "$sectionId-heading"
        h2 { id = "$sectionId-heading"; +heading }
        div(classes = "home-section-content") { content() }
    }
}

private fun FlowContent.emptyEntriesNote() {
    p(classes = "empty-note") { +"Nothing published here yet. Check back soon." }
}

private fun UL.contactLink(label: String, handle: String, href: String) {
    li {
        span(classes = "label") { +label }
        a(href = href, classes = "handle") {
            val domainSeparator = handle.indexOf('@', startIndex = 1)
            if (domainSeparator > 0) {
                attributes["aria-label"] = handle
                +handle.substring(0, domainSeparator)
                unsafe { +"<wbr>" }
                +handle.substring(domainSeparator)
            } else {
                +handle
            }
        }
    }
}
