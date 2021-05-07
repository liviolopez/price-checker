package com.liviolopez.pricechecker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class Item(
    @PrimaryKey val id: String,
    val name: String,
    val price: Float,
    val thumbnail: String
) {
    var inBasket: Boolean? = null
}
