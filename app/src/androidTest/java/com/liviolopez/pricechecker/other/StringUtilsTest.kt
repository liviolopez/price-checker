package com.liviolopez.pricechecker.other

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.liviolopez.pricechecker.utils.StringUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

// This test is performed like instrumentation in addition to unit test just to run all test in the AppTestSuite
@RunWith(AndroidJUnit4::class)
class StringUtilsTest {

    @Test
    fun code_item_to_search_is_only_number____return_true(){
        val result = StringUtils.onlyNumber("123456")
        assertTrue(result)
    }

    @Test
    fun code_item_to_search_is_only_number___letter____return_false(){
        val result = StringUtils.onlyNumber("123A456")

        assertFalse(result)
    }

    @Test
    fun code_item_to_search_is_only_number___character____return_false(){
        val result = StringUtils.onlyNumber("$")

        assertFalse(result)
    }

}