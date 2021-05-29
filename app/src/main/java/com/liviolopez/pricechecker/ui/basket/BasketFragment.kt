package com.liviolopez.pricechecker.ui.basket

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.liviolopez.pricechecker.R
import com.liviolopez.pricechecker.data.local.model.ItemBasket
import com.liviolopez.pricechecker.databinding.FragmentBasketBinding
import com.liviolopez.pricechecker.ui.MainActivity
import com.liviolopez.pricechecker.ui._components.*
import com.liviolopez.pricechecker.ui.grocery.GroceryAdapter
import com.liviolopez.pricechecker.utils.InputValidator
import com.liviolopez.pricechecker.utils.Resource
import com.liviolopez.pricechecker.utils.extensions.setGone
import com.liviolopez.pricechecker.utils.extensions.setVisible
import com.liviolopez.pricechecker.utils.extensions.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class BasketFragment : Fragment(R.layout.fragment_basket) {
    private val TAG = "BasketFragment"

    private val viewModel: BasketViewModel by activityViewModels()

    @Inject
    lateinit var validator: InputValidator

    private lateinit var actionBar: ActionBar

    private lateinit var binding: FragmentBasketBinding
    private lateinit var basketAdapter: BasketAdapter
    private lateinit var groceryAdapter: GroceryAdapter

    private var scrollToTop = false
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        actionBar = (activity as AppCompatActivity).supportActionBar!!
        navController = findNavController(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)

        menu.findItem(R.id.clean_card).apply {
            setOnMenuItemClickListener {
                viewModel.cleanBasket()
                false
            }
        }

        menu.findItem(R.id.all_items).apply {
            setOnMenuItemClickListener {
                val imm = context?.getSystemService(InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)

                navController.navigate(
                    BasketFragmentDirections.actionBasketFragmentToGroceryFragment()
                )
                false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBasketBinding.bind(view)

        basketAdapter = BasketAdapter(
            { basketId -> viewModel.removeItemFromBasket(basketId = basketId) },
            { basketId, quantity -> viewModel.updateItemBasketQuantity(basketId, quantity) }
        )

        binding.rvBasketItems.adapter = basketAdapter

        groceryAdapter = GroceryAdapter { itemId: String, clickedView: View -> onClickGroceryItem(itemId, clickedView) }
        binding.rvSearchedItems.adapter = groceryAdapter

        setupBasketAdapter()
        setupSearchAdapter()
        setupSearchInput()

        viewModel.searchQuery.value?.let { searchCode = it }

        binding.btnScan.setOnClickListener {
            searchCode = ""

            if ((activity as MainActivity).checkLocationPermission()) {
                navController.navigate(
                    BasketFragmentDirections.actionBasketFragmentToScannerFragment()
                )
            }
        }

        findNavController(this).currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("codeScanned")
            ?.observe(viewLifecycleOwner) { codeScanned ->
                searchCode = codeScanned
                binding.svSearchCode.isIconified = false
            }
    }

    private fun setupBasketAdapter() {
        viewModel.itemsBasket
            .onEach { itemsOnBasket ->
            binding.standbyView.success

            basketAdapter.submitList(itemsOnBasket) {
                if (scrollToTop) {
                    binding.rvBasketItems.scrollToPosition(0)
                    scrollToTop = false
                }

                if(isVisible){
                    val totalPrice = itemsOnBasket.map { it.basket.quantity * it.item.price }.sum()
                    actionBar.title = context?.getString(R.string.total_price_basket, totalPrice)
                }

                binding.rvBasketItems.setVisible()
            }

            if (itemsOnBasket.isEmpty()) {
                binding.standbyView.showEmptyMsg(
                    binding.rvBasketItems,
                    context?.getString(R.string.basket_empty)!!
                )
            }
        }.launchIn(lifecycleScope)
    }

    var searchCode:String
        get() = binding.svSearchCode.query.toString().trim()
        set(value) { binding.svSearchCode.setQuery(value, true) }

    private fun setupSearchInput() {
        binding.svSearchCode.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchItemOnGrocery()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    private fun searchItemOnGrocery() {
        searchCode.let { code ->
            if (code != viewModel.searchQuery.value) {
                validator.codeToSearch(code).apply {
                    if(isValid){
                        viewModel.searchQuery.value = code
                    } else {
                        binding.root.showSnackBar(errorMsg!!, 65)
                    }
                }
            }
        }
    }

    private fun setupSearchAdapter() {
        viewModel.itemsSearched
            .filter { searchCode.isNotEmpty() }
            .onEach { result ->
                when (result.status) {
                    Resource.Status.LOADING -> {
                        binding.standbySearchView.loading
                    }
                    Resource.Status.SUCCESS -> {
                        binding.standbySearchView.success

                        if (result.data.isNullOrEmpty()) {
                            binding.root.showSnackBar(requireContext().getString(R.string.empty_list_msg, searchCode), 65)
                        } else {
                            binding.rvSearchedItems.setVisible()
                            submitListGroceryAdapter(result.data)
                        }
                    }
                    Resource.Status.ERROR -> {
                        Log.e(TAG, "Error: ${result.throwable?.localizedMessage}")

                        if (result.data.isNullOrEmpty()) {
                            binding.rvSearchedItems.setGone()
                            binding.standbySearchView.showError = getString(
                                R.string.error_msg_param,
                                result.throwable?.localizedMessage
                            )
                        } else {
                            submitListGroceryAdapter(result.data)
                            binding.root.showSnackBar(getString(R.string.error_msg_updating), 65)
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun submitListGroceryAdapter(itemsList: List<ItemBasket>) {
        groceryAdapter.submitList(itemsList) {
            binding.rvSearchedItems.scrollToPosition(0)
        }
    }

    private fun onClickGroceryItem(itemId: String, clickedView: View) {
        binding.svSearchCode.setQuery("", true)
        scrollToTop = true
        submitListGroceryAdapter(emptyList())
        binding.rvSearchedItems.setGone()

        when (clickedView.id) {
            R.id.fab_add_to_cart -> viewModel.addItemToBasket(itemId)
            R.id.fab_remove_from_cart -> viewModel.removeItemFromBasket(itemId = itemId)
            else -> Unit
        }
    }
}