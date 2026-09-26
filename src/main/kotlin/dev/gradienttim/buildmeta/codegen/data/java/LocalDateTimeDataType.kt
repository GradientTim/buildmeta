package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import java.time.LocalDateTime

internal object LocalDateTimeDataType : DataType<LocalDateTime>() {
    override val classType: Class<LocalDateTime> = LocalDateTime::class.java
    override val needsImport: Boolean = true

    override fun format(value: LocalDateTime): String = "LocalDateTime.parse(\"$value\")"
}
