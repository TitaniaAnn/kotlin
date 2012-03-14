package template.experiment1

import kotlin.template.experiment1.*
import kotlin.test.assertEquals

import junit.framework.TestCase

class StringTemplateTest : TestCase() {
    fun testTemplate() : Unit {
        val name = "James"

        // Code generated by the following template expression:
        //
        // val actual = format("hello $name!")
        val actual = format{
            it.text("hello ")
            it.expression(name)
            it.text("!")
        }

        println("Got text: $actual")
        assertEquals("hello James!", actual)
    }
}