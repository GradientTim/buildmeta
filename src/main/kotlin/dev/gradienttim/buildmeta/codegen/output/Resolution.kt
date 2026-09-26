package dev.gradienttim.buildmeta.codegen.output

import java.io.*

internal data class Resolution(
    val buildMetas: List<ResolvedBuildMeta>,
    val problems: List<String>,
) : Serializable {
    fun write(file: File) {
        file.parentFile.mkdirs()
        ObjectOutputStream(file.outputStream().buffered()).use { it.writeObject(this) }
    }

    companion object {
        fun read(file: File): Resolution =
            PluginObjectInputStream(file.inputStream().buffered()).use { it.readObject() as Resolution }
    }

    private class PluginObjectInputStream(
        input: InputStream,
    ) : ObjectInputStream(input) {
        override fun resolveClass(description: ObjectStreamClass): Class<*> =
            try {
                Class.forName(description.name, false, Resolution::class.java.classLoader)
            } catch (_: ClassNotFoundException) {
                super.resolveClass(description)
            }
    }
}

internal data class ResolvedBuildMeta(
    val name: String,
    val sourceFile: SourceFile?,
    val values: Map<String, String>,
) : Serializable
