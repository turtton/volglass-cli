import io.ExecCommand

fun execMessage(cmd: ExecCommand): String = log("Exec", "${cmd.first} ${cmd.second.joinToString(" ")}")

fun create(message: String): String = log("Create", message)

fun note(message: String): String = log("Note", message)

fun warn(message: String): String = log("Warn", message)

fun info(message: String): String = log("Info", message)

fun log(prefix: String, message: String): String = "[$prefix] $message"
