package com.liviolopez.pricechecker.repository

import androidx.room.withTransaction
import com.liviolopez.pricechecker.data.local.AppDataBase
import com.liviolopez.pricechecker.data.local.AppDataStore
import com.liviolopez.pricechecker.data.local.model.Basket
import com.liviolopez.pricechecker.data.local.model.Item
import com.liviolopez.pricechecker.data.remote.RemoteDataSource
import com.liviolopez.pricechecker.data.remote.response.toLocalModel
import com.liviolopez.pricechecker.utils.networkBoundResource
import com.liviolopez.pricechecker.utils.syncNetworkResource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(
    private val remoteData: RemoteDataSource,
    private val localData: AppDataBase,
    private val appDataStore: AppDataStore
) : Repository {
    private val itemDao = localData.itemDao()
    private val basketDao = localData.basketDao()

    // Fetch data from the API each 10min (isDbUpdateNeeded())
    override suspend fun initializeCache() = syncNetworkResource(
        shouldFetch = {
            coroutineScope {
                appDataStore.isDbUpdateNeeded() || itemDao.getCountItems() == 0
            }
        },
        runFetchCall = {
            remoteData.fetchItems()
        },
        saveFetchResult = { items ->
            localData.withTransaction {
                itemDao.deleteAllItems()
                itemDao.insertItems(items.map { it.toLocalModel() })
                appDataStore.registerDbUpdate()
            }
        }
    )

    // Always fetch all items in the API
    override fun fetchItemsGrocery() = networkBoundResource(
        loadFromDb = {
            itemDao.getAllItems()
        },
        createCall = {
            remoteData.fetchItems()
        },
        saveFetchResult = { items ->
            localData.withTransaction {
                itemDao.deleteAllItems()
                itemDao.insertItems(items.map { it.toLocalModel() })
                appDataStore.registerDbUpdate()
            }
        }
    )

    override fun searchItemCode(query: String): Flow<List<Item>> {
        return itemDao.searchItem(query)
    }

    override fun getItemsBasket() = basketDao.getMyItems()

    override suspend fun addItemToBasket(itemId: String) {
        basketDao.addItemToBasket(Basket(itemId = itemId, quantity = 1))
    }

    override suspend fun removeItemFromBasket(basketId: Int?, itemId: String?) {
        basketDao.removeItemFromBasket(basketId, itemId)
    }

    override suspend fun updateItemBasketQuantity(basketId: Int, quantity: Int) {
        basketDao.updateQuantity(basketId, quantity)
    }

    override suspend fun cleanBasket() {
        basketDao.deleteAllItems()
    }
}
