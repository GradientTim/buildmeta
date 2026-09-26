package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import java.time.OffsetDateTime

internal object OffsetDateTimeDataType : DataType<OffsetDateTime>() {
    override val classType: Class<OffsetDateTime> = OffsetDateTime::class.java
    override val needsImport: Boolean = true

    override fun format(value: OffsetDateTime): String = "OffsetDateTime.parse(\"$value\")"
}
