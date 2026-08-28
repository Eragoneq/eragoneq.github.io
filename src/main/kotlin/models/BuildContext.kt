package models

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

data class BuildContext(
    val site: SiteConfig,
    val generatedAt: Instant,
) {
    val buildDate: LocalDate = generatedAt.atZone(ZoneOffset.UTC).toLocalDate()
    val year: Int = buildDate.year

    companion object {
        fun from(site: SiteConfig, clock: Clock): BuildContext =
            BuildContext(site, clock.instant())
    }
}
