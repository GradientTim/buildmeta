package dev.gradienttim.buildmeta.sample

import dev.gradienttim.buildmeta.generated.BuildMeta
import dev.gradienttim.buildmeta.generated.GitMeta
import dev.gradienttim.buildmeta.generated.Types
import dev.gradienttim.buildmeta.generated.adapters.Adapters

fun buildMetaSummary(): String =
    listOf(
        "${BuildMeta.appName} v${BuildMeta.APP_VERSION} built by ${BuildMeta.buildUser}",
        "internalNote = ${BuildMeta.internalNote}",
        "Git commit hash = ${GitMeta.COMMIT_HASH}, dirty = ${GitMeta.IS_DIRTY}",
        "greeting = ${Types.GREETING.trim()}",
        "letter = ${Types.LETTER}, answer = ${Types.ANSWER}, bigNumber = ${Types.BIG_NUMBER}",
        "pi = ${Types.PI}, ratio = ${Types.RATIO}, enabled = ${Types.ENABLED}",
        "id = ${Types.ID}, timeout = ${Types.TIMEOUT}, releasedAt = ${Types.RELEASED_AT}",
        "rawName = ${Types.rawName}",
        "usernamePattern matches 'tim' = ${Adapters.usernamePattern.matches("tim")}",
        "supportedLevels = ${Adapters.supportedLevels}",
    ).joinToString("\n")
