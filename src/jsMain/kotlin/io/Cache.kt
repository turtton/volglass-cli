package io

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.serialization.Serializable
import okio.Path.Companion.toPath

val CACHE: KStore<CacheData> = storeOf(file = "volglass-cache".toPath())

@Serializable
data class CacheData(val currentVersion: String)
