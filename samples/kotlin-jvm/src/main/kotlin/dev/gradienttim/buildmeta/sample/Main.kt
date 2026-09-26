package dev.gradienttim.buildmeta.sample

import dev.gradienttim.buildmeta.generated.BuildMeta
import dev.gradienttim.buildmeta.generated.GitMeta
import dev.gradienttim.buildmeta.generated.JavaTypes
import dev.gradienttim.buildmeta.generated.Types
import dev.gradienttim.buildmeta.generated.adapters.Adapters

fun main() {
    println("${BuildMeta.appName} v${BuildMeta.APP_VERSION} built by ${BuildMeta.buildUser}")
    println("internalNote = ${BuildMeta.internalNote}")
    println("Git commit hash = ${GitMeta.COMMIT_HASH}, dirty = ${GitMeta.IS_DIRTY}")

    print("greeting = ${Types.GREETING}")
    println("letter = ${Types.LETTER}, answer = ${Types.ANSWER}, bigNumber = ${Types.BIG_NUMBER}")
    println("pi = ${Types.PI}, ratio = ${Types.RATIO}, enabled = ${Types.ENABLED}")
    println("id = ${Types.ID}, timeout = ${Types.TIMEOUT}, releasedAt = ${Types.RELEASED_AT}")
    println("rawName = ${Types.rawName}")

    println("java id = ${JavaTypes.ID}, java timeout = ${JavaTypes.TIMEOUT}, java releasedAt = ${JavaTypes.RELEASED_AT}")
    println("releaseDate = ${JavaTypes.RELEASE_DATE}, releaseTime = ${JavaTypes.RELEASE_TIME}")
    println("releaseDateTime = ${JavaTypes.RELEASE_DATE_TIME}")
    println("releaseOffsetDateTime = ${JavaTypes.RELEASE_OFFSET_DATE_TIME}")
    println("releaseZonedDateTime = ${JavaTypes.RELEASE_ZONED_DATE_TIME}")

    println("usernamePattern matches 'tim' = ${Adapters.usernamePattern.matches("tim")}")
    println("website = ${Adapters.website}")
    println("supportedLocales = ${Adapters.supportedLocales}")

    val properties = object {}.javaClass.getResource("/app.properties")!!.readText()
    print("\napp.properties:\n$properties")
}
