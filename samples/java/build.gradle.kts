import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.Literals
import dev.gradienttim.buildmeta.meta.FieldMeta
import java.net.URI
import java.time.*
import java.util.*
import java.util.regex.Pattern

plugins {
    java
    application
    id("dev.gradienttim.buildmeta")
}

version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

application {
    mainClass.set("dev.gradienttim.buildmeta.sample.Main")
}

buildMeta {
    fallbackFileName = "BuildMeta"
    fallbackPackageName = "dev.gradienttim.buildmeta.generated"
    fallbackFieldNamingStrategy = FieldMeta.NamingStrategy.KEEP
    indent = 4

    typeAdapters {
        register(Pattern::class.java) { "Pattern.compile(${Literals.javaString(it.pattern())})" }
        register(UriDataType)
    }

    main {
        field("appName", "ExampleApp")
        field("appVersion", { project.version.toString() }) {
            namingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE
        }
        field("buildUser", providers.environmentVariable("USER").orElse("unknown"))
        field("internalNote", "only visible inside the generated package") {
            visibility = FieldMeta.Visibility.INTERNAL
        }
    }

    register("types") {
        fileName = "Types"
        fieldNamingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE

        field("greeting", "Hello \"World\"\n")
        field("letter", 'x')
        field("answer", 42)
        field("bigNumber", Long.MIN_VALUE)
        field("pi", 3.14159)
        field("ratio", 0.75f)
        field("enabled", true)
        field("id", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
        field("timeout", Duration.ofSeconds(30))
        field("releasedAt", Instant.parse("2026-01-01T12:00:00Z"))
        field("releaseDate", LocalDate.of(2026, 1, 1))
        field("releaseTime", LocalTime.of(12, 0))
        field("releaseDateTime", LocalDateTime.of(2026, 1, 1, 12, 0))
        field("releaseOffsetDateTime", OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.ofHours(1)))
        field("releaseZonedDateTime", ZonedDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneId.of("Europe/Berlin")))
        field("rawName", "kept as is") {
            namingStrategy = FieldMeta.NamingStrategy.KEEP
        }
    }

    register("adapters") {
        fileName = "Adapters"
        packageName = "dev.gradienttim.buildmeta.generated.adapters"

        field("usernamePattern", Pattern.compile("[a-z]+\\d*"))
        field("website", URI("https://docs.gradienttim.dev/buildmeta"))
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

object UriDataType : DataType<URI>() {
    override val classType: Class<URI> = URI::class.java
    override val needsImport: Boolean = true

    override fun format(value: URI): String = "URI.create(${Literals.javaString(value.toString())})"
}

fun git(vararg args: String): Provider<String> =
    providers
        .exec {
            commandLine("git", *args)
            isIgnoreExitValue = true
        }.standardOutput.asText
        .map { it.trim() }
