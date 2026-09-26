package dev.gradienttim.buildmeta.placeholders

import dev.gradienttim.buildmeta.codegen.output.Resolution
import org.gradle.api.Action
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.FileTreeElement
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.util.PatternSet
import java.io.File

internal class PlaceholderAction(
    private val resolutionFile: Provider<RegularFile>,
    private val prefix: Provider<String>,
    private val suffix: Provider<String>,
    private val includes: Provider<Set<String>>,
    private val excludes: Provider<Set<String>>,
) : Action<FileCopyDetails> {
    private var values: Map<String, String>? = null
    private var spec: Spec<FileTreeElement>? = null

    override fun execute(details: FileCopyDetails) {
        if (!spec().isSatisfiedBy(details)) return

        val prefix = prefix.get()
        if (!details.file.containsText(prefix)) return

        details.filter(PlaceholderReplacer(prefix, suffix.get(), values(), details.path))
    }

    private fun values(): Map<String, String> =
        values ?: Resolution
            .read(resolutionFile.get().asFile)
            .buildMetas
            .flatMap { buildMeta -> buildMeta.values.map { (field, value) -> "${buildMeta.name}.$field" to value } }
            .toMap()
            .also { values = it }

    private fun spec(): Spec<FileTreeElement> =
        spec ?: run {
            val alwaysExcluded = PatternSet().apply {
                isCaseSensitive = false
                include(PlaceholdersExtension.ALWAYS_EXCLUDED)
            }.asSpec
            val configured = PatternSet()
                .include(includes.get())
                .exclude(excludes.get())
                .asSpec
            Spec<FileTreeElement> { !alwaysExcluded.isSatisfiedBy(it) && configured.isSatisfiedBy(it) }
        }.also { spec = it }

    private fun File.containsText(text: String): Boolean {
        val content = String(readBytes(), Charsets.ISO_8859_1)
        val needle = String(text.toByteArray(Charsets.UTF_8), Charsets.ISO_8859_1)
        return needle in content
    }
}
