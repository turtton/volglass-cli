plugins {
    kotlin("js") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
    id("de.jensklingenberg.ktorfit") version "1.0.0"
    id("dev.petuska.npm.publish") version "3.2.0"
    id("org.jmailen.kotlinter") version "3.14.0"
}

group = "net.turtton"
version = System.getenv()["VERSION_TAG"]?.replace("v", "") ?: "DEV"

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.521"))
    implementation("org.jetbrains.kotlin-wrappers:kotlin-node")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")?.version?.also {
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$it")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("com.github.ajalt.clikt:clikt:3.5.2")
    implementation("com.squareup.okio:okio:3.3.0")?.version?.also {
        implementation("com.squareup.okio:okio-nodefilesystem:$it")
    }
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta17")?.version?.also {
        implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$it")
    }
    implementation("io.ktor:ktor-client-core:2.2.2")?.version?.also {
        implementation("io.ktor:ktor-client-content-negotiation:$it")
        implementation("io.ktor:ktor-serialization-kotlinx-json:$it")
    }
    implementation("io.github.xxfast:kstore:0.3.0")
    implementation(npm("adm-zip", "0.5.10"))
    testImplementation(kotlin("test"))
}

kotlin {
    js {
        binaries.library()
        nodejs {
            testTask {
                useMocha {
                    timeout = "10000"
                }
            }
        }
    }
}

kotlinter {
    experimentalRules = true
}

tasks.create("cleanJsTestProject") {
    doFirst {
        file("$buildDir/js/packages/${project.name}-test").listFiles()?.forEach {
            // https://regexr.com/76mv8
            if (!it.name.contains("^(kotlin|node_modules|package.json|.*.js)$".toRegex())) {
                it.deleteRecursively()
            }
        }
    }
}.also {
    tasks.test.get().dependsOn(it)
}

npmPublish {
    registries {
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
            val token = System.getenv()["NODE_AUTH_TOKEN"] ?: return@register
            authToken.set(token)
        }
    }
    packages {
        getByName("js") {
            readme.set(file("README.md"))
            packageJson {
                "bin" by {
                    "volglass" by "boot.js"
                }
                main.set("boot.js")
                private.set(false)
                keywords.set(listOf("volglass", "markdown", "kotlin"))
                repository {
                    type.set("git")
                    url.set("https://github.com/turtton/volglass-cli")
                }
            }
            files {
                from("bin/boot.js")
            }
        }
    }
}
