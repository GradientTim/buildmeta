@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.Literals
import dev.gradienttim.buildmeta.helpers.registerCiMeta
import dev.gradienttim.buildmeta.helpers.registerEnvironmentMeta
import dev.gradienttim.buildmeta.helpers.registerGitMeta
import dev.gradienttim.buildmeta.helpers.registerProjectMeta
import dev.gradienttim.buildmeta.meta.FieldMeta
import java.net.URI
import java.time.*
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.time.Duration as JavaDuration
import java.time.Instant as JavaInstant

plugins {
    application
    kotlin("jvm") version "2.4.20"
    id("dev.gradienttim.buildmeta")
}

version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("dev.gradienttim.buildmeta.sample.MainKt")
}

buildMeta {
    fallbackFileName = "BuildMeta"
    fallbackPackageName = "dev.gradienttim.buildmeta.generated"
    fallbackFieldNamingStrategy = FieldMeta.NamingStrategy.KEEP
    indent = 4

    typeAdapters {
        register(Regex::class.java) { "Regex(${Literals.kotlinString(it.pattern)})" }
        register(UriDataType)
        register(List::class.java, needsImport = false, typeName = "List<String>") { list ->
            "listOf(${list.joinToString { Literals.kotlinString(it.toString()) }})"
        }
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

    register("javaTypes") {
        fileName = "JavaTypes"
        fieldNamingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE

        field("id", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
        field("timeout", JavaDuration.ofSeconds(30))
        field("releasedAt", JavaInstant.parse("2026-01-01T12:00:00Z"))
        field("releaseDate", LocalDate.of(2026, 1, 1))
        field("releaseTime", LocalTime.of(12, 0))
        field("releaseDateTime", LocalDateTime.of(2026, 1, 1, 12, 0))
        field("releaseOffsetDateTime", OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.ofHours(1)))
        field("releaseZonedDateTime", ZonedDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneId.of("Europe/Berlin")))
    }

    register("adapters") {
        fileName = "Adapters"
        packageName = "dev.gradienttim.buildmeta.generated.adapters"

        field("usernamePattern", Regex("[a-z]+\\d*"))
        field("website", URI("https://docs.gradienttim.dev/buildmeta"))
        field("supportedLocales", listOf("en", "de"))
    }

    registerCiMeta()
    registerGitMeta()
    registerProjectMeta()
    registerEnvironmentMeta()
}

object UriDataType : DataType<URI>() {
    override val classType: Class<URI> = URI::class.java
    override val needsImport: Boolean = true

    override fun format(value: URI): String = "URI.create(${Literals.kotlinString(value.toString())})"
}
