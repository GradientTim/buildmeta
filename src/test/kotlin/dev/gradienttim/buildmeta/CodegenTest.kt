package dev.gradienttim.buildmeta

import dev.gradienttim.buildmeta.codegen.data.Literals
import dev.gradienttim.buildmeta.meta.FieldMeta.NamingStrategy
import dev.gradienttim.buildmeta.value.FieldName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CodegenTest {
    @Test
    fun `screaming snake case splits words`() {
        mapOf(
            "rawName" to "RAW_NAME",
            "HTTPServer" to "HTTP_SERVER",
            "version2Beta" to "VERSION2_BETA",
            "already_SNAKE" to "ALREADY_SNAKE",
            "x" to "X",
        ).forEach { (input, expected) ->
            assertEquals(expected, NamingStrategy.SCREAMING_SNAKE_CASE.apply(FieldName(input)))
        }
    }

    @Test
    fun `keep leaves the name unchanged`() {
        assertEquals("rawName", NamingStrategy.KEEP.apply(FieldName("rawName")))
    }

    @Test
    fun `invalid field names are rejected`() {
        listOf("", " ", "1abc", "a-b", "a.b").forEach { name ->
            assertFailsWith<IllegalArgumentException> { FieldName(name) }
        }
    }

    @Test
    fun `kotlin literals are escaped`() {
        assertEquals("\"a\\\"b\\\\c\\\$d\\n\"", Literals.kotlinString("a\"b\\c\$d\n"))
        assertEquals("\"\\u0001\"", Literals.kotlinString("\u0001"))
        assertEquals("'\\''", Literals.kotlinChar('\''))
    }

    @Test
    fun `java literals are escaped`() {
        assertEquals("\"a\\\"b\\\\c\$d\\n\"", Literals.javaString("a\"b\\c\$d\n"))
        assertEquals("\"\\001\"", Literals.javaString("\u0001"))
        assertEquals("'\\''", Literals.javaChar('\''))
    }
}
