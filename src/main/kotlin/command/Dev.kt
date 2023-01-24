package command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import coroutineScope
import execMessage
import io.POST_DIR
import io.exists
import io.pnpmWithWorkSpace
import io.spawnAsync
import kotlinx.coroutines.launch
import log
import okio.Path.Companion.toPath

class Dev : CliktCommand() {
    private val useSsh by option("--use-ssh", "-s").flag()
    private val useTemplateContent by option("--template-content", "-C").flag()
    private val devDirName by option("-f").default("dev")

    override fun run() {
        coroutineScope.launch {
            clone(devDirName, useSsh)
            if (useTemplateContent) {
                TODO("Not implemented yet")
            } else {
                Prepare.processContents(POST_DIR, devDirName)
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
    }
}
