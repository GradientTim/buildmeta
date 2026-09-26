@file:Suppress("SpellCheckingInspection")

package dev.gradienttim.buildmeta.codegen.generators

import dev.gradienttim.buildmeta.codegen.CodeWriter
import dev.gradienttim.buildmeta.codegen.RenderedField
import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.kotlin.*
import dev.gradienttim.buildmeta.meta.BuildMeta
import dev.gradienttim.buildmeta.meta.FieldMeta

internal abstract class KotlinBuildMetaGenerator : BuildMetaGenerator() {
    override val reservedKeywords: Set<String> = setOf(
        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when",
        "while",
    )

    protected val kotlinDataTypes: List<DataType<*>> = listOf(
        StringDataType,
        CharDataType,
        IntDataType,
        LongDataType,
        DoubleDataType,
        FloatDataType,
        BooleanDataType,
        UuidDataType,
        DurationDataType,
        InstantDataType,
    )

    override val fileExtension: String = "kt"
    override val statementTerminator: String = ""

    override fun isReservedName(name: String): Boolean = super.isReservedName(name) || name.all { it == '_' }

    override fun fileAnnotations(fields: List<RenderedField>): List<String> {
        val optIns = fields.flatMapTo(sortedSetOf()) { it.optIns }
        val suppress = "@file:Suppress(\"all\", \"ktlint\")"
        if (optIns.isEmpty()) return listOf(suppress)
        return listOf(suppress, "@file:OptIn(${optIns.joinToString { "$it::class" }})")
    }

    override fun typeName(classType: Class<*>): String =
        requireNotNull(classType.kotlin.simpleName) {
            "Type adapter for '${classType.name}' has no simple name"
        }

    override fun renderType(
        output: BuildMeta.Output,
        fields: List<RenderedField>,
    ): String = CodeWriter.write(output.indent) {
        block("public object ${output.fileName}") {
            for (field in fields) {
                line(field.toDeclaration())
            }
        }
    }

    private fun RenderedField.toDeclaration(): String {
        val const = if (constant) "const " else ""
        return "${visibility.toKotlinModifier()} ${const}val $name: $type = $literal"
    }

    private fun FieldMeta.Visibility.toKotlinModifier(): String = when (this) {
        FieldMeta.Visibility.PUBLIC -> "public"
        FieldMeta.Visibility.INTERNAL -> "internal"
    }
}
