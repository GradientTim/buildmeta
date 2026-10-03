package dev.gradienttim.buildmeta.helpers

import dev.gradienttim.buildmeta.BuildMetaExtension
import dev.gradienttim.buildmeta.meta.FieldMeta
import org.gradle.api.Project
import org.gradle.api.provider.Provider

public fun BuildMetaExtension.registerProjectMeta(
    fileName: String? = "ProjectMeta",
    packageName: String? = null,
    fieldNamingStrategy: FieldMeta.NamingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE,
    useRootProjectFallback: Boolean = false,
    includeRootProject: Boolean = false,
) {
    register("projectMeta") {
        this.fileName.set(fileName)
        this.packageName.set(packageName)
        this.fieldNamingStrategy.set(fieldNamingStrategy)

        field("name", projectName)
        field("path", projectPath)
        field("group", projectGroup.withFallback(rootProjectGroup, useRootProjectFallback).orElse(projectDefaultGroup))
        field(
            "version",
            projectVersion.withFallback(rootProjectVersion, useRootProjectFallback).orElse(Project.DEFAULT_VERSION),
        )
        field("description", projectDescription.withFallback(rootProjectDescription, useRootProjectFallback).orElse(""))

        if (includeRootProject) {
            field("rootName", rootProjectName)
            field("rootGroup", rootProjectGroup.orElse(""))
            field("rootVersion", rootProjectVersion.orElse(Project.DEFAULT_VERSION))
            field("rootDescription", rootProjectDescription.orElse(""))
        }
    }
}

private fun Provider<String>.withFallback(
    fallback: Provider<String>,
    enabled: Boolean,
): Provider<String> = if (enabled) orElse(fallback) else this
