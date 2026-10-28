package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FrontMatter(
    val title: String? = null,
    val name: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val tag: String? = null,
    val date: String? = null,
    @SerialName("last_update") val lastUpdate: String? = null,
    val status: String? = null,
)
