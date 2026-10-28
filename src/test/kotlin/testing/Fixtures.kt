package testing

import models.BuildContext
import models.Entry
import models.EntryKind
import models.FrontMatter
import models.SiteConfig
import java.nio.file.Path
import java.time.Instant
import java.time.LocalDate

fun testSiteConfig(basePath: String = ""): SiteConfig = SiteConfig(
    siteName = "Example Site",
    titlePrefix = "Example // ",
    baseUrl = "https://example.test",
    basePath = basePath,
    description = "Example description",
    homePostLimit = 3,
    homeProjectLimit = 3,
).validate()

fun testBuildContext(
    basePath: String = "",
    generatedAt: Instant = Instant.parse("2024-02-29T23:59:58Z"),
): BuildContext = BuildContext(testSiteConfig(basePath), generatedAt)

fun testEntry(
    url: String,
    title: String = url,
    date: LocalDate? = LocalDate.parse("2024-01-01"),
    kind: EntryKind = EntryKind.POST,
    subtitle: String = "Subtitle",
): Entry = Entry(
    kind = kind,
    sourcePath = Path.of(url.substringAfterLast('/').substringBeforeLast('.') + ".md"),
    outputRelPath = Path.of(url),
    url = url,
    slug = url.substringAfterLast('/').substringBeforeLast('.'),
    title = title,
    subtitle = subtitle,
    date = date,
    lastUpdate = date,
    frontmatter = FrontMatter(title = title, date = date?.toString()),
    content = "<p>Rendered content</p>",
)
