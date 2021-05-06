package com.liviolopez.pricechecker.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class Item(
    @PrimaryKey val id: String,
    val name: String,
    val price: Float,
    val thumbnail: String
)

data class ItemBasket(@Embedded val item: Item, val inBasket: Boolean? = false)