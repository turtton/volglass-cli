buildscript {
    configurations.classpath {
        resolutionStrategy {
            force(
                "com.pinterest.ktlint:ktlint-rule-engine:1.2.1",
                "com.pinterest.ktlint:ktlint-rule-engine-core:1.2.1",
                "com.pinterest.ktlint:ktlint-cli-reporter-core:1.2.1",
                "com.pinterest.ktlint:ktlint-cli-reporter-checkstyle:1.2.1",
                "com.pinterest.ktlint:ktlint-cli-reporter-json:1.2.1",
                "com.pinterest.ktlint:ktlint-cli-reporter-html:1.2.1",
                "com.pinterest.ktlint:ktlint-cli-reporter-plain:1.2.1",
                "com.pinterest.ktlint:ktlint-cli-reporter-sarif:1.2.1",
                "com.pinterest.ktlint:ktlint-ruleset-standard:1.2.1",
            )
        }
    }
}
plugins {
    kotlin("multiplatform") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
    id("de.jensklingenberg.ktorfit") version "2.0.0"
    id("dev.petuska.npm.publish") version "3.4.2"
    id("org.jmailen.kotlinter") version "4.3.0"
}

group = "net.turtton"
version = System.getenv()["VERSION_TAG"]?.replace("v", "") ?: "DEV"

repositories {
    mavenCentral()
}

kotlin {
    js {
        useCommonJs()
        binaries.library()
        nodejs {
            testTask {
                useMocha {
                    timeout = "10000"
                }
            }
        }
    }
    sourceSets {
        val coroutineVersion = "1.8.1"
        val jsMain by getting {
            dependencies {
                implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.760"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-node")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                implementation("com.github.ajalt.clikt:clikt:4.4.0")
                implementation("com.squareup.okio:okio:3.9.0")?.version?.also {
                    implementation("com.squareup.okio:okio-nodefilesystem:$it")
                }
                implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.0.0")
                implementation("io.ktor:ktor-client-core:2.3.11")?.version?.also {
                    implementation("io.ktor:ktor-client-content-negotiation:$it")
                    implementation("io.ktor:ktor-serialization-kotlinx-json:$it")
                }
                implementation("io.github.xxfast:kstore:0.8.0")?.version?.also {
                    implementation("io.github.xxfast:kstore-file:$it")
                }
                implementation(npm("adm-zip", "0.5.10"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
            }
        }
    }
}

// Exclude ksp generated files from lint target
tasks.whenTaskAdded {
    if (name.contains("Generated") && (name.contains("lint") || name.contains("format"))) {
        enabled = false
    }
}

tasks.create("cleanJsTestProject") {
    doFirst {
        file("${layout.buildDirectory}/js/packages/${project.name}-test").listFiles()?.forEach {
            // https://regexr.com/76mv8
            if (!it.name.contains("^(kotlin|node_modules|package.json|.*.js)$".toRegex())) {
                it.deleteRecursively()
            }
        }
    }
}.also {
    tasks.getByName("jsTest").dependsOn(it)
}

// https://github.com/google/ksp/issues/1525
tasks.create("kspCommonMainKotlinMetadata")

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
