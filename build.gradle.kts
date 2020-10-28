import java.util.UUID
import java.util.Date
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import java.net.URI

plugins {
    kotlin("multiplatform") version "1.4.10"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
}
group = "io.skipn"
version = "0.0.1b"
val kversion = "1.4.1"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/ktor")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")
                implementation("io.ktor:ktor-client-core:$kversion")
                implementation("io.ktor:ktor-client-json:$kversion")
                implementation("io.ktor:ktor-client-serialization:$kversion")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.4.10")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:$kversion")
                implementation("io.ktor:ktor-html-builder:$kversion")
                implementation("io.ktor:ktor-serialization:$kversion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("is-sorted", "1.0.5"))
                implementation(devNpm("postcss-loader", "4.0.0"))
                implementation(devNpm("postcss", "7.0.32"))
                implementation(devNpm("raw-loader", ""))
                implementation(npm("css-loader", "3.4.2"))
                implementation(npm("style-loader", "1.1.3"))
                implementation(npm("google-maps", "4.3.3"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

//val pomUrl = "https://github.com/stevenflautner/skipn"
//val pomScmUrl = "https://github.com/stevenflautner/skipn"
//val pomIssueUrl = "https://github.com/stevenflautner/skipn/issues"
//val pomDesc = "https://github.com/stevenflautner/skipn"
//
//val githubRepo = "stevenflautner/skipn"
//val githubReadme = "README.md"
//
//val pomLicenseName = "MIT"
//val pomLicenseUrl = "https://opensource.org/licenses/mit-license.php"
//val pomLicenseDist = "skipn"
//
//val pomDeveloperId = "stevenflautner"
//val pomDeveloperName = "Steven Flautner"

val artifactName = project.name
val artifactGroup = project.group.toString()
val artifactVersion = project.version.toString()

val bintrayRepo = "skipn"
val owner = "stevenflautner"
val repoName = "skipn"
val versionDescription = "Pre-release 0.0.1"
val license = "MIT"
val projVcsUrl = "https://github.com/stevenflautner/skipn.git"

publishing {
    publications {
        bintray {
            user = "stevenflautner"
            key = project.findProperty("bintrayKey").toString()
            publish = true

            pkg.apply {
                repo = bintrayRepo
                name = repoName
                userOrg = "skipn"
                setLicenses("MIT")
                vcsUrl = projVcsUrl
                version.apply {
                    name = artifactVersion
                    desc = versionDescription
                    released = Date().toString()
                    vcsTag = artifactVersion
                }
            }
        }
    }
}

tasks.withType<com.jfrog.bintray.gradle.tasks.BintrayUploadTask> {
    dependsOn(tasks.getByName("publishToMavenLocal"))
    doFirst {
        setPublications(
            *publishing.publications
                .filterIsInstance<MavenPublication>()
                .map { publication ->
                    val moduleFile = buildDir.resolve("publications/${publication.name}/module.json")
                    if (moduleFile.exists()) {
                        publication.artifact(object : org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact(moduleFile) {
                            override fun getDefaultExtension() = "module"
                        })
                    }
                    publication.name
                }.toTypedArray()
        )
    }
}