package dev.gradienttim.buildmeta.placeholders

import dev.gradienttim.buildmeta.BuildMetaExtension
import org.gradle.api.GradleException
import org.gradle.api.Transformer

internal class PlaceholderReplacer(
    private val prefix: String,
    private val suffix: String,
    private val values: Map<String, String>,
    private val path: String,
) : Transformer<String?, String> {
    private var lineNumber = 0

    override fun transform(line: String): String {
        lineNumber++
        return buildString {
            var index = 0
            while (true) {
                val start = line.indexOf(prefix, index)
                if (start == -1) {
                    append(line, index, line.length)
                    break
                }

                var escapes = 0
                while (start - escapes > index && line[start - escapes - 1] == ESCAPE) {
                    escapes++
                }

                if (escapes % 2 == 1) {
                    append(line, index, start - escapes)
                    repeat(escapes / 2) { append(ESCAPE) }
                    append(prefix)
                    index = start + prefix.length
                    continue
                }

                val end = line.indexOf(suffix, start + prefix.length)
                if (end == -1) {
                    append(line, index, line.length)
                    break
                }

                val key = line.substring(start + prefix.length, end).trim()
                append(line, index, start - escapes)
                repeat(escapes / 2) { append(ESCAPE) }
                append(lookup(key))
                index = end + suffix.length
            }
        }
    }

    private fun lookup(key: String): String {
        val qualifiedKey = if ('.' in key) key else "${BuildMetaExtension.MAIN_BUILD_META_NAME}.$key"
        return values[qualifiedKey] ?: throw GradleException(
            "Unknown placeholder '$prefix$key$suffix' in $path:$lineNumber. " +
                "Available: ${values.keys.sorted().joinToString()}",
        )
    }

    companion object {
        private const val ESCAPE = '\\'
    }
}
