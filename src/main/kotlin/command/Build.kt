package command

import create
import execMessage
import io.CONTENT_DIR
import io.VOLGLASS_DIR
import io.copyRecursively
import io.mkdir
import io.pnpmVolglass
import io.spawnAsync
import log
import okio.Path.Companion.toPath

class Build : Prepare() {

    override suspend fun runAsChild() {
        val export = pnpmVolglass("run", "export")
        echo(execMessage(export))
        spawnAsync(export)

        echo(create(CONTENT_DIR))
        val contentDir = CONTENT_DIR.toPath()
        contentDir.mkdir()

        val volglassDir = VOLGLASS_DIR.toPath()

        volglassDir.div("out").copyRecursively(contentDir)

        // val nextConfig = "next.config.js"
        // volglassDir.div(nextConfig).copy(contentDir.div(nextConfig))
        // val publicDir = "public"
        // volglassDir.div(publicDir).copyRecursively(contentDir.div(publicDir))
        // val packageJson = "package.json"
        // volglassDir.div(packageJson).copy(contentDir.div(packageJson))
        // val nextDir = ".next"
        // val nextDirPath = volglassDir.div(nextDir)
        // nextDirPath.div("standalone").copyRecursively(contentDir)
        // val staticDir = "static"
        // val contentNextJsDir = contentDir.div(nextDir)
        // contentNextJsDir.mkdir()
        // nextDirPath.div(staticDir).copyRecursively(contentNextJsDir.div(staticDir))
        echo(log("Done", "Content generated in $CONTENT_DIR"))
    }
}
