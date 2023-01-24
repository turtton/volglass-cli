import com.github.ajalt.clikt.core.subcommands
import command.Build
import command.Command
import command.Dev
import command.Init
import command.Serve
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

val COROUTINE_SCOPE = CoroutineScope(Dispatchers.Main)

@OptIn(ExperimentalJsExport::class)
@JsExport
fun volglass(args: Array<String>) {
    val command = Command().subcommands(Init(), Build(), Serve(), Dev())
    runCatching {
        command.main(args)
    }.onFailure {
        println("Err: Failed to parse command")
        println(command.commandHelp)
    }
}
