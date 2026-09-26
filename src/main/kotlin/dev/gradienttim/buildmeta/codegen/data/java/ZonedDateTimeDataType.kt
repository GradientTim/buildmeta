package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import java.time.ZonedDateTime

internal object ZonedDateTimeDataType : DataType<ZonedDateTime>() {
    override val classType: Class<ZonedDateTime> = ZonedDateTime::class.java
    override val needsImport: Boolean = true

    override fun format(value: ZonedDateTime): String = "ZonedDateTime.parse(\"$value\")"
}
