package command

import COROUTINE_SCOPE
import com.github.ajalt.clikt.core.CliktCommand
import create
import execMessage
import io.POST_DIR
import io.mkdir
import io.spawnAsync
import kotlinx.coroutines.launch
import note

class Init : CliktCommand() {
    override fun run() {
        COROUTINE_SCOPE.launch {
            val installPnpm = "npm" to arrayOf("i", "--save-dev", "--silent", "pnpm")
            echo(execMessage(installPnpm))
            echo(note("Npm cannot output progress. Please wait few minutes..."))
            // TODO print npm progress
            spawnAsync(installPnpm)
            mkdir(POST_DIR).onSuccess {
                echo(create(POST_DIR))
                echo(note("Put in your vault contents into posts directory"))
            }
        }
    }
}
