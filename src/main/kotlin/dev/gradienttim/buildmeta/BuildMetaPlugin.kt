package dev.gradienttim.buildmeta

import dev.gradienttim.buildmeta.codegen.generators.BuildMetaGenerator
import dev.gradienttim.buildmeta.codegen.generators.JavaBuildMetaGenerator
import dev.gradienttim.buildmeta.codegen.generators.KotlinJvmBuildMetaGenerator
import dev.gradienttim.buildmeta.codegen.generators.KotlinMultiplatformBuildMetaGenerator
import dev.gradienttim.buildmeta.meta.BuildMeta
import dev.gradienttim.buildmeta.meta.FieldMeta
import dev.gradienttim.buildmeta.placeholders.PlaceholderAction
import dev.gradienttim.buildmeta.placeholders.PlaceholdersExtension
import dev.gradienttim.buildmeta.tasks.GenerateBuildMetaTask
import dev.gradienttim.buildmeta.tasks.ResolveBuildMetaTask
import dev.gradienttim.buildmeta.tasks.findOutputConflicts
import org.gradle.api.*
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.withGroovyBuilder
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.language.jvm.tasks.ProcessResources

public class BuildMetaPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("buildMeta", BuildMetaExtension::class.java)
        val language = target.objects.property(Language::class.java)
        val resolvedLanguage = language.orElse(
            target.provider<Language> {
                throw GradleException(
                    "dev.gradienttim.buildmeta requires the '$JAVA_PLUGIN_ID', '$KOTLIN_JVM_PLUGIN_ID', " +
                            "or '$KOTLIN_MULTIPLATFORM_PLUGIN_ID' plugin to also be applied",
                )
            },
        )

        val resolveTask = target.tasks.register(RESOLVE_TASK_NAME, ResolveBuildMetaTask::class.java) {
            group = TASK_GROUP
            description = "Resolves the field values of all BuildMetas and checks them for problems."
            resolution.set(
                resolvedLanguage.flatMap { language ->
                    val outputs = extension.buildMetas.map { it.output(extension) }
                    resolveValues(target, outputs).map { values ->
                        val resolution = language.generator.resolve(outputs, values)
                        resolution.copy(problems = findOutputConflicts(outputs) + resolution.problems)
                    }
                },
            )
            resolutionFile.set(target.layout.buildDirectory.file("buildmeta/resolution.bin"))
        }
        val resolutionFile = resolveTask.flatMap { it.resolutionFile }

        val generateTask = target.tasks.register(GENERATE_TASK_NAME) {
            group = TASK_GROUP
            description = "Generates the sources of all BuildMetas."
        }

        extension.buildMetas.all {
            val buildMeta = this
            fileName.convention(extension.fallbackFileName)
            packageName.convention(extension.fallbackPackageName)
            fieldNamingStrategy.convention(extension.fallbackFieldNamingStrategy)

            val generateBuildMetaTask =
                target.tasks.register(generateTaskName(buildMeta), GenerateBuildMetaTask::class.java) {
                    group = TASK_GROUP
                    description = "Generates the source of the '${buildMeta.name}' BuildMeta."
                    this.resolutionFile.set(resolutionFile)
                    buildMetaName.set(buildMeta.name)
                    outputDirectory.set(
                        target.layout.buildDirectory.dir(
                            resolvedLanguage.map { "generated/buildmeta/${it.directoryName}/${buildMeta.name}" },
                        ),
                    )
                }

            generateTask.configure {
                dependsOn(generateBuildMetaTask)
            }
        }

        target.plugins.withType(LifecycleBasePlugin::class.java) {
            target.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
                dependsOn(resolveTask)
            }
        }

        target.pluginManager.withPlugin(KOTLIN_JVM_PLUGIN_ID) {
            language.set(Language.KOTLIN_JVM)
            addGeneratedSources(extension, target, kotlinSourceDirectories(target, "main"))
        }

        target.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
            language.set(Language.KOTLIN_MULTIPLATFORM)
            addGeneratedSources(extension, target, kotlinSourceDirectories(target, "commonMain"))
            generateOnIdeSync(target, generateTask)

            target.tasks.withType(ProcessResources::class.java).configureEach {
                if (name.endsWith(KOTLIN_PROCESS_RESOURCES_SUFFIX) &&
                    !name.endsWith(KOTLIN_TEST_PROCESS_RESOURCES_SUFFIX)
                ) {
                    replacePlaceholders(this, extension.placeholders, resolutionFile)
                }
            }
        }

        target.pluginManager.withPlugin(JAVA_PLUGIN_ID) {
            language.convention(Language.JAVA)

            val mainSourceSet = target.extensions
                .getByType(JavaPluginExtension::class.java)
                .sourceSets
                .getByName("main")
            val sourceDirectories = mainSourceSet.java
            val empty: Provider<Any> = target.provider { emptyList<Any>() }

            extension.buildMetas.all {
                val outputDirectory = generatedDirectory(target, this)
                sourceDirectories.srcDir(
                    language.flatMap { if (it == Language.JAVA) outputDirectory else empty },
                )
            }

            target.tasks.named(mainSourceSet.processResourcesTaskName, ProcessResources::class.java).configure {
                replacePlaceholders(this, extension.placeholders, resolutionFile)
            }
        }
    }

    private fun resolveValues(
        project: Project,
        outputs: List<BuildMeta.Output>,
    ): Provider<List<List<Any?>>> {
        val values = project.objects.listProperty(FieldMeta.ResolvedValue::class.java)
        for (output in outputs) {
            for (field in output.fields) {
                values.add(field.resolvedValue())
            }
        }

        val sizes = outputs.map { it.fields.size }
        return values.map { resolved ->
            var offset = 0
            sizes.map { size -> resolved.subList(offset, offset + size).map { it.value }.also { offset += size } }
        }
    }

    private fun replacePlaceholders(
        task: ProcessResources,
        placeholders: PlaceholdersExtension,
        resolutionFile: Provider<RegularFile>,
    ) {
        task.inputs
            .file(resolutionFile)
            .withPropertyName("buildMetaResolution")
            .withPathSensitivity(PathSensitivity.NONE)
        task.inputs.property("buildMetaPlaceholderPrefix", placeholders.prefix)
        task.inputs.property("buildMetaPlaceholderSuffix", placeholders.suffix)
        task.inputs.property("buildMetaPlaceholderIncludes", placeholders.includes)
        task.inputs.property("buildMetaPlaceholderExcludes", placeholders.excludes)
        task.eachFile(
            PlaceholderAction(
                resolutionFile = resolutionFile,
                prefix = placeholders.prefix,
                suffix = placeholders.suffix,
                includes = placeholders.includes,
                excludes = placeholders.excludes,
            ),
        )
    }

    private fun kotlinSourceDirectories(
        project: Project,
        sourceSet: String,
    ): SourceDirectorySet {
        val sourceSets = project.extensions
            .getByName("kotlin")
            .withGroovyBuilder { getProperty("sourceSets") } as NamedDomainObjectContainer<*>

        return sourceSets
            .getByName(sourceSet)
            .withGroovyBuilder { getProperty("kotlin") } as SourceDirectorySet
    }

    private fun generateOnIdeSync(
        project: Project,
        generateTask: TaskProvider<Task>,
    ) {
        project.tasks.named { it == KOTLIN_IDE_SYNC_TASK_NAME }.configureEach {
            dependsOn(generateTask)
        }
    }

    private fun addGeneratedSources(
        extension: BuildMetaExtension,
        project: Project,
        sourceDirectories: SourceDirectorySet,
    ) {
        extension.buildMetas.all {
            sourceDirectories.srcDir(generatedDirectory(project, this))
        }
    }

    private fun generatedDirectory(
        project: Project,
        buildMeta: BuildMeta,
    ): Provider<Directory> =
        project.tasks
            .named(generateTaskName(buildMeta), GenerateBuildMetaTask::class.java)
            .flatMap { it.outputDirectory }

    private fun generateTaskName(buildMeta: BuildMeta): String =
        "generate${buildMeta.name.replaceFirstChar(Char::uppercaseChar)}BuildMeta"

    private enum class Language(
        val generator: BuildMetaGenerator,
        val directoryName: String,
    ) {
        JAVA(JavaBuildMetaGenerator, "java"),
        KOTLIN_JVM(KotlinJvmBuildMetaGenerator, "kotlin"),
        KOTLIN_MULTIPLATFORM(KotlinMultiplatformBuildMetaGenerator, "kotlin"),
    }

    private companion object {
        const val TASK_GROUP = "buildmeta"
        const val GENERATE_TASK_NAME = "generateBuildMeta"
        const val RESOLVE_TASK_NAME = "resolveBuildMeta"
        const val KOTLIN_PROCESS_RESOURCES_SUFFIX = "ProcessResources"
        const val KOTLIN_TEST_PROCESS_RESOURCES_SUFFIX = "TestProcessResources"
        const val JAVA_PLUGIN_ID = "java"
        const val KOTLIN_IDE_SYNC_TASK_NAME = "prepareKotlinIdeaImport"
        const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"
        const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
    }
}
