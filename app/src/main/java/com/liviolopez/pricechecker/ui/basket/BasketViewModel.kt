package com.liviolopez.pricechecker.ui.basket

import androidx.lifecycle.*
import com.liviolopez.pricechecker.data.local.model.BasketItem
import com.liviolopez.pricechecker.data.local.model.ItemBasket
import com.liviolopez.pricechecker.repository.Repository
import com.liviolopez.pricechecker.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * use in terminal the following command to test SaveStateHandle
 * $> adb shell am kill com.liviolopez.pricechecker
 */

@HiltViewModel
class BasketViewModel @Inject constructor (
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeCache()
            searchItemCode()
        }
    }

    // --------------- Search ---------------

    private val _itemsSearched = MutableStateFlow<Resource<List<ItemBasket>>>(Resource.loading())
    val itemsSearched = _itemsSearched.asStateFlow()

    private val _searchQuery = savedStateHandle.getLiveData<String?>("key_search_query")
    val searchQuery = MutableStateFlow(_searchQuery.value)

    private suspend fun searchItemCode(){
        searchQuery
            .onEach { _searchQuery.value = it }
            .filterNotNull()
            .collectLatest { query ->
                _itemsSearched.value = Resource.loading()

                repository.searchItemCode(query)
                    .catch { e -> _itemsSearched.value = Resource.error(e, emptyList()) }
                    .collect { _itemsSearched.value = Resource.success(it) }
            }
    }

    private fun cleanSearch(){
        searchQuery.value = null
        _itemsSearched.value = Resource.success(emptyList())
    }

    // ------------ Basket ---------------

    val itemsBasket: Flow<List<BasketItem>> get() = repository.getItemsBasket()

    fun addItemToBasket(itemId: String){
        cleanSearch()
        viewModelScope.launch {
            repository.addItemToBasket(itemId)
        }
    }

    fun removeItemFromBasket(basketId: Int? = null, itemId: String? = null){
        cleanSearch()
        viewModelScope.launch {
            repository.removeItemFromBasket(basketId, itemId)
        }
    }

    fun updateItemBasketQuantity(basketId: Int, quantity: Int){
        viewModelScope.launch {
            repository.updateItemBasketQuantity(basketId, quantity)
        }
    }

    fun cleanBasket(){
        viewModelScope.launch {
            repository.cleanBasket()
        }
    }
}
