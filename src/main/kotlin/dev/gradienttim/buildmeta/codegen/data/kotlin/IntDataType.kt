package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType

internal object IntDataType : DataType<Int>() {
    override val classType: Class<Int> = Int::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: Int): String = value.toString()
}
