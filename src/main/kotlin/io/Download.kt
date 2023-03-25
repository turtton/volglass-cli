package io

import de.jensklingenberg.ktorfit.Ktorfit
import info
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsChannel
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import note
import okio.Buffer
import okio.NodeJsFileSystem
import okio.Path.Companion.toPath

const val OWNER = "turtton"
const val REPO = "volglass"

suspend fun downloadLatestRepo(): String {
    val ktorfit = createClient()
    val githubApi = ktorfit.create<GithubApi>()
    println(info("Checking new releases"))
    val releaseData = githubApi.getLatestRelease(OWNER, REPO)
    val currentCacheData = CACHE.get()
    val currentTag = currentCacheData?.currentVersion
    val tag = releaseData.tagName
    val volglassPath = VOLGLASS_DIR.toPath()
    if (volglassPath.exists()) {
        if (tag == currentTag) {
            println(note("Latest version is already downloaded. If you want to download again, please remove current $VOLGLASS_DIR file."))
            return tag
        } else {
            volglassPath.delete()
            CONTENT_DIR.toPath().delete()
        }
    }
    println("Start downloading new release")
    val zipFile = "$VOLGLASS_DIR.zip"
    githubApi.downloadZipBall(OWNER, REPO, tag).writeToFile(zipFile)
    extractZipFile(zipFile, VOLGLASS_DIR)
    zipFile.toPath().delete()
    volglassPath.list().getOrThrow()[0].extractFiles()
    CACHE.set(currentCacheData?.copy(currentVersion = tag) ?: CacheData(tag))
    return tag
}

suspend fun HttpStatement.writeToFile(path: String) = execute { response ->
    val contentBody = response.bodyAsChannel()
    val bufferSize = 1024 * 100
    val buffer = ByteArray(bufferSize)

    var lastPrintedTime = Clock.System.now().minus(2.seconds)
    val zipFile = NodeJsFileSystem.sink(path.toPath())
    do {
        val current = contentBody.readAvailable(buffer, 0, bufferSize)

        if (current > 0) {
            val now = Clock.System.now()
            if (now.minus(lastPrintedTime) > 1.seconds) {
                println("Downloading...${contentBody.totalBytesRead} bytes")
                lastPrintedTime = now
            }
            val writableBuffer = if (current < bufferSize) buffer.sliceArray(0 until current) else buffer
            zipFile.write(Buffer().write(writableBuffer), writableBuffer.size.toLong())
        }
    } while (!contentBody.isClosedForRead)
    zipFile.flush()
    println("Complete! Total:${contentBody.totalBytesRead} bytes")
}

fun createClient(): Ktorfit = Ktorfit.Builder().build {
    httpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    }
    baseUrl("https://api.github.com/")
}
