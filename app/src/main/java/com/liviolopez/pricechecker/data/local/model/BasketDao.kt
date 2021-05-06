package com.liviolopez.pricechecker.data.local.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BasketDao {
    @Transaction
    @Query("SELECT * FROM basket ORDER BY createdAt DESC")
    fun getMyItems(): Flow<List<BasketItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemToBasket(basketItem: Basket)

    @Query("DELETE FROM basket WHERE id = :basketId OR itemId = :itemId")
    suspend fun removeItemFromBasket(basketId: Int?, itemId: String?)

    @Query("UPDATE basket SET quantity = :quantity WHERE id = :basketId")
    suspend fun updateQuantity(basketId: Int, quantity: Int)

    @Query("DELETE FROM basket")
    suspend fun deleteAllItems()
}