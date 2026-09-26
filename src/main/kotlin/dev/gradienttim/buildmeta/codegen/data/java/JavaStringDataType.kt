package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.Literals

internal object JavaStringDataType : DataType<String>() {
    override val classType: Class<String> = String::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: String): String = Literals.javaString(value)
}
