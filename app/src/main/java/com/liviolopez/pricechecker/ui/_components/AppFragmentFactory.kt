package com.liviolopez.pricechecker.ui._components

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.liviolopez.pricechecker.ui.basket.BasketFragment
import com.liviolopez.pricechecker.ui.camera.ScannerFragment
import com.liviolopez.pricechecker.ui.grocery.GroceryFragment
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
class AppFragmentFactory @Inject constructor()
: FragmentFactory(){
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            BasketFragment::class.java.name -> BasketFragment()
            GroceryFragment::class.java.name -> GroceryFragment()
            ScannerFragment::class.java.name -> ScannerFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}