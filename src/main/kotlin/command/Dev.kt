package command

import COROUTINE_SCOPE
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import execMessage
import io.POST_DIR
import io.copyRecursively
import io.delete
import io.exists
import io.extractFiles
import io.extractZipFile
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.list
import io.pnpmWithWorkSpace
import io.spawnAsync
import io.writeToFile
import kotlinx.coroutines.launch
import log
import note
import okio.Path
import okio.Path.Companion.toPath

class Dev : CliktCommand() {
    private val useSsh by option("--use-ssh", "-s").flag()
    private val useTemplateContent by option(
        "--use-template",
        "-T",
        help = "Download template contents and builds with it",
    ).flag()
    private val devDirName by option("-f").default("dev")

    override fun run() {
        COROUTINE_SCOPE.launch {
            clone(devDirName, useSsh)
            if (useTemplateContent) {
                val templateDir = "template"
                downloadTemplate(templateDir.toPath()).onFailure {
                    it.printStackTrace()
                    error("Failed to download templates")
                }
                Prepare.processContents(templateDir, devDirName, false)
            } else {
                Prepare.processContents(POST_DIR, devDirName, false)
            }
            val devInstall = pnpmWithWorkSpace(devDirName, "i")
            echo(execMessage(devInstall))
            spawnAsync(devInstall)
            val devRunDev = pnpmWithWorkSpace(devDirName, "run", "dev")
            echo(execMessage(devRunDev))
            spawnAsync(devRunDev)
        }
    }

    companion object {
        suspend fun clone(workDir: String, useSsh: Boolean) {
            val devDir = workDir.toPath()
            if (devDir.exists()) {
                println(log("Detect", workDir))
                return
            }
            val url = if (useSsh) "git@github.com:turtton/volglass.git" else "https://github.com/turtton/volglass.git"
            val cloneCommand = "git" to arrayOf("clone", url, workDir)
            println(execMessage(cloneCommand))
            spawnAsync(cloneCommand)
        }

        suspend fun downloadTemplate(targetDir: Path): Result<Unit> = kotlin.runCatching {
            if (targetDir.exists()) {
                println(note("$targetDir already exists. If you want to download again, please delete it."))
                return@runCatching
            }
            val templateRepoDir = "templateRepo"
            val templateRepoDirPath = templateRepoDir.toPath()
            val templateRepoZip = "templateRepo.zip"
            HttpClient()
                .prepareGet("https://github.com/turtton/volglass-docs/archive/refs/heads/main.zip")
                .writeToFile(templateRepoZip)
            extractZipFile(templateRepoZip, templateRepoDir)
            templateRepoDirPath.list().getOrThrow()[0].extractFiles()
            val templateContentDir = templateRepoDirPath.div("posts")
            templateContentDir.copyRecursively(targetDir)
            templateRepoZip.toPath().delete().getOrThrow()
            templateRepoDirPath.delete().getOrThrow()
        }
    }
}
