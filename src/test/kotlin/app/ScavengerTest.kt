package app

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ScavengerTest {

    private fun getResourceAsText(path: String): String? =
        object {}.javaClass.getResource(path)?.readText()

    @Test
    fun testAll() {
        for (i in 1..2) {
            var testName = "/test"
            if (i < 10) testName += "0"
            testName += i.toString()

            val before = getResourceAsText(testName + "before.java")!!
            val afterExpected = getResourceAsText(testName + "after.java")!!
            val consoleExpected = getResourceAsText(testName + "console.txt")!!

            val scavenger = Scavenger()
            var afterActual: String
            val baos = ByteArrayOutputStream()
            baos.use {
                PrintStream(baos).use {
                    afterActual = scavenger.removeUnusedVarDecls(before, it, false, false)
                }
            }
            Assertions.assertEquals(afterExpected, afterActual)
            Assertions.assertEquals(consoleExpected, baos.toString())
        }
    }
}
