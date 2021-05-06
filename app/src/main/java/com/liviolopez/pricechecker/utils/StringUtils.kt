package com.liviolopez.pricechecker.utils

object StringUtils {
    private val regex = "^[0-9]*\$".toRegex()

    fun onlyNumber(string: String) = regex.matches(string)
}