package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.Literals

internal object CharDataType : DataType<Char>() {
    override val classType: Class<Char> = Char::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: Char): String = Literals.kotlinChar(value)
}
