package dev.gradienttim.buildmeta.meta

import dev.gradienttim.buildmeta.BuildMetaExtension
import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.value.FieldName
import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

/**
 * Represents a specification for building structured field configurations.
 *
 * This class allows managing and configuring various fields, file names, package names,
 * and naming strategies in the context of a build process. It provides support for
 * validating field names, registering new fields, and applying configurable strategies
 * for field naming conventions or packaging rules.
 *
 * The primary components of a `BuildMeta` include:
 * - A list of registered fields.
 * - Configurable properties for the file name, package name, and field naming strategy.
 * - Methods for managing fields and applying naming or validation logic.
 *
 * All properties and methods ensure appropriate validation and enforce constraints
 * for valid Kotlin identifiers in the context of field names, package names, or other
 * configurable elements.
 */
public abstract class BuildMeta @Inject constructor(
    public val name: String,
    internal val providers: ProviderFactory,
) {
    /**
     * A list of field specifications that define the properties, their values, and associated options
     * for a build specification.
     *
     * Each entry in the list is represented by a [FieldMeta] instance, which contains a field name,
     * a value, and configurable options. The `fields` collection serves as the central store for all
     * field definitions within a build configuration.
     *
     * Modifications to the list should adhere to field definition rules, such as ensuring unique field
     * names within the containing context.
     */
    public val fields: List<FieldMeta<*>>
        field = mutableListOf<FieldMeta<*>>()

    /**
     * The name of the generated file and type. Must be a valid Kotlin identifier.
     *
     * Defaults to [BuildMetaExtension.fallbackFileName].
     */
    public abstract val fileName: Property<String>

    /**
     * The package of the generated source. Must consist of one or more dot-separated segments,
     * each a valid Kotlin identifier.
     *
     * Defaults to [BuildMetaExtension.fallbackPackageName].
     */
    public abstract val packageName: Property<String>

    /**
     * The naming strategy applied to fields that don't set their own via [FieldMeta.Options.namingStrategy].
     *
     * Defaults to [BuildMetaExtension.fallbackFieldNamingStrategy].
     */
    public abstract val fieldNamingStrategy: Property<FieldMeta.NamingStrategy>

    /**
     * Registers a new field with the specified name, value, and options.
     *
     * The field name must be unique within the context of the current instance.
     * The provided value is associated with the field, and its behavior can be customized
     * using the options provided.
     *
     * @param name The name of the field to register. Must be a valid Kotlin identifier.
     * @param value The value associated with the field.
     * @param options A block to configure additional options for the field, such as visibility and naming strategy.
     *                Defaults to an empty configuration.
     * @throws IllegalArgumentException If a field with the specified name is already registered.
     */
    @JvmOverloads
    public fun <TValue : Any> field(
        name: String,
        value: TValue?,
        options: Action<FieldMeta.Options> = Action {},
    ): Unit = field(name, providers.provider { value }, options)

    @JvmOverloads
    public fun <TValue : Any> field(
        name: String,
        value: Provider<TValue>,
        options: Action<FieldMeta.Options> = Action {},
    ) {
        val fieldName = FieldName(name)
        require(fields.none { it.name == fieldName }) {
            "Field '$fieldName' is already registered"
        }

        fields.add(
            FieldMeta(
                name = fieldName,
                value = value,
                options = FieldMeta.Options().also(options::execute),
            ),
        )
    }

    /**
     * Registers a new field with the specified name, a lazily evaluated value, and configuration options.
     *
     * The field name must be unique within the current context. The associated value is lazily evaluated
     * using the provided lambda, and additional configuration for the field can be specified using the `options` block.
     *
     * @param name The name of the field to register. Must be a valid Kotlin identifier.
     * @param value A lambda that provides the value associated with the field. The value is evaluated lazily.
     * @param options A block to configure additional options for the field, such as visibility and naming strategy.
     *                Defaults to an empty configuration.
     * @throws IllegalArgumentException If a field with the specified name is already registered.
     */
    @JvmOverloads
    public fun <TValue : Any> field(
        name: String,
        value: () -> TValue?,
        options: Action<FieldMeta.Options> = Action {},
    ): Unit = field(name, providers.provider(value), options)

    internal fun output(extension: BuildMetaExtension): Output =
        Output(
            name = name,
            fileName = fileName.get(),
            packageName = packageName.get(),
            fieldNamingStrategy = fieldNamingStrategy.get(),
            fields = fields.toList(),
            indent = extension.indent.get(),
            typeAdapters = extension.typeAdapters.dataTypes.toList(),
        )

    internal class Output(
        val name: String,
        val fileName: String,
        val packageName: String,
        val fieldNamingStrategy: FieldMeta.NamingStrategy,
        val fields: List<FieldMeta<*>>,
        val indent: Int,
        val typeAdapters: List<DataType<*>>,
    )

    public companion object {
        public const val DEFAULT_FILE_NAME: String = "BuildMeta"
        public const val DEFAULT_PACKAGE_NAME: String = "com.example.generated.buildmeta"
    }
}
