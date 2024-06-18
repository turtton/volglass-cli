package io.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseData(
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("zipball_url")
    val zipballUrl: String,
)
