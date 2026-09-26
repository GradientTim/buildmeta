package dev.gradienttim.buildmeta.codegen

internal class CodeWriter(
    private val indentUnit: String,
) {
    private val builder = StringBuilder()
    private var level = 0

    fun line(text: String = "") {
        if (text.isNotEmpty()) {
            repeat(level) { builder.append(indentUnit) }
        }
        builder.append(text).append('\n')
    }

    fun indent(body: CodeWriter.() -> Unit) {
        level++
        body()
        level--
    }

    fun block(
        header: String,
        body: CodeWriter.() -> Unit = {},
    ) {
        line("$header {")
        indent(body)
        line("}")
    }

    override fun toString(): String = builder.toString()

    companion object {
        fun write(
            indent: Int,
            body: CodeWriter.() -> Unit,
        ): String = CodeWriter(" ".repeat(indent)).apply(body).toString()
    }
}
