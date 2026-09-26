import org.gradle.plugin.compatibility.compatibility
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "2.2.1"
}

group = property("project.group") as String
version = property("project.version") as String

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
    explicitApi()
}

afterEvaluate {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            languageVersion.set(KotlinVersion.KOTLIN_2_4)
            apiVersion.set(KotlinVersion.KOTLIN_2_4)
        }
    }
}

gradlePlugin {
    vcsUrl = "https://github.com/GradientTim/buildmeta"
    website = "https://docs.gradienttim.dev/buildmeta"

    plugins {
        create("buildMeta") {
            id = "dev.gradienttim.buildmeta"
            tags = listOf("buildconfig", "build-metadata", "metadata", "codegen", "kotlin", "kotlin-multiplatform", "java")
            displayName = "BuildMeta"
            description = "Generates type-safe build metadata classes for Kotlin JVM, Kotlin Multiplatform and Java projects."
            implementationClass = "dev.gradienttim.buildmeta.BuildMetaPlugin"

            compatibility {
                features {
                    isolatedProjects = true
                    configurationCache = true
                }
            }
        }
    }
}
