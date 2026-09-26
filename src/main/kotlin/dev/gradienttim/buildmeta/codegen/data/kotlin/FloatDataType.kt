package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType

internal object FloatDataType : DataType<Float>() {
    override val classType: Class<Float> = Float::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: Float): String = when {
        value.isNaN() -> "Float.NaN"
        value == Float.POSITIVE_INFINITY -> "Float.POSITIVE_INFINITY"
        value == Float.NEGATIVE_INFINITY -> "Float.NEGATIVE_INFINITY"
        else -> "${value}F"
    }
}
