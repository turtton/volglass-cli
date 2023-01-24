package command

import create
import io.CONTENT_DIR
import io.VOLGLASS_DIR
import io.atomicMove
import io.copy
import io.list
import io.mkdir
import log
import okio.Path.Companion.toPath

class Build : Prepare() {

    override suspend fun runAsChild() {
        echo(create(CONTENT_DIR))
        val contentDir = CONTENT_DIR.toPath()
        contentDir.mkdir()
        val volglassDir = VOLGLASS_DIR.toPath()
        val nextConfig = "next.config.js"
        volglassDir.div(nextConfig).copy(contentDir.div(nextConfig))
        val publicDir = "public"
        volglassDir.div(publicDir).atomicMove(contentDir.div(publicDir))
        val packageJson = "package.json"
        volglassDir.div(packageJson).copy(contentDir.div(packageJson))
        val nextDir = ".next"
        val nextDirPath = volglassDir.div(nextDir)
        nextDirPath.div("standalone").list().getOrNull()?.forEach {
            it.atomicMove(contentDir)
        }
        val staticDir = "static"
        nextDirPath.div(staticDir).atomicMove(contentDir.div(staticDir))
        echo(log("Done", "Content generated in $CONTENT_DIR"))
    }
}
