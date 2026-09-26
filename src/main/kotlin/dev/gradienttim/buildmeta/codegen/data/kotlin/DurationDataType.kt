package dev.gradienttim.buildmeta.codegen.data.kotlin

import dev.gradienttim.buildmeta.codegen.data.DataType
import kotlin.time.Duration

internal object DurationDataType : DataType<Duration>() {
    override val classType: Class<Duration> = Duration::class.java
    override val needsImport: Boolean = true

    override fun format(value: Duration): String = "Duration.parseIsoString(\"${value.toIsoString()}\")"
}
