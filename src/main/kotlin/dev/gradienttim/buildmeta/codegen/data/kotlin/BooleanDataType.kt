package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType

internal object BooleanDataType : DataType<Boolean>() {
    override val classType: Class<Boolean> = Boolean::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: Boolean): String = value.toString()
}
