package com.liviolopez.pricechecker.data.local.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT item.*, (CASE WHEN basket.id IS NULL THEN 0 ELSE 1 END) AS inBasket FROM item LEFT JOIN basket ON basket.itemId = item.id WHERE item.id LIKE '%' || :query || '%'")
    fun searchItem(query: String): Flow<List<ItemBasket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<Item>)

    @Query("DELETE FROM item WHERE id NOT IN (SELECT itemId FROM basket)")
    suspend fun deleteAllItems()

    @Query("SELECT COUNT(*) FROM item")
    suspend fun getCountItems(): Int
}