package com.liviolopez.pricechecker

import com.liviolopez.pricechecker.other.InputValidatorTest
import com.liviolopez.pricechecker.other.StringUtilsTest
import com.liviolopez.pricechecker.other.ThemeColorTest
import com.liviolopez.pricechecker.ui.MainActivityTest
import com.liviolopez.pricechecker.ui.grocery.GroceryFragmentTest_FakeAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.runner.RunWith
import org.junit.runners.Suite

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainActivityTest::class,
    GroceryFragmentTest_FakeAPI::class,
    InputValidatorTest::class,
    StringUtilsTest::class,
    ThemeColorTest::class
)
class AppTestSuite