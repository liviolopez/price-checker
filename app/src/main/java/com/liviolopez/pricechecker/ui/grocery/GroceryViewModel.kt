package com.liviolopez.pricechecker.ui.grocery

import androidx.lifecycle.*
import com.liviolopez.pricechecker.data.local.model.Item
import com.liviolopez.pricechecker.repository.Repository
import com.liviolopez.pricechecker.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class GroceryViewModel @Inject constructor (
    private val repository: Repository
) : ViewModel() {

    private val _allItems = MutableStateFlow<Resource<List<Item>>>(Resource.loading())
    val allItems = _allItems.asStateFlow()

    suspend fun fetchItemsGrocery() {
        repository.fetchItemsGrocery()
            .catch { e -> _allItems.value = Resource.error(e, emptyList()) }
            .collect {  _allItems.value = it }
    }
}