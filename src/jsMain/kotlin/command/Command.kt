package command

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand

class Command : CliktCommand(
    help =
        """
        Volglass helper
        """.trimIndent(),
) {
    init {
        completionOption()
    }

    override fun run() = Unit
}
