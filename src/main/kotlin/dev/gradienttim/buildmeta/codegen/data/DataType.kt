package dev.gradienttim.buildmeta.codegen.data

/**
 * Abstract representation of a data type used for formatting and metadata handling.
 *
 * This class defines the structure for managing type-specific formatting behavior
 * and related metadata such as class type and import requirements. It serves as a
 * base for creating custom data type handlers that can be used in code generation
 * or similar scenarios.
 *
 * @param T The type of data represented by this DataType.
 */
public abstract class DataType<T : Any> {
    /**
     * Represents the runtime class of the data type handled by the implementing `DataType`.
     *
     * This property identifies the Java `Class` object that corresponds to the generic type parameter `T`
     * of the `DataType`. It is used for type identification, registration, and compatibility checks
     * within the broader type-handling framework.
     *
     * It is particularly useful when ensuring uniqueness of type adapters, validating
     * data type compatibility, and determining type-specific metadata or behavior.
     */
    public abstract val classType: Class<T>

    /**
     * Indicates whether the data type represented by this `DataType` requires an import statement
     * in the generated code.
     *
     * This property determines whether the fully qualified class name of the data type should be included
     * within an `import` statement during code generation. It is often used to ensure that references
     * to the data type remain appropriately scoped and avoid unnecessary verbosity in the generated output.
     */
    public abstract val needsImport: Boolean

    /**
     * Determines whether the generated field associated with a data type is declared as a constant.
     *
     * When set to `true`, the field is generated as a constant (e.g., `const val` in Kotlin),
     * which requires the field to contain a compile-time constant value. If `false`, the field
     * is generated as a regular property (e.g., `val` in Kotlin).
     *
     * This property is used when rendering fields, specifying whether a given field should
     * adhere to restrictions and requirements for constant values in the target language.
     */
    public open val constant: Boolean = false

    /**
     * Represents a set of opt-in annotations or requirements associated with the data type. These opt-ins
     * are used to specify features, behaviors, or experimental APIs that consumers of the data type should
     * acknowledge or enable explicitly.
     *
     * Subclasses can override this property to provide specific opt-in requirements based on their
     * implementation details.
     */
    public open val optIns: Set<String> = emptySet()

    public open val typeName: String? = null

    /**
     * Formats the given value into a string representation specific to the data type.
     *
     * @param value The value to format.
     * @return The formatted string representation of the value.
     */
    public abstract fun format(value: T): String

    /**
     * Converts the given value into its string representation.
     *
     * @param value The value to be formatted.
     * @return A string representation of the provided value.
     */
    public open fun formatText(value: T): String = value.toString()
}
