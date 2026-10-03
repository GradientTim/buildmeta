package dev.gradienttim.buildmeta.helpers

import dev.gradienttim.buildmeta.BuildMetaExtension
import dev.gradienttim.buildmeta.meta.BuildMeta
import dev.gradienttim.buildmeta.meta.FieldMeta
import org.gradle.api.provider.Provider

private val TEMPLATE_VARIABLE_REGEX = Regex("\\$\\{([A-Z_]+)}")

public fun BuildMetaExtension.registerCiMeta(
    fileName: String? = "CiMeta",
    packageName: String? = null,
    fieldNamingStrategy: FieldMeta.NamingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE,
) {
    register("ciMeta") {
        this.fileName.set(fileName)
        this.packageName.set(packageName)
        this.fieldNamingStrategy.set(fieldNamingStrategy)

        val service = ciService()
        val isCi = service.map { true }.orElse(env("CI").map { !it.equals("false", ignoreCase = true) }).orElse(false)

        field("isCi", isCi)
        field("ciProvider", service.map { it.id }.orElse(isCi.map { if (it) "unknown" else "none" }))
        field("buildNumber", service.flatMap { firstEnv(it.buildNumber) }.orElse(""))
        field("buildUrl", service.flatMap { template(it.buildUrl) }.orElse(""))
        field("branch", ciBranch().orElse(""))
    }
}

internal fun BuildMeta.ciBranch(): Provider<String> =
    ciService().flatMap { firstEnv(it.branch) }.map { it.removePrefix("origin/") }

private enum class CiService(
    val id: String,
    val detect: String,
    val buildNumber: List<String>,
    val buildUrl: String?,
    val branch: List<String>,
) {
    GITHUB_ACTIONS(
        id = "github-actions",
        detect = "GITHUB_ACTIONS",
        buildNumber = listOf("GITHUB_RUN_NUMBER"),
        buildUrl = $$"${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}",
        branch = listOf("GITHUB_HEAD_REF", "GITHUB_REF_NAME"),
    ),
    GITLAB(
        id = "gitlab",
        detect = "GITLAB_CI",
        buildNumber = listOf("CI_PIPELINE_IID"),
        buildUrl = $$"${CI_PIPELINE_URL}",
        branch = listOf("CI_MERGE_REQUEST_SOURCE_BRANCH_NAME", "CI_COMMIT_REF_NAME"),
    ),
    JENKINS(
        id = "jenkins",
        detect = "JENKINS_URL",
        buildNumber = listOf("BUILD_NUMBER"),
        buildUrl = $$"${BUILD_URL}",
        branch = listOf("CHANGE_BRANCH", "BRANCH_NAME", "GIT_BRANCH"),
    ),
    CIRCLECI(
        id = "circleci",
        detect = "CIRCLECI",
        buildNumber = listOf("CIRCLE_BUILD_NUM"),
        buildUrl = $$"${CIRCLE_BUILD_URL}",
        branch = listOf("CIRCLE_BRANCH"),
    ),
    AZURE_PIPELINES(
        id = "azure-pipelines",
        detect = "TF_BUILD",
        buildNumber = listOf("BUILD_BUILDNUMBER"),
        buildUrl = $$"${SYSTEM_COLLECTIONURI}${SYSTEM_TEAMPROJECT}/_build/results?buildId=${BUILD_BUILDID}",
        branch = listOf("SYSTEM_PULLREQUEST_SOURCEBRANCH", "BUILD_SOURCEBRANCHNAME"),
    ),
    BITBUCKET(
        id = "bitbucket",
        detect = "BITBUCKET_BUILD_NUMBER",
        buildNumber = listOf("BITBUCKET_BUILD_NUMBER"),
        buildUrl = "\${BITBUCKET_GIT_HTTP_ORIGIN}/addon/pipelines/home#!/results/\${BITBUCKET_BUILD_NUMBER}",
        branch = listOf("BITBUCKET_BRANCH"),
    ),
    BUILDKITE(
        id = "buildkite",
        detect = "BUILDKITE",
        buildNumber = listOf("BUILDKITE_BUILD_NUMBER"),
        buildUrl = $$"${BUILDKITE_BUILD_URL}",
        branch = listOf("BUILDKITE_BRANCH"),
    ),
    TRAVIS(
        id = "travis",
        detect = "TRAVIS",
        buildNumber = listOf("TRAVIS_BUILD_NUMBER"),
        buildUrl = $$"${TRAVIS_BUILD_WEB_URL}",
        branch = listOf("TRAVIS_PULL_REQUEST_BRANCH", "TRAVIS_BRANCH"),
    ),
    TEAMCITY(
        id = "teamcity",
        detect = "TEAMCITY_VERSION",
        buildNumber = listOf("BUILD_NUMBER"),
        buildUrl = null,
        branch = emptyList(),
    ),
}

private fun BuildMeta.ciService(): Provider<CiService> =
    CiService.entries
        .map { service -> env(service.detect).map { service } }
        .reduce { detected, next -> detected.orElse(next) }

private fun BuildMeta.env(name: String): Provider<String> =
    providers.environmentVariable(name).map { it.ifEmpty { null } }

private fun BuildMeta.firstEnv(names: List<String>): Provider<String> =
    names.map { env(it) }.reduceOrNull { first, next -> first.orElse(next) } ?: providers.provider { null }

private fun BuildMeta.template(template: String?): Provider<String> {
    if (template == null) return providers.provider { null }
    return TEMPLATE_VARIABLE_REGEX.findAll(template).fold(providers.provider { template }) { result, match ->
        result.zip(env(match.groupValues[1])) { text, value -> text.replace(match.value, value) }
    }
}
