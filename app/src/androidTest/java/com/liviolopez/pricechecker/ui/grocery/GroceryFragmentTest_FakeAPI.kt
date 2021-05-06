package com.liviolopez.pricechecker.ui.grocery

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.SmallTest
import com.liviolopez.pricechecker.R
import com.liviolopez.pricechecker._components.FileReader
import com.liviolopez.pricechecker._components.launchFragmentInHiltContainer
import com.liviolopez.pricechecker.ui._components.AppFragmentFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.FlowPreview
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@FlowPreview
@SmallTest
@HiltAndroidTest
class GroceryFragmentTest_FakeAPI{

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fragmentFactory: AppFragmentFactory

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        hiltRule.inject()

        mockWebServer = MockWebServer()
        mockWebServer.start(8080)

        launchFragmentInHiltContainer<GroceryFragment>(factory = fragmentFactory)
    }

    @After
    fun tearDown() = mockWebServer.shutdown()

    @Test
    fun is_OverlayStandByView_on_initial_status(){
        onView(withId(R.id.progress_bar_container)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.error_container)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.empty_list)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun is_the_Fragment_title_same_in_string_resources(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        onView(withId(R.id.action_bar)).check(ViewAssertions.matches(hasDescendant(withText(context.getString(R.string.all_items)))))
    }

    @Test
    fun recycler_view_is_showing_correct_item_names(){

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("pricechecker.items.json"))
            }
        }

        Thread.sleep(500)

        onView(withId(R.id.rv_all_items)).check(
            ViewAssertions.matches(hasDescendant(withText("Banana")))
        )

        onView(withId(R.id.rv_all_items)).check(
            ViewAssertions.matches(hasDescendant(withText("Apple")))
        )

        onView(withId(R.id.rv_all_items)).check(
            ViewAssertions.matches(hasDescendant(withText("Other Stuff")))
        )
    }
}