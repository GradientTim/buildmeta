package dev.gradienttim.buildmeta.codegen.generators

import dev.gradienttim.buildmeta.codegen.CodeWriter
import dev.gradienttim.buildmeta.codegen.RenderedField
import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.output.Resolution
import dev.gradienttim.buildmeta.codegen.output.ResolvedBuildMeta
import dev.gradienttim.buildmeta.codegen.output.SourceFile
import dev.gradienttim.buildmeta.meta.BuildMeta
import dev.gradienttim.buildmeta.meta.FieldMeta
import dev.gradienttim.buildmeta.value.IDENTIFIER_REGEX
import dev.gradienttim.buildmeta.value.PackageName

internal abstract class BuildMetaGenerator {
    protected abstract val reservedKeywords: Set<String>

    protected open val reservedTypeNames: Set<String> = emptySet()

    protected abstract val dataTypes: List<DataType<*>>

    protected abstract val fileExtension: String

    protected abstract val statementTerminator: String

    protected abstract fun typeName(classType: Class<*>): String

    protected open fun isReservedName(name: String): Boolean = name in reservedKeywords

    protected open fun fileAnnotations(fields: List<RenderedField>): List<String> = emptyList()

    protected abstract fun renderType(
        output: BuildMeta.Output,
        fields: List<RenderedField>,
    ): String

    fun resolve(
        outputs: List<BuildMeta.Output>,
        values: List<List<Any?>>,
    ): Resolution {
        val resolved = outputs.zip(values) { output, outputValues -> resolveBuildMeta(output, outputValues) }
        return Resolution(
            buildMetas = resolved.map { it.first },
            problems = resolved.flatMap { it.second }.distinct(),
        )
    }

    private fun resolveBuildMeta(
        output: BuildMeta.Output,
        values: List<Any?>,
    ): Pair<ResolvedBuildMeta, List<String>> {
        if (output.fields.isEmpty()) {
            return ResolvedBuildMeta(output.name, null, emptyMap()) to emptyList()
        }

        val resolution = resolveFields(output, values)
        val sourceFile = if (resolution.problems.isEmpty()) render(output, resolution.fields) else null
        val resolvedValues = resolution.fields.associate { it.declaredName to it.text }
        return ResolvedBuildMeta(output.name, sourceFile, resolvedValues) to resolution.problems
    }

    private fun render(
        output: BuildMeta.Output,
        fields: List<RenderedField>,
    ): SourceFile {
        val imports = fields.mapNotNullTo(sortedSetOf()) { it.import }
        val annotations = fileAnnotations(fields)

        val header = CodeWriter.write(output.indent) {
            line(GENERATED_HEADER)
            line()
            if (annotations.isNotEmpty()) {
                for (annotation in annotations) {
                    line(annotation)
                }
                line()
            }
            line("package ${output.packageName}$statementTerminator")
            line()
            if (imports.isNotEmpty()) {
                for (import in imports) {
                    line("import $import$statementTerminator")
                }
                line()
            }
        }

        return SourceFile(
            packageName = output.packageName,
            fileName = "${output.fileName}.$fileExtension",
            content = header + renderType(output, fields),
        )
    }

    private fun resolveFields(
        output: BuildMeta.Output,
        values: List<Any?>,
    ): FieldResolution {
        val problems = mutableListOf<String>()

        if (output.indent <= 0) {
            problems += "indent must be a positive integer, but was ${output.indent}"
        }

        val fileName = output.fileName
        try {
            PackageName(output.packageName)
        } catch (exception: IllegalArgumentException) {
            problems += "BuildMeta '${output.name}': ${exception.message}"
        }
        if (!IDENTIFIER_REGEX.matches(fileName)) {
            problems += "BuildMeta '${output.name}': file name '$fileName' is not a valid identifier"
        } else if (isReservedName(fileName)) {
            problems += "BuildMeta '${output.name}': file name '$fileName' is a reserved name"
        } else if (fileName in reservedTypeNames) {
            problems += "BuildMeta '${output.name}': file name '$fileName' is not allowed as a type name"
        }

        val fields = output.fields.zip(values).mapNotNull { (meta, value) -> renderField(meta, value, output, problems) }

        fields
            .groupBy { it.name }
            .filterValues { it.size > 1 }
            .keys
            .forEach { name ->
                problems += "BuildMeta '${output.name}': multiple fields resolve to the name '$name'"
            }

        fields
            .mapNotNullTo(sortedSetOf()) { it.import }
            .groupBy { it.substringAfterLast('.') }
            .filterValues { it.size > 1 }
            .values
            .forEach { clashing ->
                problems += "BuildMeta '${output.name}' uses types with the same name: ${clashing.joinToString()}. " +
                        "Move these fields into separate BuildMetas."
            }

        return FieldResolution(fields, problems)
    }

    private fun renderField(
        meta: FieldMeta<*>,
        resolved: Any?,
        output: BuildMeta.Output,
        problems: MutableList<String>,
    ): RenderedField? {
        if (resolved == null) {
            problems += "BuildMeta '${output.name}': field '${meta.name}' resolved to null; " +
                    "generated fields must have a non-null value"
            return null
        }

        val dataType = findDataType(resolved, output.typeAdapters)
        if (dataType == null) {
            problems += "BuildMeta '${output.name}': field '${meta.name}' resolved to an unsupported type " +
                    "'${resolved::class.qualifiedName}'; register one in buildMeta { typeAdapters { } }"
            return null
        }

        val name = (meta.options.namingStrategy ?: output.fieldNamingStrategy).apply(meta.name)
        if (isReservedName(name)) {
            problems += "BuildMeta '${output.name}': field name '$name' is a reserved name"
            return null
        }

        return RenderedField(
            declaredName = meta.name.value,
            name = name,
            type = dataType.typeName ?: typeName(dataType.classType),
            literal = dataType.formatUnchecked(resolved),
            text = dataType.formatTextUnchecked(resolved),
            visibility = meta.options.visibility,
            constant = dataType.constant,
            import = dataType.classType.canonicalName.takeIf { dataType.needsImport },
            optIns = dataType.optIns,
        )
    }

    private fun findDataType(
        value: Any,
        typeAdapters: List<DataType<*>>,
    ): DataType<*>? =
        (typeAdapters + dataTypes).firstOrNull {
            it.classType.kotlin.javaObjectType.isInstance(value)
        }

    @Suppress("UNCHECKED_CAST")
    private fun DataType<*>.formatUnchecked(value: Any): String = (this as DataType<Any>).format(value)

    @Suppress("UNCHECKED_CAST")
    private fun DataType<*>.formatTextUnchecked(value: Any): String = (this as DataType<Any>).formatText(value)

    private class FieldResolution(
        val fields: List<RenderedField>,
        val problems: List<String>,
    )

    private companion object {
        const val GENERATED_HEADER = "// Generated by BuildMeta (dev.gradienttim.buildmeta). Do not edit."
    }
}
