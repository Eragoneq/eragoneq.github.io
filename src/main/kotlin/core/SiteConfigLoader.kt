package core

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import models.SiteConfig
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

private val siteConfigYaml = Yaml(configuration = YamlConfiguration(strictMode = true))

fun loadSiteConfig(path: Path): SiteConfig {
    require(path.exists()) { "Site configuration not found: $path" }

    return runCatching {
        siteConfigYaml.decodeFromString<SiteConfig>(path.readText(Charsets.UTF_8)).validate()
    }.getOrElse { error ->
        throw IllegalArgumentException(
            "Unable to load site configuration from $path: ${error.message}",
            error,
        )
    }
}
