package io

@JsModule("adm-zip")
@JsNonModule
external class AdmZip(targetFile: String) {
    fun extractAllTo(
        targetPath: String,
        overwrite: Boolean,
    )
}
