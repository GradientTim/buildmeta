package dev.gradienttim.buildmeta.helpers

import dev.gradienttim.buildmeta.BuildMetaExtension
import dev.gradienttim.buildmeta.meta.BuildMeta
import dev.gradienttim.buildmeta.meta.FieldMeta
import org.gradle.api.provider.Provider

private const val UNKNOWN = "unknown"

private val URL_USER_INFO_REGEX = Regex("^(\\w[\\w+.-]*://)[^/@]+@")

public fun BuildMetaExtension.registerGitMeta(
    fileName: String? = "GitMeta",
    packageName: String? = null,
    fieldNamingStrategy: FieldMeta.NamingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE,
    includeIdentity: Boolean = true,
    includeCommitDetails: Boolean = true,
    includeRepoStats: Boolean = true,
) {
    register("gitMeta") {
        this.fileName.set(fileName)
        this.packageName.set(packageName)
        this.fieldNamingStrategy.set(fieldNamingStrategy)

        if (includeIdentity) {
            field("commitHash", git("rev-parse", "HEAD").orUnknown())
            field("commitHashShort", git("rev-parse", "--short", "HEAD").orUnknown())
            val branch = git("rev-parse", "--abbrev-ref", "HEAD")
            field("branch", branch.map { it.takeUnless { name -> name == "HEAD" } }.orElse(ciBranch()).orElse(branch.orUnknown()))
            field("isDirty", git("status", "--porcelain").map { it.isNotEmpty() })
        }

        if (includeCommitDetails) {
            field("commitTimestamp", git("show", "-s", "--format=%ct", "HEAD").map { it.toLongOrNull() ?: 0L })
            field("commitMessage", git("show", "-s", "--format=%s", "HEAD"))
            field("commitAuthor", git("show", "-s", "--format=%an", "HEAD").orUnknown())
        }

        if (includeRepoStats) {
            field("commitCount", git("rev-list", "--count", "HEAD").map { it.toIntOrNull() ?: 0 })
            field("remoteUrl", git("remote", "get-url", "origin").map { it.replace(URL_USER_INFO_REGEX, "$1") })
        }
    }
}

private fun BuildMeta.git(vararg args: String): Provider<String> =
    providers
        .exec {
            commandLine("git", *args)
            isIgnoreExitValue = true
        }.standardOutput.asText
        .map { it.trim() }

private fun Provider<String>.orUnknown(): Provider<String> = map { it.ifEmpty { UNKNOWN } }
