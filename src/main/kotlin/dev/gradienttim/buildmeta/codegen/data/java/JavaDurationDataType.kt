package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import java.time.Duration

internal object JavaDurationDataType : DataType<Duration>() {
    override val classType: Class<Duration> = Duration::class.java
    override val needsImport: Boolean = true

    override fun format(value: Duration): String = "Duration.parse(\"$value\")"
}
