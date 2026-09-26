package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import java.time.Instant

internal object JavaInstantDataType : DataType<Instant>() {
    override val classType: Class<Instant> = Instant::class.java
    override val needsImport: Boolean = true

    override fun format(value: Instant): String = "Instant.parse(\"$value\")"
}
