package dev.gradienttim.buildmeta.codegen

import dev.gradienttim.buildmeta.meta.FieldMeta

internal class RenderedField(
    val declaredName: String,
    val name: String,
    val type: String,
    val literal: String,
    val text: String,
    val visibility: FieldMeta.Visibility,
    val constant: Boolean,
    val import: String?,
    val optIns: Set<String>,
)
