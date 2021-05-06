package com.liviolopez.pricechecker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.liviolopez.pricechecker.data.local.model.Basket
import com.liviolopez.pricechecker.data.local.model.BasketDao
import com.liviolopez.pricechecker.data.local.model.Item
import com.liviolopez.pricechecker.data.local.model.ItemDao

@Database(entities = [Item::class, Basket::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun basketDao(): BasketDao
}