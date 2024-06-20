package command

import execMessage
import io.pnpmVolglass
import io.spawnAsync

class Serve : Prepare() {
    override suspend fun runAsChild() {
        val serve = pnpmVolglass("run", "start")
        echo(execMessage(serve))
        spawnAsync(serve)
    }
}
