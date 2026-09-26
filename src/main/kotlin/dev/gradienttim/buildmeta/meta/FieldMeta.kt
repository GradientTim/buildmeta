package dev.gradienttim.buildmeta.meta

import dev.gradienttim.buildmeta.value.FieldName
import org.gradle.api.provider.Provider
import java.io.Serializable

/**
 * Represents the specification for a field with a name, value, and additional configuration options.
 *
 * This class models the structure of a field by defining its name, associated value, and
 * a set of customizable options that influence its behavior or representation. It provides
 * a mechanism to encapsulate field data and configuration in one cohesive unit.
 *
 * @param TValue The type of the value associated with the field.
 * @property name The name of the field, ensuring it adheres to valid Kotlin identifier rules.
 * @property value The value associated with the field.
 * @property options The configuration options for the field, such as its visibility and naming strategy.
 */
public class FieldMeta<TValue : Any> internal constructor(
    public val name: FieldName,
    public val value: Provider<TValue>,
    public val options: Options,
) {
    internal fun resolvedValue(): Provider<ResolvedValue> = value.map { ResolvedValue(it) }.orElse(ResolvedValue(null))

    internal class ResolvedValue(val value: Any?) : Serializable

    /**
     * Represents the visibility level of a field or element.
     *
     * This enumeration defines the available visibility options that can be applied to a field
     * or configuration element, determining its scope and accessibility within a generated
     * output or a build specification.
     *
     * - `PUBLIC`: Indicates the field or element is publicly accessible, allowing visibility
     *             across different modules or packages.
     * - `INTERNAL`: In Kotlin, limits visibility to the current module. In Java, which has no module
     *               visibility, the field is package-private and only accessible from the generated
     *               class's package.
     */
    public enum class Visibility {
        PUBLIC,
        INTERNAL,
    }

    /**
     * Defines strategies for transforming field names into different naming conventions.
     *
     * This enum provides multiple implementations for naming strategies that can be used
     * when generating or processing field names. Each strategy transforms the input
     * field name in a specific manner based on the rules it implements.
     *
     * Strategies include:
     * - `KEEP`: Retains the original field name without modifications.
     * - `SCREAMING_SNAKE_CASE`: Converts the field name to uppercase and separates words
     *   with underscores, following the SCREAMING_SNAKE_CASE convention.
     */
    public enum class NamingStrategy {
        KEEP,
        SCREAMING_SNAKE_CASE,
        ;

        public fun apply(name: FieldName): String = when (this) {
            KEEP -> name.value
            SCREAMING_SNAKE_CASE -> toScreamingSnakeCase(name.value)
        }

        private fun toScreamingSnakeCase(value: String): String =
            buildString {
                for (index in value.indices) {
                    val char = value[index]

                    if (index > 0 && char.isUpperCase() && startsWord(value, index)) {
                        append('_')
                    }

                    append(char.uppercaseChar())
                }
            }

        private fun startsWord(
            value: String,
            index: Int,
        ): Boolean {
            val previous = value[index - 1]
            val next = value.getOrNull(index + 1)
            return previous.isLowerCase() ||
                    previous.isDigit() ||
                    (previous.isUpperCase() && next?.isLowerCase() == true)
        }
    }

    public class Options {
        /**
         * Specifies the visibility level of the generated or processed field.
         *
         * This property determines the accessibility of the field based on the [Visibility] enumeration,
         * which includes `PUBLIC` and `INTERNAL`. The default visibility is `PUBLIC`.
         *
         * Modifying this property allows customization of the field's visibility in the context of source generation
         * or processing logic. It is commonly used to control access restrictions for generated code elements.
         */
        public var visibility: Visibility = Visibility.PUBLIC

        /**
         * Defines the naming strategy to be used for transforming field names into specific naming conventions.
         *
         * This property allows customization of how field names are processed during generation or transformation.
         * The naming strategy is represented by the [NamingStrategy] enum, which provides various methods for
         * formatting field names, such as retaining the original name or converting it into a standardized
         * format like SCREAMING_SNAKE_CASE.
         *
         * If set to `null`, the field uses the naming strategy of its [dev.gradienttim.buildmeta.meta.BuildMeta],
         * which in turn defaults to the extension's fallback naming strategy.
         */
        public var namingStrategy: NamingStrategy? = null
    }
}
