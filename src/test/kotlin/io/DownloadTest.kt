package io

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath

@ExperimentalCoroutinesApi
class DownloadTest {
    private val githubApi = createClient().create<GithubApi>()

    @Test
    fun testRelease() = runTest {
        val owner = "Kotlin"
        val repo = "kotlinx.serialization"
        val result = githubApi.getLatestRelease(owner, repo)
        val tag = result.tagName
        assertEquals(result.htmlUrl, "https://github.com/$owner/$repo/releases/tag/$tag")
        assertEquals(result.zipballUrl, "https://api.github.com/repos/$owner/$repo/zipball/$tag")
    }

    @Test
    fun downloadZipAndExtract() = runTest(dispatchTimeoutMs = 10.seconds.toLong(DurationUnit.MILLISECONDS)) {
        val zipFileName = "test.zip"
        val zipFilePath = zipFileName.toPath()
        assertTrue(!zipFilePath.exists(), "Zip file pre check")
        githubApi.donwloadZipBall("turtton", "YtAlarm", "v0.1.0").writeToFile(zipFileName)
        assertTrue(zipFilePath.exists(), "ZipFile Exists")
        val outputName = "test"
        val outputPath = outputName.toPath()
        extractZipFile(zipFileName, outputName)

        assertTrue(outputPath.exists(), "Output Exists")

        outputPath.list().getOrThrow()[0].extractFiles()

        val result = outputPath.list().getOrThrow()
        assertTrue(result.any { it.toString().contains("README.md") }, "README exists")
    }
}
