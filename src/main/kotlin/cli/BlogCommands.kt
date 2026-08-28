package cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import core.SiteBuilder
import core.SiteValidationException
import core.loadSiteConfig
import core.validateSite
import java.nio.file.Path

class BlogCommands : CliktCommand(name = "blog") {
    override fun run() = Unit
}

/**
 * Usage:
 *   blog generate
 *   blog generate -i ./content -o ./public -s ./static
 */
class GenerateCommand : CliktCommand(name = "generate") {

    private val inputDir: Path by option("-i", "--input", help = "Path to the content directory")
        .path(mustExist = false)
        .default(Path.of("./content"))

    private val outputDir: Path by option("-o", "--output", help = "Path to the output directory")
        .path(mustExist = false)
        .default(Path.of("./public"))

    private val staticDir: Path by option("-s", "--static", help = "Path to the static assets directory")
        .path(mustExist = false)
        .default(Path.of("./static"))

    private val configFile: Path by option("-c", "--config", help = "Path to the site configuration")
        .path(mustExist = true)
        .default(Path.of("./site.yml"))

    private val strict: Boolean by option(
        "--strict",
        help = "Fail the build when any content file is malformed",
    ).flag(default = false)

    override fun run() {
        echo("Building site...")
        echo("  Content:  $inputDir")
        echo("  Output:   $outputDir")
        echo("  Static:   $staticDir")
        echo("  Config:   $configFile")
        echo("  Strict:   $strict")

        val builder = SiteBuilder(
            contentDir = inputDir,
            outputDir = outputDir,
            staticSrcDir = staticDir,
            siteConfig = loadSiteConfig(configFile),
            strict = strict,
        )

        val startTime = System.currentTimeMillis()
        val (postCount, projectCount) = builder.build()
        val duration = (System.currentTimeMillis() - startTime) / 1000.0

        echo("Done! Built $postCount posts and $projectCount projects in $duration seconds into $outputDir")
    }
}

class ValidateCommand : CliktCommand(name = "validate") {
    private val siteDir: Path by option("--site", help = "Path to the generated site")
        .path(mustExist = false)
        .default(Path.of("./public"))

    private val configFile: Path by option("-c", "--config", help = "Path to the site configuration")
        .path(mustExist = true)
        .default(Path.of("./site.yml"))

    override fun run() {
        val report = validateSite(siteDir, loadSiteConfig(configFile))
        if (!report.isValid) throw SiteValidationException(report)

        echo(
            "Validated ${report.checkedHtmlFiles} HTML files, " +
                "${report.checkedLinks} internal links, and ${report.checkedXmlFiles} XML files.",
        )
    }
}
