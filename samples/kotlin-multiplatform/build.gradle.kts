@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.Literals
import dev.gradienttim.buildmeta.meta.FieldMeta
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

plugins {
    kotlin("multiplatform") version "2.4.20"
    id("dev.gradienttim.buildmeta")
}

version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
    jvm()
}

buildMeta {
    fallbackFileName = "BuildMeta"
    fallbackPackageName = "dev.gradienttim.buildmeta.generated"
    fallbackFieldNamingStrategy = FieldMeta.NamingStrategy.KEEP
    indent = 4

    typeAdapters {
        register(Regex::class.java) { "Regex(${Literals.kotlinString(it.pattern)})" }
        register(IntRangeDataType)
    }

    main {
        field("appName", "ExampleApp")
        field("appVersion", { project.version.toString() }) {
            namingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE
        }
        field("buildUser", providers.environmentVariable("USER").orElse("unknown"))
        field("internalNote", "only visible inside this module") {
            visibility = FieldMeta.Visibility.INTERNAL
        }
    }

    register("types") {
        fileName = "Types"
        fieldNamingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE

        field("greeting", "Hello \"World\", this costs \$5\n")
        field("letter", 'x')
        field("answer", 42)
        field("bigNumber", Long.MIN_VALUE)
        field("pi", 3.14159)
        field("ratio", 0.75f)
        field("enabled", true)
        field("id", Uuid.parse("123e4567-e89b-12d3-a456-426614174000"))
        field("timeout", 30.seconds)
        field("releasedAt", Instant.parse("2026-01-01T12:00:00Z"))
        field("rawName", "kept as is") {
            namingStrategy = FieldMeta.NamingStrategy.KEEP
        }
    }

    register("adapters") {
        fileName = "Adapters"
        packageName = "dev.gradienttim.buildmeta.generated.adapters"

        field("usernamePattern", Regex("[a-z]+\\d*"))
        field("supportedLevels", 1..10)
    }

    register("gitMeta") {
        fileName = "GitMeta"
        fieldNamingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE

        field("commitHash", git("rev-parse", "--short", "HEAD").map { it.ifEmpty { "unknown" } })
        field("isDirty", git("status", "--porcelain").map { it.isNotEmpty() }) {
            visibility = FieldMeta.Visibility.INTERNAL
        }
    }
}

object IntRangeDataType : DataType<IntRange>() {
    override val classType: Class<IntRange> = IntRange::class.java
    override val needsImport: Boolean = false

    override fun format(value: IntRange): String = "${value.first}..${value.last}"
}

fun git(vararg args: String): Provider<String> =
    providers
        .exec {
            commandLine("git", *args)
            isIgnoreExitValue = true
        }.standardOutput.asText
        .map { it.trim() }
