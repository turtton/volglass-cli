package io

import external.spawn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import node.childProcess.ChildProcessEvent
import node.stream.ReadableEvent

typealias ExecCommand = Pair<String, Array<String>>

suspend fun spawnAsync(cmd: ExecCommand) = spawnAsync(cmd.first, *cmd.second)

suspend fun spawnAsync(
    command: String,
    vararg args: String,
) = suspendCoroutine { continuation ->
    spawn(command, args.toList().toTypedArray()).apply {
        stdout?.addListener(ReadableEvent.DATA) { data ->
            println(data)
        }
        stderr?.addListener(ReadableEvent.DATA) { data ->
            println(data)
        }
        on(ChildProcessEvent.EXIT) { code, _ ->
            continuation.resume(code)
        }
    }
}

fun pnpm(vararg args: String): ExecCommand = "pnpm" to args.toList().toTypedArray()

fun pnpmWithWorkSpace(
    workSpace: String,
    vararg args: String,
): ExecCommand = pnpm("-C", workSpace, *args)

fun pnpmVolglass(vararg args: String): ExecCommand = pnpmWithWorkSpace(VOLGLASS_DIR, *args)
