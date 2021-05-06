package com.liviolopez.pricechecker.utils

import android.content.Context
import com.liviolopez.pricechecker.R
import javax.inject.Inject

data class Validation(val isValid: Boolean, val errorMsg: String? = null)

class InputValidator @Inject constructor(val context: Context) {

    fun codeToSearch(string: String) : Validation {
        val errorMsg = when {
            !StringUtils.onlyNumber(string) -> context.getString(R.string.validation_code_only_numbers)
            string.length > 4 -> context.getString(R.string.validation_code_max_characters)
            else -> null
        }

        return Validation(errorMsg == null, errorMsg)
    }

}