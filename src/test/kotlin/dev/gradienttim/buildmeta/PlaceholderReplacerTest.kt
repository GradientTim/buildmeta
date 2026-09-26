package dev.gradienttim.buildmeta

import dev.gradienttim.buildmeta.placeholders.PlaceholderReplacer
import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlaceholderReplacerTest {
    private val values = mapOf("main.appName" to "App", "git.hash" to "abc")

    private fun replace(line: String): String =
        PlaceholderReplacer("{{buildMeta:", "}}", values, "app.properties").transform(line)

    @Test
    fun `replaces main and qualified placeholders`() {
        assertEquals("App abc", replace("{{buildMeta:appName}} {{buildMeta:git.hash}}"))
        assertEquals("App", replace("{{buildMeta: appName }}"))
    }

    @Test
    fun `leaves lines without placeholders unchanged`() {
        assertEquals("a\\\\b {{other}}", replace("a\\\\b {{other}}"))
        assertEquals("{{buildMeta:appName", replace("{{buildMeta:appName"))
    }

    @Test
    fun `backslashes escape the prefix in pairs`() {
        mapOf(
            "\\{{buildMeta:appName}}" to "{{buildMeta:appName}}",
            "C:\\\\{{buildMeta:appName}}" to "C:\\App",
            "\\\\\\{{buildMeta:appName}}" to "\\{{buildMeta:appName}}",
            "\\\\\\\\{{buildMeta:appName}}" to "\\\\App",
            "{{buildMeta:appName}}\\{{buildMeta:appName}}" to "App{{buildMeta:appName}}",
        ).forEach { (input, expected) ->
            assertEquals(expected, replace(input), input)
        }
    }

    @Test
    fun `unknown placeholders fail with file and line`() {
        val replacer = PlaceholderReplacer("{{buildMeta:", "}}", values, "app.properties")
        replacer.transform("first")
        val exception = assertFailsWith<GradleException> { replacer.transform("{{buildMeta:missing}}") }
        assertContains(exception.message.orEmpty(), "app.properties:2")
    }
}
