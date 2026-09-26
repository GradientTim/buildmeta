package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import java.util.*

internal object JavaUuidDataType : DataType<UUID>() {
    override val classType: Class<UUID> = UUID::class.java
    override val needsImport: Boolean = true

    override fun format(value: UUID): String = "UUID.fromString(\"$value\")"
}
