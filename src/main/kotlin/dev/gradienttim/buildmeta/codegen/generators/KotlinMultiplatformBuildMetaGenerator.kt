package dev.gradienttim.buildmeta.codegen.generators

import dev.gradienttim.buildmeta.codegen.data.DataType

internal object KotlinMultiplatformBuildMetaGenerator : KotlinBuildMetaGenerator() {
    override val dataTypes: List<DataType<*>> = kotlinDataTypes
}
