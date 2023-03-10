package command

import COROUTINE_SCOPE
import com.github.ajalt.clikt.core.CliktCommand
import execMessage
import io.POST_DIR
import io.VOLGLASS_DIR
import io.copy
import io.downloadLatestRepo
import io.exists
import io.isDirectory
import io.list
import io.mkdir
import io.pnpmVolglass
import io.spawnAsync
import io.writeAllText
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import warn

abstract class Prepare(help: String = "") : CliktCommand(help) {
    override fun run() {
        COROUTINE_SCOPE.launch {
            downloadLatestRepo()
            val prepareVolglass = pnpmVolglass("i")
            echo(execMessage(prepareVolglass))
            spawnAsync(prepareVolglass)
            processContents(POST_DIR, VOLGLASS_DIR)
            val buildContent = pnpmVolglass("run", "build")
            echo(execMessage(buildContent))
            for (i in 1..6) {
                val exitCode = spawnAsync(buildContent)
                if (exitCode == 0) {
                    break
                } else if (i == 6) {
                    error("Failed to build volglass")
                } else {
                    print(warn("($i/5) Failed to build contents retrying"))
                    repeat(3) {
                        delay(1.seconds)
                        print(".")
                        if (it == 3) {
                            echo()
                        }
                    }
                }
            }
            runAsChild()
        }
    }

    abstract suspend fun runAsChild()

    companion object {
        fun processContents(targetDirectory: String, outputDir: String, mkDirs: Boolean = true, rootPath: String? = null) {
            val contentPostDir = "$outputDir/posts".toPath()
            val contentImageDir = "$outputDir/public/images".toPath()
            val targetDirPath = targetDirectory.toPath()
            if (mkDirs) {
                contentPostDir.mkdir()
                contentImageDir.mkdir()
                if (!targetDirPath.div("README.md").exists()) {
                    println(warn("Cannot detect README.md. Please create note in $targetDirectory/README.md"))
                    writeAllText(contentPostDir.div("README.md").toString(), "# README")
                }
            }
            targetDirPath.list().getOrNull()?.forEach {
                if (it.isDirectory().getOrThrow()) {
                    processContents(it.toString(), outputDir, false, rootPath ?: targetDirectory)
                } else {
                    val targetPath = it.toString().split(rootPath ?: targetDirectory).last()
                    val fileName = it.name
                    val extension = fileName.split(".").lastOrNull()
                    if (extension != "md") {
                        val contentImageFile = "$contentImageDir/$fileName".toPath()
                        if (contentImageFile.exists()) {
                            println(warn("$fileName is already exists. Please rename either"))
                        }
                        it.copy(contentImageFile)
                    } else {
                        val targetFilePath = "$contentPostDir$targetPath"
                        val parent = targetFilePath.toPath().parent!!
                        if (!parent.exists()) {
                            parent.mkdir()
                        }
                        it.copy(targetFilePath.toPath())
                    }
                }
            }
        }
    }
}
