package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.Literals

internal object StringDataType : DataType<String>() {
    override val classType: Class<String> = String::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: String): String = Literals.kotlinString(value)
}
