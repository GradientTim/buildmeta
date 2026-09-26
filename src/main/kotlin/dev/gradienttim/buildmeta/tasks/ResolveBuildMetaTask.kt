package dev.gradienttim.buildmeta.tasks

import dev.gradienttim.buildmeta.codegen.output.Resolution
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Writing the resolved values is cheaper than a build cache lookup")
internal abstract class ResolveBuildMetaTask : DefaultTask() {
    @get:Input
    abstract val resolution: Property<Resolution>

    @get:OutputFile
    abstract val resolutionFile: RegularFileProperty

    @TaskAction
    fun resolve() {
        val resolution = resolution.get()
        failOnProblems(resolution.problems)
        resolution.write(resolutionFile.get().asFile)
    }
}
