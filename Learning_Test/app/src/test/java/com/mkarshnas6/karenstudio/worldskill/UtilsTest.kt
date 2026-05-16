package com.mkarshnas6.karenstudio.worldskill

object UtilsTest {
    fun testShowZoogAndFard(num: Int): String {
        return if (num % 2 == 0) "zoog" else "fard"
    }
}