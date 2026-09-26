package dev.gradienttim.buildmeta.tasks

import dev.gradienttim.buildmeta.meta.BuildMeta

internal fun findOutputConflicts(outputs: Collection<BuildMeta.Output>): List<String> =
    outputs
        .filter { it.fields.isNotEmpty() }
        .groupBy { "${it.packageName}.${it.fileName}" }
        .filterValues { it.size > 1 }
        .map { (type, conflicting) ->
            "BuildMetas ${conflicting.joinToString { "'${it.name}'" }} all generate '$type'. " +
                "Set a distinct fileName or packageName on one of them."
        }
