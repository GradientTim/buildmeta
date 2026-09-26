package dev.gradienttim.buildmeta.codegen.data

public class TypeAdapters {
    internal val dataTypes: List<DataType<*>>
        field = mutableListOf<DataType<*>>()

    /**
     * Registers a new data type for formatting and type handling.
     *
     * This method ensures that the provided data type is not already registered
     * before adding it to the registry. If a data type with the same class type
     * is already registered, an exception is thrown. Otherwise, the data type is
     * successfully added.
     *
     * @param T The type of data represented by the `DataType`.
     * @param dataType The `DataType` instance to be registered.
     * @throws IllegalArgumentException If a type adapter for the same class type
     *         is already registered.
     */
    public fun <T : Any> register(dataType: DataType<T>) {
        require(dataTypes.none { it.classType == dataType.classType }) {
            "A type adapter for '${dataType.classType.name}' is already registered"
        }
        dataTypes.add(dataType)
    }

    /**
     * Registers a new data type for formatting and type handling.
     *
     * This method simplifies the process of adding a new data type by automatically
     * creating a `LambdaDataType` instance using the provided parameters and subsequently
     * registering it. The data type ensures proper handling of formatting and metadata
     * and is uniquely identified by its class type.
     *
     * @param T The type of data represented by the `DataType`.
     * @param classType The `Class` instance representing the runtime type of the data being registered.
     * @param needsImport Specifies whether the data type requires an import statement in generated code. Defaults to `true`.
     * @param constant Indicates whether the generated field associated with the data type should be declared as a constant. Defaults to `false`.
     * @param format A lambda function that defines how to format instances of the data type into their string representation.
     * @throws IllegalArgumentException If a type adapter for the same class type is already registered.
     */
    @JvmOverloads
    public fun <T : Any> register(
        classType: Class<T>,
        needsImport: Boolean = true,
        constant: Boolean = false,
        typeName: String? = null,
        format: (T) -> String,
    ): Unit = register(LambdaDataType(classType, needsImport, constant, typeName, format))

    private class LambdaDataType<T : Any>(
        override val classType: Class<T>,
        override val needsImport: Boolean,
        override val constant: Boolean,
        override val typeName: String?,
        private val formatter: (T) -> String,
    ) : DataType<T>() {
        override fun format(value: T): String = formatter(value)
    }
}
