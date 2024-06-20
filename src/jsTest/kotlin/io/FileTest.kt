package io

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import okio.Path.Companion.toPath

class FileTest {
    @Test
    fun copyRecursivelyTest() {
        val origin = "copyRecursivelyFrom".toPath()
        origin.mkdir()
        val testFile = "copyRecursively.txt"
        origin.div(testFile).writeText("test")
        val child = origin.div("child")
        child.mkdir()
        child.div(testFile).writeText("test")

        val target = "copyRecursivelyTo".toPath()
        target.mkdir()
        origin.copyRecursively(target)

        assertTrue(target.div(testFile).exists(), "test file")
        assertTrue(target.div("child").div(testFile).exists(), "chilld test file")

        val testFilePath = testFile.toPath()
        testFilePath.writeText("test")
        val resultPath = "copyRecursivelyNormalCopied.txt".toPath()
        testFilePath.copyRecursively(resultPath)
        assertFalse(resultPath.isDirectory().getOrNull() ?: true, "Normal copy")
    }
}
