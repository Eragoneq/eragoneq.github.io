package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class SiteConfig(
    @SerialName("site_name") val siteName: String,
    @SerialName("title_prefix") val titlePrefix: String,
    @SerialName("base_url") val baseUrl: String,
    @SerialName("base_path") val basePath: String = "",
    val description: String,
    @SerialName("home_post_limit") val homePostLimit: Int = 3,
    @SerialName("home_project_limit") val homeProjectLimit: Int = 3,
) {
    fun validate(): SiteConfig {
        require(siteName.isNotBlank()) { "site_name must not be blank" }
        require(titlePrefix.isNotBlank()) { "title_prefix must not be blank" }
        require(description.isNotBlank()) { "description must not be blank" }
        require(homePostLimit > 0) { "home_post_limit must be greater than zero" }
        require(homeProjectLimit > 0) { "home_project_limit must be greater than zero" }

        val uri = runCatching { URI(baseUrl) }
            .getOrElse { throw IllegalArgumentException("base_url is not a valid URL: $baseUrl", it) }
        require(uri.scheme == "http" || uri.scheme == "https") {
            "base_url must use http or https"
        }
        require(!uri.host.isNullOrBlank()) { "base_url must include a host" }
        require(uri.rawQuery == null && uri.rawFragment == null && uri.rawUserInfo == null) {
            "base_url must not contain credentials, a query, or a fragment"
        }
        require(uri.path.isNullOrEmpty()) {
            "base_url must not contain a path; use base_path instead"
        }
        require(!baseUrl.endsWith('/')) { "base_url must not end with /" }

        require(basePath.isEmpty() || basePath.startsWith('/')) {
            "base_path must be empty or start with /"
        }
        require(basePath.isEmpty() || !basePath.endsWith('/')) {
            "base_path must not end with /"
        }
        require('?' !in basePath && '#' !in basePath && '\\' !in basePath && "//" !in basePath) {
            "base_path must be a plain URL path"
        }
        require(basePath.split('/').none { it == "." || it == ".." }) {
            "base_path must not contain . or .. segments"
        }

        return this
    }

    /** Returns a root-relative URL, including the configured project base path. */
    fun sitePath(relativePath: String): String {
        val normalizedPath = relativePath.replace('\\', '/').trimStart('/')
        require(normalizedPath.split('/').none { it == "." || it == ".." }) {
            "Site path must not contain . or .. segments: $relativePath"
        }
        val rootPath = if (normalizedPath.isEmpty()) "/" else "/$normalizedPath"
        return if (basePath.isEmpty()) rootPath else "$basePath$rootPath"
    }

    fun absoluteUrl(relativePath: String): String = "$baseUrl${sitePath(relativePath)}"
}
