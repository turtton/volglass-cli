@file:JsModule("node:child_process")
@file:JsNonModule

package external

import js.array.ReadonlyArray
import node.childProcess.ChildProcess

external fun spawn(
    modulePath: String,
    args: ReadonlyArray<String> = definedExternally,
): ChildProcess
