package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType

internal object LongDataType : DataType<Long>() {
    override val classType: Class<Long> = Long::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: Long): String = if (value == Long.MIN_VALUE) "Long.MIN_VALUE" else "${value}L"
}
