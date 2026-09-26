package dev.gradienttim.buildmeta.value

/**
 * Represents a valid Kotlin package name used for the generated source.
 *
 * This class enforces that the package name is not blank and consists of one or more
 * dot-separated segments, each of which is itself a valid Kotlin identifier. (Must start
 * with a letter or underscore, followed by letters, numbers, or underscores.)
 *
 * Package names are immutable and can be used across contexts where valid package
 * name validation is required.
 *
 * @property value The actual package name as a string. Must be a valid Kotlin package name.
 * @throws IllegalArgumentException If the input string is blank or contains an invalid segment.
 */
@JvmInline
internal value class PackageName(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Package name must not be blank" }

        for (segment in value.split('.')) {
            require(IDENTIFIER_REGEX.matches(segment)) {
                "Package name '$value' contains an invalid segment: '$segment'"
            }
        }
    }

    override fun toString(): String = value
}
