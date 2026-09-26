package dev.gradienttim.buildmeta.tasks

import dev.gradienttim.buildmeta.codegen.output.Resolution
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Writing a few small source files is cheaper than a build cache lookup")
internal abstract class GenerateBuildMetaTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val resolutionFile: RegularFileProperty

    @get:Input
    abstract val buildMetaName: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val name = buildMetaName.get()
        val sourceFile = Resolution
            .read(resolutionFile.get().asFile)
            .buildMetas
            .first { it.name == name }
            .sourceFile

        val outputDir = outputDirectory.get().asFile
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        if (sourceFile == null) return

        val file = outputDir.resolve(sourceFile.relativePath)
        file.parentFile.mkdirs()
        file.writeText(sourceFile.content)
    }
}
