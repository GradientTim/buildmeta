# BuildMeta

A Gradle plugin to generate build meta files for Kotlin (JVM / Multiplatform) and Java applications.

[Documentation](https://docs.gradienttim.dev/buildmeta) - [Gradle Plugin Portal](https://plugins.gradle.org/plugin/dev.gradienttim.buildmeta)

***

## Features

- Type-safe build values (version, commit hash, ...) generated as constants
- Support for Java, Kotlin JVM, and Kotlin Multiplatform
- Multiple generated files, each with its own name, package, and naming strategy
- Placeholders to write the same values into resource files
- Custom type adapters for any value type
- Lazy `Provider` values, including git calls and task outputs
- Configuration cache and isolated projects support

## Installation

Requires Gradle 9.7 or newer, plus the Java, Kotlin JVM, or Kotlin Multiplatform plugin.

```kotlin
plugins {
    id("dev.gradienttim.buildmeta") version "VERSION"
}
```

See the [installation guide](https://docs.gradienttim.dev/buildmeta/install) for more details.

## Usage

```kotlin
buildMeta {
    main {
        field("appName", "ExampleApp")
        field("appVersion", "1.0.0")
    }
}
```

```kotlin
import com.example.generated.buildmeta.BuildMeta

fun main() {
    println("${BuildMeta.appName} v${BuildMeta.appVersion}") // ExampleApp v1.0.0
}
```

See the [usage guide](https://docs.gradienttim.dev/buildmeta/usage) for field options, multiple BuildMetas, and
values from git or environment variables. [Types](https://docs.gradienttim.dev/buildmeta/types) and
[Placeholders](https://docs.gradienttim.dev/buildmeta/placeholders) have their own pages.

## Building from Source

Requires a JDK 17+.

```bash
git clone https://github.com/GradientTim/buildmeta.git
cd buildmeta
./gradlew build
```

The `samples` directory contains example projects for Java, Kotlin JVM, and Kotlin Multiplatform.
