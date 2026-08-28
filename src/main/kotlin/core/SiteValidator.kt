package core

import models.SiteConfig
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import java.nio.file.Path
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText

data class SiteValidationReport(
    val checkedHtmlFiles: Int,
    val checkedLinks: Int,
    val checkedXmlFiles: Int,
    val errors: List<String>,
) {
    val isValid: Boolean get() = errors.isEmpty()
}

class SiteValidationException(report: SiteValidationReport) : IllegalStateException(
    buildString {
        append("Generated site validation failed:")
        report.errors.forEach { error -> append("\n - $error") }
    },
)

private val HTML_LINK_REGEX = Regex(
    """(?:href|src)\s*=\s*["']([^"']+)["']""",
    RegexOption.IGNORE_CASE,
)
private val URI_SCHEME_REGEX = Regex("^[A-Za-z][A-Za-z0-9+.-]*:")

fun validateSite(siteDir: Path, siteConfig: SiteConfig): SiteValidationReport {
    val errors = mutableListOf<String>()
    if (!siteDir.exists() || !siteDir.isDirectory()) {
        return SiteValidationReport(0, 0, 0, listOf("Site directory does not exist: $siteDir"))
    }

    val normalizedSiteDir = siteDir.toAbsolutePath().normalize()
    val htmlFiles = normalizedSiteDir.toFile().walkTopDown()
        .filter { it.isFile && it.extension.equals("html", ignoreCase = true) }
        .map { it.toPath() }
        .sortedBy { normalizedSiteDir.relativize(it).toString() }
        .toList()

    var checkedLinks = 0
    htmlFiles.forEach { htmlFile ->
        val sourceName = normalizedSiteDir.relativize(htmlFile).invariantPath()
        HTML_LINK_REGEX.findAll(htmlFile.readText(Charsets.UTF_8)).forEach { match ->
            val reference = match.groupValues[1]
            if (isExternalOrAnchorOnly(reference)) return@forEach

            checkedLinks++
            val pathOnly = reference.substringBefore('#').substringBefore('?')
            val target = resolveTarget(
                siteDir = normalizedSiteDir,
                sourceFile = htmlFile,
                urlPath = pathOnly,
                siteConfig = siteConfig,
                errors = errors,
                sourceName = sourceName,
            ) ?: return@forEach

            val resolvedTarget = when {
                target.isDirectory() -> target.resolve("index.html")
                pathOnly.endsWith('/') -> target.resolve("index.html")
                else -> target
            }
            if (!resolvedTarget.isRegularFile()) {
                errors += "$sourceName references missing target '$reference'"
            }
        }
    }

    val xmlFiles = listOf(
        normalizedSiteDir.resolve("sitemap.xml") to "urlset",
        normalizedSiteDir.resolve("feed.xml") to "rss",
    )
    var checkedXmlFiles = 0
    xmlFiles.forEach { (xmlFile, expectedRoot) ->
        if (!xmlFile.isRegularFile()) {
            errors += "Missing required XML file: ${normalizedSiteDir.relativize(xmlFile).invariantPath()}"
            return@forEach
        }
        checkedXmlFiles++
        validateXml(xmlFile, expectedRoot, errors)
    }

    val robotsPath = normalizedSiteDir.resolve("robots.txt")
    if (!robotsPath.isRegularFile()) {
        errors += "Missing required file: robots.txt"
    } else {
        val expectedSitemap = "Sitemap: ${siteConfig.absoluteUrl("sitemap.xml")}"
        if (expectedSitemap !in robotsPath.readText(Charsets.UTF_8)) {
            errors += "robots.txt does not reference ${siteConfig.absoluteUrl("sitemap.xml")}"
        }
    }

    return SiteValidationReport(
        checkedHtmlFiles = htmlFiles.size,
        checkedLinks = checkedLinks,
        checkedXmlFiles = checkedXmlFiles,
        errors = errors.sorted(),
    )
}

private fun isExternalOrAnchorOnly(reference: String): Boolean =
    reference.isBlank() ||
        reference.startsWith('#') ||
        reference.startsWith("//") ||
        URI_SCHEME_REGEX.containsMatchIn(reference)

private fun resolveTarget(
    siteDir: Path,
    sourceFile: Path,
    urlPath: String,
    siteConfig: SiteConfig,
    errors: MutableList<String>,
    sourceName: String,
): Path? {
    val target = if (urlPath.startsWith('/')) {
        if (siteConfig.basePath.isNotEmpty() &&
            urlPath != siteConfig.basePath &&
            !urlPath.startsWith("${siteConfig.basePath}/")
        ) {
            errors += "$sourceName uses root-relative URL outside base_path: '$urlPath'"
            return null
        }
        val relativeToSite = urlPath.removePrefix(siteConfig.basePath).trimStart('/')
        siteDir.resolve(relativeToSite)
    } else {
        sourceFile.parent.resolve(urlPath)
    }.normalize()

    if (!target.startsWith(siteDir)) {
        errors += "$sourceName contains a link outside the generated site: '$urlPath'"
        return null
    }
    return target
}

private fun validateXml(xmlFile: Path, expectedRoot: String, errors: MutableList<String>) {
    runCatching {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            isXIncludeAware = false
            setExpandEntityReferences(false)
            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "")
            setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "")
        }
        val builder = factory.newDocumentBuilder().apply {
            setErrorHandler(object : ErrorHandler {
                override fun warning(exception: SAXParseException) = Unit
                override fun error(exception: SAXParseException) = throw exception
                override fun fatalError(exception: SAXParseException) = throw exception
            })
        }
        val document = xmlFile.toFile().inputStream().use(builder::parse)
        val rootName = document.documentElement.localName ?: document.documentElement.nodeName
        require(rootName == expectedRoot) {
            "expected <$expectedRoot> root element but found <$rootName>"
        }
    }.onFailure { error ->
        errors += "${xmlFile.fileName} is invalid: ${error.message}"
    }
}

private fun Path.invariantPath(): String = toString().replace('\\', '/')
