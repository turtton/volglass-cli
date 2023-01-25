import command.Dev
import io.exists
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath

@OptIn(ExperimentalCoroutinesApi::class)
class DevTest {
    @Test
    fun downloadTemplateTest() = runTest {
        val templateDirPath = "DownloadTemplateTestDir".toPath()
        Dev.downloadTemplate(templateDirPath).getOrThrow()
        assertTrue(templateDirPath.div("README.md").exists(), "downloaded README.md")
    }
}
