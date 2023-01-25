package command

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import create
import execMessage
import info
import io.CONTENT_DIR
import io.VOLGLASS_DIR
import io.copy
import io.copyRecursively
import io.delete
import io.exists
import io.mkdir
import io.pnpmVolglass
import io.spawnAsync
import note
import okio.Path.Companion.toPath

class Build : Prepare(
    "Build server with your contents",
) {
    private val nodeServer by option("-n", help = "Build nodejs server").flag()

    override suspend fun runAsChild() {
        echo(create(CONTENT_DIR))
        val contentDir = CONTENT_DIR.toPath()
        if (contentDir.exists()) {
            contentDir.delete()
        }
        contentDir.mkdir()
        echo(info("Copy files..."))
        val volglassDir = VOLGLASS_DIR.toPath()
        if (nodeServer) {
            val nextConfig = "next.config.js"
            volglassDir.div(nextConfig).copy(contentDir.div(nextConfig))
            val publicDir = "public"
            volglassDir.div(publicDir).copyRecursively(contentDir.div(publicDir))
            val postsDir = "posts"
            volglassDir.div(postsDir).copyRecursively(contentDir.div(postsDir))
            val packageJson = "package.json"
            volglassDir.div(packageJson).copy(contentDir.div(packageJson))
            val nextDir = ".next"
            val nextDirPath = volglassDir.div(nextDir)
            nextDirPath.div("standalone").copyRecursively(contentDir, "node_modules".toRegex())
            val staticDir = "static"
            val contentNextJsDir = contentDir.div(nextDir)
            contentNextJsDir.mkdir()
            nextDirPath.div(staticDir).copyRecursively(contentNextJsDir.div(staticDir))
            echo(note("To run the server, please run 'npm i && node server.js' in $CONTENT_DIR directory"))
        } else {
            val export = pnpmVolglass("run", "export")
            echo(execMessage(export))
            spawnAsync(export)
            volglassDir.div("out").copyRecursively(contentDir)
        }
        echo(info("Content generated in $CONTENT_DIR"))
    }
}
