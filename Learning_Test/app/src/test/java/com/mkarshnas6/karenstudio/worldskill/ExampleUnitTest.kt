package com.mkarshnas6.karenstudio.worldskill

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val name = "Mohammad"
        val family = "Karshnas"
        assertEquals("Mohammad Karshnas", "$name $family")
    }

    @Test
    fun checkZoogAndFard() {
        val listNums = mapOf(
            233 to "fard",
            2323543 to "fard",
            1 to "fard",
            2 to "zoog",
            23 to "fard",
            1119 to "fard",
            4848 to "zoog"
        )
        for ((num,expected) in listNums) {
            assertEquals(expected, UtilsTest.testShowZoogAndFard(num))
        }
    }

}