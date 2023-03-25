import command.Prepare
import io.exists
import io.mkdir
import io.writeAllText
import kotlin.test.Test
import kotlin.test.assertTrue
import okio.Path.Companion.toPath

class PrepareTest {
    @Test
    fun processContents() {
        val processDir = "processContents"
        prepare(processDir)
        val resultDir = "processed"
        resultDir.toPath().mkdir()
        Prepare.processContents(processDir, resultDir, true)

        val postDir = "$resultDir/posts"
        assertTrue("$postDir/README.md".toPath().exists(), "README.md")
        assertTrue("$postDir/text1.md".toPath().exists(), "text1.md")
        assertTrue("$postDir/child/text2.md".toPath().exists(), "text2.md")

        val imageDir = "$resultDir/public/images"
        assertTrue("$imageDir/image1.png".toPath().exists(), "image1.png")
        assertTrue("$imageDir/image2.jpeg".toPath().exists(), "image2.jpeg")
    }

    @Test
    fun processContentsForV04() {
        val processDir = "processContents"
        prepare(processDir)
        val resultDir = "processed"
        resultDir.toPath().mkdir()
        Prepare.processContents(processDir, resultDir, false)

        val postDir = "$resultDir/posts"
        assertTrue("$postDir/README.md".toPath().exists(), "README.md")
        assertTrue("$postDir/text1.md".toPath().exists(), "text1.md")
        assertTrue("$postDir/child/text2.md".toPath().exists(), "text2.md")

        assertTrue("$postDir/image1.png".toPath().exists(), "image1.png")
        assertTrue("$postDir/child/image2.jpeg".toPath().exists(), "image2.jpeg")
    }

    private fun prepare(processDirName: String) {
        val workDir = processDirName.toPath()
        workDir.mkdir()
        writeAllText("$processDirName/README.md", "# README!!!")
        writeAllText("$processDirName/text1.md", "# Test1")
        writeAllText("$processDirName/image1.png", "This is image1 file")
        val childDirName = "$processDirName/child"
        childDirName.toPath().mkdir()
        writeAllText("$childDirName/text2.md", "# Test2")
        writeAllText("$childDirName/image2.jpeg", "This is image2 file")
    }
}
