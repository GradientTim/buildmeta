@file:Suppress("SpellCheckingInspection")

package dev.gradienttim.buildmeta.placeholders

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

public abstract class PlaceholdersExtension {
    public abstract val prefix: Property<String>

    public abstract val suffix: Property<String>

    public abstract val includes: SetProperty<String>

    public abstract val excludes: SetProperty<String>

    init {
        prefix.convention(DEFAULT_PREFIX)
        suffix.convention(DEFAULT_SUFFIX)
    }

    public fun include(vararg patterns: String) {
        includes.addAll(*patterns)
    }

    public fun exclude(vararg patterns: String) {
        excludes.addAll(*patterns)
    }

    public companion object {
        public const val DEFAULT_PREFIX: String = "{{buildMeta:"
        public const val DEFAULT_SUFFIX: String = "}}"

        internal val ALWAYS_EXCLUDED: List<String> =
            listOf(
                "png", "jpg", "jpeg", "gif", "bmp", "ico", "icns", "webp", "tif", "tiff", "psd", "avif", "heic", "svgz",
                "ttf", "otf", "woff", "woff2", "eot",
                "jar", "war", "ear", "aar", "zip", "gz", "tgz", "tar", "bz2", "xz", "zst", "7z", "rar",
                "class", "dex", "so", "dll", "dylib", "exe", "bin", "dat", "wasm", "o", "a", "lib",
                "jks", "keystore", "p12", "pfx", "der", "db", "sqlite",
                "mp3", "wav", "ogg", "flac", "aac", "m4a", "mp4", "mov", "avi", "mkv", "webm",
                "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt", "ods", "odp",
            ).map { "**/*.$it" }
    }
}
