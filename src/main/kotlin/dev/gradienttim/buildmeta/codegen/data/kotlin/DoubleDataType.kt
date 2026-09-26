package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType

internal object DoubleDataType : DataType<Double>() {
    override val classType: Class<Double> = Double::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: Double): String =
        when {
            value.isNaN() -> "Double.NaN"
            value == Double.POSITIVE_INFINITY -> "Double.POSITIVE_INFINITY"
            value == Double.NEGATIVE_INFINITY -> "Double.NEGATIVE_INFINITY"
            else -> value.toString()
        }
}
