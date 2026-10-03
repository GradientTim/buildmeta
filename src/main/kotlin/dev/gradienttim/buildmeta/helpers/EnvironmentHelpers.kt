package dev.gradienttim.buildmeta.helpers

import dev.gradienttim.buildmeta.BuildMetaExtension
import dev.gradienttim.buildmeta.meta.FieldMeta
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.util.GradleVersion
import java.net.InetAddress

public fun BuildMetaExtension.registerEnvironmentMeta(
    fileName: String? = "EnvironmentMeta",
    packageName: String? = null,
    fieldNamingStrategy: FieldMeta.NamingStrategy = FieldMeta.NamingStrategy.SCREAMING_SNAKE_CASE,
    includeUser: Boolean = false,
    includeHostName: Boolean = false,
    includeTimestamp: Boolean = false,
) {
    register("environmentMeta") {
        this.fileName.set(fileName)
        this.packageName.set(packageName)
        this.fieldNamingStrategy.set(fieldNamingStrategy)

        field("gradleVersion", GradleVersion.current().version)
        field("javaVersion", javaVersion)
        field("kotlinVersion", kotlinVersion.orElse(""))
        field("osName", providers.systemProperty("os.name"))
        field("osArch", providers.systemProperty("os.arch"))

        if (includeUser) {
            field("buildUser", providers.systemProperty("user.name").orElse("unknown"))
        }

        if (includeHostName) {
            field("hostName", providers.of(HostNameValueSource::class.java) {})
        }

        if (includeTimestamp) {
            field(
                "buildTimestamp",
                providers
                    .environmentVariable("SOURCE_DATE_EPOCH")
                    .map { it.trim().toLongOrNull() }
                    .orElse(providers.of(EpochSecondsValueSource::class.java) {}),
            )
        }
    }
}

internal abstract class HostNameValueSource : ValueSource<String, ValueSourceParameters.None> {
    override fun obtain(): String = runCatching { InetAddress.getLocalHost().hostName }.getOrDefault("unknown")
}

internal abstract class EpochSecondsValueSource : ValueSource<Long, ValueSourceParameters.None> {
    override fun obtain(): Long = System.currentTimeMillis() / 1000
}
