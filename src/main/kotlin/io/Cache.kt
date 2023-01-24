package io

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storeOf
import kotlinx.serialization.Serializable

val cache: KStore<CacheData> = storeOf("volglass-cache")

@Serializable
data class CacheData(val currentVersion: String)
