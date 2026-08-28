package templates

import kotlinx.html.*
import models.BuildContext
import models.Entry

fun homePage(posts: List<Entry>, projects: List<Entry>, context: BuildContext): String =
    basePage("index", NavItem.INDEX, context) {
        section(classes = "hero") {
            p(classes = "kicker") { +"Personal site - est. ${context.year}" }
            h1 {
                +"Hi, I'm Eragoneq."
                span(classes = "cursor")
            }
            p(classes = "lede") {
                +"Professional and hobbyist software developer, with a focus on cybersecurity and privacy. "
                +"This is my personal website, where I share my work, thoughts and projects."
            }
            div(classes = "meta-row") {
                span {
                    +"SYS "
                    b { +"ONLINE" }
                }
                span {
                    +"MODE "
                    b { +"DARK" }
                }
                span {
                    +"LAST UPDATE "
                    b { +cardDate(context.buildDate) }
                }
            }
            span(classes = "mg-ditherfade") {
                attributes["aria-hidden"] = "true"
            }
        }

        h2(classes = "section-label") { +"Latest entries" }
        if (posts.isEmpty()) {
            emptyEntriesNote()
        } else {
            ul(classes = "card-list") {
                posts.take(context.site.homePostLimit).forEach { post ->
                    cardRow(
                        href = context.site.sitePath(post.url),
                        date = cardDate(post.date),
                        titleText = post.title,
                        desc = post.subtitle,
                    )
                }
            }
        }

        h2(classes = "section-label") { +"Selected work" }
        if (projects.isEmpty()) {
            emptyEntriesNote()
        } else {
            ul(classes = "card-list") {
                projects.take(context.site.homeProjectLimit).forEachIndexed { index, project ->
                    projectCardRow(
                        href = context.site.sitePath(project.url),
                        date = artifactStamp(index + 1, project.date?.year ?: context.year),
                        titleText = project.title,
                        desc = project.subtitle,
                        status = StatusBadge.from(project.status),
                    )
                }
            }
        }

        div {
            style = "margin-top: 48px"
            a(classes = "btn", href = context.site.sitePath(NavItem.ABOUT.path)) { +"About me →" }
        }
    }

fun blogPage(posts: List<Entry>, context: BuildContext): String =
    basePage("blog", NavItem.BLOG, context) {
        pageHead(
            kicker = "/blog - written content",
            heading = "Blog Posts",
            lede = "My notes on projects, code and all the random stuff in between.",
            deco = {
                span(classes = "mg-frame deco") {
                    attributes["aria-hidden"] = "true"
                    i()
                }
            },
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
                        desc = post.subtitle,
                    )
                }
            }
        }
    }

fun projectsPage(projects: List<Entry>, context: BuildContext): String =
    basePage("projects", NavItem.PROJECTS, context) {
        val activeCount = projects.count { StatusBadge.from(it.status) == StatusBadge.ACTIVE }
        val doneCount = projects.count { StatusBadge.from(it.status) == StatusBadge.DONE }
        pageHead(
            kicker = "/projects - built output",
            heading = "Artifacts",
            meta = listOf(
                "TOTAL" to "%02d".format(projects.size),
                "ACTIVE" to "%02d".format(activeCount),
                "DONE" to "%02d".format(doneCount),
            ),
            deco = {
                span(classes = "mg-scan deco") {
                    attributes["aria-hidden"] = "true"
                }
            },
        )

        if (projects.isEmpty()) {
            emptyEntriesNote()
        } else {
            ul(classes = "card-list") {
                projects.forEachIndexed { index, project ->
                    projectCardRow(
                        href = context.site.sitePath(project.url),
                        date = artifactStamp(index + 1, project.date?.year ?: context.year),
                        titleText = project.title,
                        desc = project.subtitle,
                        status = StatusBadge.from(project.status),
                    )
                }
            }
        }
    }

fun aboutPage(context: BuildContext): String =
    basePage("about", NavItem.ABOUT, context) {
        pageHead(
            kicker = "/about - profile",
            heading = "Who is running this",
            deco = {
                span(classes = "mg-frame deco") {
                    attributes["aria-hidden"] = "true"
                    i()
                }
            },
        )

        div(classes = "about-grid") {
            div {
                p {
                    +"Hi, I'm Michał, known on the Internet as Eragoneq and that's my amazing personal website. "
                    +"I'm a general software enthusiast with a passion for cybersecurity. Fellow "
                    span(classes = "lod") {
                        attributes["title"] = "Arch btw"
                        +"LinuxOnDesktop"
                    }
                    +" user, Minecrafter by trait and tea enjoyer."
                }
                p {
                    +"This website is a personal project I'm working on and is part of a larger "
                    +"effort to build a collection of my work and skills online."
                }
                p {
                    +"It houses mainly the organized and curated content, while I plan to also host "
                    +"more information in my "
                    a(href = "https://garden.eragoneq.fyi", classes = "lod") { +"Digital Garden" }
                    +". More information on the actual website and in a separate blog post."
                }
            }

            ul(classes = "facts") {
                fact("Alias", "Eragoneq")
                fact("Desktop", "Linux")
                fact("Focus", "Cybersecurity, Privacy")
                fact("Main game", "Minecraft")
                fact("Fuel", "Tea")
                fact("Other projects", "garden.eragoneq.fyi")
                li(classes = "deco-row") {
                    attributes["aria-hidden"] = "true"
                    span(classes = "mg-barcode") {}
                }
            }
        }

        h2(classes = "section-label") { +"Current to-dos" }
        ul(classes = "card-list") {
            listOf(
                "Fill in more content",
                "RSS feed ✅",
                "GIF banner (88x31) and web-ring section",
            ).forEachIndexed { index, todo ->
                cardRow(
                    href = "#",
                    date = "%02d".format(index + 1),
                    titleText = todo,
                    tag = "-*-",
                    desc = "",
                )
            }
        }
    }

fun contactPage(context: BuildContext): String =
    basePage("contact", NavItem.CONTACT, context) {
        pageHead(
            kicker = "/contact - open channel",
            heading = "Get in touch",
            lede = "Collaboration, questions or just a way to say hi.",
            deco = {
                span(classes = "mg-signal deco") {
                    attributes["aria-hidden"] = "true"
                    i { attributes["style"] = "height:25%" }
                    i { attributes["style"] = "height:50%" }
                    i { attributes["style"] = "height:75%" }
                    i(classes = "off") { attributes["style"] = "height:100%" }
                }
            },
        )

        h2(classes = "section-label") { +"Channels" }
        ul(classes = "contact-links") {
            contactLink(
                label = "E-Mail",
                handle = "eragoneq [at] pm.me",
                href = "mailto:eragoneq@pm.me",
            )
            contactLink(
                label = "GitHub",
                handle = "@Eragoneq",
                href = "https://github.com/Eragoneq",
            )
            contactLink(
                label = "Twitter (X)",
                handle = "@eragoneq",
                href = "https://x.com/eragoneq",
            )
            contactLink(
                label = "Mastodon",
                handle = "@eragoneq@infosec.exchange",
                href = "https://infosec.exchange/@eragoneq",
            )
        }
    }

private fun MAIN.emptyEntriesNote() {
    p(classes = "lede") {
        +"No entries yet. Add markdown files to "
        code { +"content/" }
        +" to publish."
    }
}

private fun artifactStamp(number: Int, entryYear: Int): String =
    "P-%02d / %04d".format(number, entryYear)

private fun UL.fact(key: String, value: String) {
    li {
        span(classes = "k") { +key }
        +value
    }
}

private fun UL.contactLink(label: String, handle: String, href: String) {
    li {
        a(href = href) {
            span(classes = "label") { +label }
            span(classes = "handle") { +handle }
        }
    }
}
