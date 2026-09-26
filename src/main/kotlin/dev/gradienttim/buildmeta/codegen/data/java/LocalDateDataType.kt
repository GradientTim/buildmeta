package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import java.time.LocalDate

internal object LocalDateDataType : DataType<LocalDate>() {
    override val classType: Class<LocalDate> = LocalDate::class.java
    override val needsImport: Boolean = true

    override fun format(value: LocalDate): String = "LocalDate.parse(\"$value\")"
}
