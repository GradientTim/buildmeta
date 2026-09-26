package dev.gradienttim.buildmeta.value

/**
 * Represents a valid Kotlin identifier used as a field name.
 *
 * This class enforces that the field name is not blank and adheres to the rules of a valid Kotlin
 * identifier (must start with a letter or underscore, followed by letters, numbers, or underscores).
 *
 * Field names are immutable and can be used across contexts where valid identifier validation is required.
 *
 * @property value The actual field name as a string. Must be a valid Kotlin identifier.
 * @throws IllegalArgumentException If the input string is blank or not a valid Kotlin identifier.
 */
@JvmInline
public value class FieldName(public val value: String) {
    init {
        require(value.isNotBlank()) { "Field name must not be blank" }
        require(IDENTIFIER_REGEX.matches(value)) { "Field name '$value' is not a valid Kotlin identifier" }
    }

    public override fun toString(): String = value
}
