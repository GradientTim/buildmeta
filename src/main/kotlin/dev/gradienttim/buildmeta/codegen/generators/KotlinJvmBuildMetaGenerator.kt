package dev.gradienttim.buildmeta.codegen.generators

import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.java.*

internal object KotlinJvmBuildMetaGenerator : KotlinBuildMetaGenerator() {
    override val dataTypes: List<DataType<*>> = kotlinDataTypes + listOf(
        JavaUuidDataType,
        JavaDurationDataType,
        JavaInstantDataType,
        LocalDateDataType,
        LocalTimeDataType,
        LocalDateTimeDataType,
        OffsetDateTimeDataType,
        ZonedDateTimeDataType,
    )
}
