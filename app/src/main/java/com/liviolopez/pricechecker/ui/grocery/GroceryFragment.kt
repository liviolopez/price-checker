package com.liviolopez.pricechecker.ui.grocery

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.liviolopez.pricechecker.R
import com.liviolopez.pricechecker.databinding.FragmentGroceryBinding
import com.liviolopez.pricechecker.ui._components.loading
import com.liviolopez.pricechecker.ui._components.showEmptyMsg
import com.liviolopez.pricechecker.ui._components.showError
import com.liviolopez.pricechecker.ui._components.success
import com.liviolopez.pricechecker.utils.Resource
import com.liviolopez.pricechecker.utils.extensions.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class GroceryFragment : Fragment(R.layout.fragment_grocery) {
    private val TAG = "SourceFragment"

    private val viewModel: GroceryViewModel by activityViewModels()

    private lateinit var binding: FragmentGroceryBinding
    private lateinit var groceryAdapter: GroceryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGroceryBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar!!.title = context?.getString(R.string.all_items)

        groceryAdapter = GroceryAdapter()
        binding.rvAllItems.adapter = groceryAdapter

        setupAllItemsAdapter()

        lifecycleScope.launchWhenCreated {
            viewModel.fetchItemsGrocery()
        }
    }

    private fun setupAllItemsAdapter() {
        viewModel.allItems.onEach { result ->

            when (result.status) {
                Resource.Status.LOADING -> binding.standbyView.loading
                Resource.Status.SUCCESS -> {
                    binding.standbyView.success

                    if (result.data.isNullOrEmpty()) {
                        binding.standbyView.showEmptyMsg
                    } else {
                        binding.rvAllItems.setVisible()
                        groceryAdapter.submitList(result.data)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e(TAG, "Error: ${result.throwable?.localizedMessage}")

                    binding.standbyView.showError = getString(
                        R.string.error_msg_param,
                        result.throwable?.localizedMessage
                    )
                }
            }

        }.launchIn(lifecycleScope)
    }
}