package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Strongly typed representation of the YAML front matter.
 */
@Serializable
data class FrontMatter(
    val title: String? = null,
    val name: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val date: String? = null,
    @SerialName("last_update") val lastUpdate: String? = null,
    val status: String? = null,
)
