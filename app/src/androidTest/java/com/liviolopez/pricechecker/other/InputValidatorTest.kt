package com.liviolopez.pricechecker.other

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.liviolopez.pricechecker.R
import com.liviolopez.pricechecker.utils.InputValidator
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.FlowPreview
import org.junit.*
import org.junit.Assert.*
import javax.inject.Inject

@FlowPreview
@SmallTest
@HiltAndroidTest
class InputValidatorTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var validator: InputValidator

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun is_4_digit_or_less_code_to_search_valid___return_true(){
        val fourDigits = validator.codeToSearch("1234")
        assertTrue(fourDigits.isValid)
        assertNull(fourDigits.errorMsg)

        val threeDigits = validator.codeToSearch("321")
        assertTrue(threeDigits.isValid)
        assertNull(threeDigits.errorMsg)

        val twoDigits = validator.codeToSearch("22")
        assertTrue(twoDigits.isValid)
        assertNull(twoDigits.errorMsg)

        val oneDigits = validator.codeToSearch("1")
        assertTrue(oneDigits.isValid)
        assertNull(oneDigits.errorMsg)
    }

    @Test
    fun is_large_code_to_search_valid___return_false(){
        val validation = validator.codeToSearch("123456789")

        val errorMaxCharacters = context.getString(R.string.validation_code_max_characters)

        assertFalse(validation.isValid)
        assertEquals(errorMaxCharacters, validation.errorMsg)
    }

    @Test
    fun is_alphanumeric_code_to_search_valid___return_false(){
        val validation = validator.codeToSearch("12a b6-9")

        val errorMaxCharacters = context.getString(R.string.validation_code_only_numbers)

        assertFalse(validation.isValid)
        assertEquals(errorMaxCharacters, validation.errorMsg)
    }
}