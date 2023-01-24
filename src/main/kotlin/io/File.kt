package io

import okio.NodeJsFileSystem as fs
import okio.Path
import okio.Path.Companion.toPath
import warn

const val VOLGLASS_DIR = "volglass"

const val CONTENT_DIR = "_$VOLGLASS_DIR"

const val POST_DIR = "posts"

fun readAllText(filePath: String): String {
    return fs.read(filePath.toPath()) {
        readUtf8()
    }
}

fun writeAllText(filePath: String, text: String) {
    return fs.write(filePath.toPath()) {
        writeUtf8(text)
    }
}

fun mkdir(path: String): Result<Unit> = runCatching {
    fs.createDirectory(path.toPath(), true)
}

fun Path.mkdir(): Result<Unit> = kotlin.runCatching {
    fs.createDirectories(this, true)
}

fun Path.delete(): Result<Unit> = kotlin.runCatching {
    fs.deleteRecursively(this)
}

fun Path.exists(): Boolean = fs.exists(this)

fun Path.isDirectory(): Result<Boolean> = kotlin.runCatching {
    fs.metadata(this).isDirectory
}

fun Path.list(): Result<List<Path>> = kotlin.runCatching {
    fs.list(this)
}

fun Path.extractFiles(): Result<Unit> = list().map { result ->
    result.forEach {
        val targetPath = it.toString().replace("/$name", "").toPath()
        it.atomicMove(targetPath)
    }
    delete()
}

fun Path.copy(targetPath: Path): Result<Unit> = kotlin.runCatching {
    fs.copy(this, targetPath)
}.onFailure {
    println(warn("Failed to copy file $targetPath"))
}

fun Path.atomicMove(targetPath: Path): Result<Unit> = kotlin.runCatching {
    fs.atomicMove(this, targetPath)
}

fun extractZipFile(targetPath: String, outputPath: String, overwrite: Boolean = false) {
    AdmZip(targetPath).extractAllTo(outputPath, overwrite)
}
