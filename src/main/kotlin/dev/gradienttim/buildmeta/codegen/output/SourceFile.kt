package dev.gradienttim.buildmeta.codegen.output

import java.io.Serializable

internal class SourceFile(
    packageName: String,
    fileName: String,
    val content: String,
) : Serializable {
    val relativePath: String = "${packageName.replace('.', '/')}/$fileName"
}
