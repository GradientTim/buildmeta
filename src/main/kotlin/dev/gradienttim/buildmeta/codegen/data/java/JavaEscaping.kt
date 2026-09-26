package dev.gradienttim.buildmeta.codegen.data.java

internal fun String.escapeJava(quote: Char): String = buildString {
    for (char in this@escapeJava) {
        when (char) {
            '\\' -> append("\\\\")
            quote -> append('\\').append(quote)
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            '\b' -> append("\\b")
            else -> if (char.isISOControl()) append("\\%03o".format(char.code)) else append(char)
        }
    }
}
