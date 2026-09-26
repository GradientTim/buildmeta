package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import java.time.LocalTime

internal object LocalTimeDataType : DataType<LocalTime>() {
    override val classType: Class<LocalTime> = LocalTime::class.java
    override val needsImport: Boolean = true

    override fun format(value: LocalTime): String = "LocalTime.parse(\"$value\")"
}
