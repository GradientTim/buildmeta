package dev.gradienttim.buildmeta

import dev.gradienttim.buildmeta.codegen.data.TypeAdapters
import dev.gradienttim.buildmeta.meta.BuildMeta
import dev.gradienttim.buildmeta.meta.FieldMeta
import dev.gradienttim.buildmeta.placeholders.PlaceholdersExtension
import dev.gradienttim.buildmeta.value.IDENTIFIER_REGEX
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

public abstract class BuildMetaExtension @Inject constructor(
    objects: ObjectFactory,
) {
    public abstract val fallbackFileName: Property<String>

    public abstract val fallbackPackageName: Property<String>

    public abstract val fallbackFieldNamingStrategy: Property<FieldMeta.NamingStrategy>

    /**
     * Controls the number of spaces used for indentation when generating build metadata files.
     *
     * Must be a positive integer. An invalid value fails the build when the files are generated.
     */
    public abstract val indent: Property<Int>

    public val buildMetas: NamedDomainObjectContainer<BuildMeta> =
        objects
            .domainObjectContainer(BuildMeta::class.java) { name ->
                require(IDENTIFIER_REGEX.matches(name)) {
                    "BuildMeta name '$name' is not a valid identifier. " +
                        "Use letters, digits and underscores, starting with a letter or underscore."
                }
                objects.newInstance(BuildMeta::class.java, name)
            }.apply {
                register(MAIN_BUILD_META_NAME)
            }

    public val typeAdapters: TypeAdapters = TypeAdapters()

    public val placeholders: PlaceholdersExtension = objects.newInstance(PlaceholdersExtension::class.java)

    init {
        fallbackFileName.convention(BuildMeta.DEFAULT_FILE_NAME)
        fallbackPackageName.convention(BuildMeta.DEFAULT_PACKAGE_NAME)
        fallbackFieldNamingStrategy.convention(FieldMeta.NamingStrategy.KEEP)
        indent.convention(4)
    }

    /**
     * Configures the main build metadata using the specified configuration logic.
     *
     * @param configure A lambda function that provides the configuration logic for the main build metadata.
     */
    public fun main(configure: Action<BuildMeta>) {
        buildMetas.named(MAIN_BUILD_META_NAME).configure(configure)
    }

    /**
     * Registers a new build metadata configuration with the given name and configuration logic.
     *
     * @param name The name of the build metadata to register. Must be unique within the context of the build metadata container.
     * @param configure A lambda function providing configuration logic for the registered build metadata.
     */
    public fun register(
        name: String,
        configure: Action<BuildMeta>,
    ) {
        buildMetas.register(name, configure)
    }

    /**
     * Configures the `TypeAdapters` instance using the specified configuration logic.
     *
     * @param configure A lambda function that provides the configuration logic
     * for the `TypeAdapters` instance. It allows registering or managing data type
     * adapters used within the build process.
     */
    public fun typeAdapters(configure: Action<TypeAdapters>) {
        configure.execute(typeAdapters)
    }

    /**
     * Configures the `PlaceholdersExtension` instance using the specified configuration logic.
     *
     * @param configure A lambda function that provides configuration logic
     * for the `PlaceholdersExtension`. It allows managing placeholder-related settings,
     * such as prefix, suffix, includes, and excludes.
     */
    public fun placeholders(configure: Action<PlaceholdersExtension>) {
        configure.execute(placeholders)
    }

    public companion object {
        public const val MAIN_BUILD_META_NAME: String = "main"
    }
}
