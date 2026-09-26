package dev.gradienttim.buildmeta.tasks

import org.gradle.api.GradleException

internal fun failOnProblems(problems: List<String>) {
    if (problems.isEmpty()) return

    throw GradleException(
        problems.joinToString(
            separator = "\n",
            prefix = "Found ${problems.size} BuildMeta problem(s):\n",
        ) { "  - $it" },
    )
}
