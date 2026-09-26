package dev.gradienttim.buildmeta.codegen.generators

import dev.gradienttim.buildmeta.codegen.CodeWriter
import dev.gradienttim.buildmeta.codegen.RenderedField
import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.java.*
import dev.gradienttim.buildmeta.codegen.data.kotlin.*
import dev.gradienttim.buildmeta.meta.BuildMeta
import dev.gradienttim.buildmeta.meta.FieldMeta

internal object JavaBuildMetaGenerator : BuildMetaGenerator() {
    override val reservedKeywords: Set<String> = setOf(
        "_",
        "abstract",
        "assert",
        "boolean",
        "break",
        "byte",
        "case",
        "catch",
        "char",
        "class",
        "const",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "enum",
        "extends",
        "false",
        "final",
        "finally",
        "float",
        "for",
        "goto",
        "if",
        "implements",
        "import",
        "instanceof",
        "int",
        "interface",
        "long",
        "native",
        "new",
        "null",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "short",
        "static",
        "strictfp",
        "super",
        "switch",
        "synchronized",
        "this",
        "throw",
        "throws",
        "transient",
        "true",
        "try",
        "void",
        "volatile",
        "while",
    )

    override val reservedTypeNames: Set<String> = setOf(
        "permits",
        "record",
        "sealed",
        "var",
        "yield",
    )

    override val dataTypes: List<DataType<*>> = listOf(
        JavaStringDataType,
        JavaCharDataType,
        IntDataType,
        LongDataType,
        DoubleDataType,
        FloatDataType,
        BooleanDataType,
        JavaUuidDataType,
        JavaDurationDataType,
        JavaInstantDataType,
        LocalDateDataType,
        LocalTimeDataType,
        LocalDateTimeDataType,
        OffsetDateTimeDataType,
        ZonedDateTimeDataType,
    )

    override val fileExtension: String = "java"
    override val statementTerminator: String = ";"

    override fun typeName(classType: Class<*>): String = classType.simpleName

    override fun renderType(
        output: BuildMeta.Output,
        fields: List<RenderedField>,
    ): String = CodeWriter.write(output.indent) {
        val className = output.fileName
        line("@SuppressWarnings(\"all\")")
        block("public final class $className") {
            for (field in fields) {
                line(field.toDeclaration())
            }
            if (fields.isNotEmpty()) {
                line()
            }
            block("private $className()")
        }
    }

    private fun RenderedField.toDeclaration(): String {
        val modifiers = listOfNotNull(visibility.toJavaModifier(), "static", "final").joinToString(" ")
        return "$modifiers $type $name = $literal$statementTerminator"
    }

    private fun FieldMeta.Visibility.toJavaModifier(): String? = when (this) {
        FieldMeta.Visibility.PUBLIC -> "public"
        FieldMeta.Visibility.INTERNAL -> null
    }
}
