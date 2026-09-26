package dev.gradienttim.buildmeta.codegen.data

import dev.gradienttim.buildmeta.codegen.data.java.escapeJava
import dev.gradienttim.buildmeta.codegen.data.kotlin.escapeKotlin

public object Literals {
    @JvmStatic
    public fun kotlinString(value: String): String = "\"${value.escapeKotlin('"')}\""

    @JvmStatic
    public fun javaString(value: String): String = "\"${value.escapeJava('"')}\""

    @JvmStatic
    public fun kotlinChar(value: Char): String = "'${value.toString().escapeKotlin('\'')}'"

    @JvmStatic
    public fun javaChar(value: Char): String = "'${value.toString().escapeJava('\'')}'"
}
