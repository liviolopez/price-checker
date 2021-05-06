package com.liviolopez.pricechecker.data.local.model

import androidx.room.*

@Entity(
    tableName = "basket",
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = ["id"],
        childColumns = ["itemId"]
    )],
    indices = [Index(value = ["itemId"], unique = true)]
)
data class Basket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: String,
    val quantity: Int,
    val createdAt: Long = System.currentTimeMillis()
)

data class BasketItem(
    @Embedded val basket: Basket,
    @Relation(
        parentColumn = "itemId",
        entityColumn = "id"
    )
    val item: Item
)