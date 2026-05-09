package templates

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import models.Entry
import models.SiteConfig
import java.time.format.DateTimeFormatter

private val DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")

/**
 * Static page generators — each returns a complete HTML document string.
 */

/**
 * Blog index page — lists all blog posts.
 */
fun indexPage(posts: List<Entry>, year: Int): String = basePage("Blog${SiteConfig.TITLE_SUFFIX}", year, "/static/css/blog.css") {
            if (posts.isEmpty()) {
                article(classes = "blog-entry") {
                    h2 { +"No posts yet" }
                    p {
                        +"Add markdown files to "
                        code { +"content/" }
                        +" to publish posts."
                    }
                }
            } else {
                posts.forEachIndexed { index, post ->
                    article(classes = "blog-entry") {
                        h2 { a(href = post.url) { +post.title } }
                        if (post.date != null) {
                            time(classes = "bitcount") {
                                attributes["datetime"] = post.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                +"[${post.date.format(DISPLAY_FORMAT)}]"
                            }
                        }
                        if (post.subtitle.isNotBlank()) {
                            p { +post.subtitle }
                        }
                    }
                    if (index < posts.lastIndex) {
                        hr {}
                    }
                }
            }
}

/**
 * Projects listing page — lists all projects.
 */
fun projectsPage(projects: List<Entry>, year: Int): String = basePage("Projects${SiteConfig.TITLE_SUFFIX}", year, "/static/css/projects.css") {
            if (projects.isEmpty()) {
                article(classes = "project-entry") {
                    h2 { +"No projects yet" }
                    p {
                        +"Add markdown files to "
                        code { +"content/projects/" }
                        +" to publish projects."
                    }
                }
            } else {
                projects.forEachIndexed { index, project ->
                    article(classes = "project-entry") {
                        h2 { a(href = project.url) { +project.title } }
                        if (project.status.isNotBlank()) {
                            p(classes = "status") { +"[${project.status}]" }
                        }
                        p { +project.subtitle }
                    }
                    if (index < projects.lastIndex) {
                        hr {}
                    }
                }
            }
}

/**
 * About page — static content.
 */
fun aboutPage(year: Int): String = basePage("About Me${SiteConfig.TITLE_SUFFIX}", year, "/static/css/about.css") {
            section {
                h2 { +"About Me" }
                p {
                    +"Hi, I'm Eragoneq and that's my amazing hand-crafted website. "
                    +"I'm a general software enthusiast with a passion for cybersecurity. Fellow "
                    span(classes = "lod") {
                        title = "Arch btw"
                        +"LinuxOnDesktop"
                    }
                    +" user, Minecrafter by trait and tea enjoyer."
                }
                hr {}
                p {
                    +"This website is a personal project I'm working on and is part of a larger "
                    +"effort to build a collection of my work and skills online."
                }
                p {
                    +"It houses mainly the organized and curated content, while I plan to also host "
                    +"more information in my "
                    a(href = "https://garden.eragoneq.fyi") { +"Digital Garden" }
                    +". More information on the actual website and in a separate blog post."
                }
                hr {}
                p { +"To-dos for the near future:" }
                ul {
                    li { +"Actual content 💀" }
                    li { +"RSS feed ✅" }
                    li { +"GIF banner (88x31) and web-ring section" }
                }
            }
            hr {}
            section(classes = "filler") {}
            hr {}
            section {
                h2 { +"Socials" }
                ul {
                    li { +"E-Mail: eragoneq [at] pm.me" }
                    li {
                        +"Github: "
                        a(href = "https://github.com/Eragoneq") { +"Eragoneq" }
                    }
                    li {
                        +"Twitter: "
                        a(href = "https://twitter.com/eragoneq") { +"@eragoneq" }
                    }
                    li {
                        +"Mastodon: "
                        a(href = "https://infosec.exchange/@eragoneq") { +"@eragoneq@infosec.exchange" }
                    }
                }
            }
}
