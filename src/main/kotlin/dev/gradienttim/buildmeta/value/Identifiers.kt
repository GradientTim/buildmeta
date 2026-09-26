package dev.gradienttim.buildmeta.value

/**
 * Matches a valid Kotlin identifier: starts with a letter or underscore, followed by
 * letters, digits, or underscores. Shared by [FieldName] and [PackageName], whose
 * segments both need to be usable as-is in a generated source.
 */
internal val IDENTIFIER_REGEX = Regex("^[A-Za-z_][A-Za-z0-9_]*$")
