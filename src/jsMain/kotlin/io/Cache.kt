package io

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.serialization.Serializable

val CACHE: KStore<CacheData> = storeOf(filePath = "volglass-cache")

@Serializable
data class CacheData(val currentVersion: String)
