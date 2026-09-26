package dev.gradienttim.buildmeta.codegen.data.java

import dev.gradienttim.buildmeta.codegen.data.DataType
import dev.gradienttim.buildmeta.codegen.data.Literals

internal object JavaCharDataType : DataType<Char>() {
    override val classType: Class<Char> = Char::class.java
    override val needsImport: Boolean = false
    override val constant: Boolean = true

    override fun format(value: Char): String = Literals.javaChar(value)
}
