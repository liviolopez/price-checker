package com.liviolopez.pricechecker.repository

import com.liviolopez.pricechecker.data.local.model.BasketItem
import com.liviolopez.pricechecker.data.local.model.Item
import com.liviolopez.pricechecker.data.local.model.ItemBasket
import com.liviolopez.pricechecker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun initializeCache()

    fun fetchItemsGrocery(): Flow<Resource<List<Item>>>
    fun searchItemCode(query: String): Flow<List<ItemBasket>>

    fun getItemsBasket(): Flow<List<BasketItem>>
    suspend fun addItemToBasket(itemId: String)
    suspend fun removeItemFromBasket(basketId: Int?, itemId: String?)
    suspend fun updateItemBasketQuantity(basketId: Int, quantity: Int)
    suspend fun cleanBasket()
}