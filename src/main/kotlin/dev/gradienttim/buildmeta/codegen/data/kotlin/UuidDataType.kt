package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType
import kotlin.uuid.Uuid

internal object UuidDataType : DataType<Uuid>() {
    override val classType: Class<Uuid> = Uuid::class.java
    override val needsImport: Boolean = true
    override val optIns: Set<String> = setOf("kotlin.uuid.ExperimentalUuidApi")

    override fun format(value: Uuid): String = "Uuid.parse(\"$value\")"
}
