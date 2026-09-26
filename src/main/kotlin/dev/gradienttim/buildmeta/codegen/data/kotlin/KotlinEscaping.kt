package dev.gradienttim.buildmeta.codegen.data.kotlin

internal fun String.escapeKotlin(quote: Char): String =
    buildString {
        for (char in this@escapeKotlin) {
            when (char) {
                '\\' -> append("\\\\")
                quote -> append('\\').append(quote)
                '$' -> append("\\\$")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\b' -> append("\\b")
                else -> if (char.isISOControl()) append("\\u%04x".format(char.code)) else append(char)
            }
        }
    }
