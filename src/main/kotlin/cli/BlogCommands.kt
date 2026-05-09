package cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import core.SiteBuilder
import java.nio.file.Path

/**
 * Root CLI command group.
 */
class BlogCommands : CliktCommand(name = "blog") {
    override fun run() = Unit
}

/**
 * `generate` subcommand — builds the entire static site.
 *
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

    override fun run() {
        echo("Building site...")
        echo("  Content:  $inputDir")
        echo("  Output:   $outputDir")
        echo("  Static:   $staticDir")

        val builder = SiteBuilder(
            contentDir = inputDir,
            outputDir = outputDir,
            staticSrcDir = staticDir,
        )

        val startTime = System.currentTimeMillis()
        val (postCount, projectCount) = builder.build()
        val duration = (System.currentTimeMillis() - startTime) / 1000.0

        echo("Done! Built $postCount posts and $projectCount projects in $duration seconds into $outputDir")
    }
}