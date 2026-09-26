package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType
import kotlin.time.Instant

internal object InstantDataType : DataType<Instant>() {
    override val classType: Class<Instant> = Instant::class.java
    override val needsImport: Boolean = true
    override val optIns: Set<String> = setOf("kotlin.time.ExperimentalTime")

    override fun format(value: Instant): String = "Instant.parse(\"$value\")"
}
