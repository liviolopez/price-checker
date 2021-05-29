package com.liviolopez.pricechecker.ui.grocery

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.liviolopez.pricechecker.data.local.model.Item
import com.liviolopez.pricechecker.data.local.model.ItemBasket
import com.liviolopez.pricechecker.databinding.ItemOnGroceryDescriptionBinding
import com.liviolopez.pricechecker.databinding.ItemOnGroceryToBasketBinding
import com.liviolopez.pricechecker.ui._components.BindingViewHolder
import com.liviolopez.pricechecker.ui._components.typed
import com.liviolopez.pricechecker.ui._components.viewHolderFrom
import com.liviolopez.pricechecker.utils.extensions.setImage
import com.liviolopez.pricechecker.utils.extensions.visibleIf

// This ListAdapter receives Any as a parameter to show the implementation of various ViewHolders according to the class (Item, ItemBasket).
// Also as an example, to show the class ItemBasket with the Room's annotation @Embedded
//
// The Git Branch in the follow link has a modified version of this ListAdapter that receive the "Item" class instead of Any
// https://github.com/liviolopez/price-checker/compare/recycler-item-viewtype

class GroceryAdapter(
    private val clickListener: ((String, View) -> Unit)? = null
) : ListAdapter<Any, BindingViewHolder<*>>(ItemComparator()) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is ItemBasket) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*> {
        return when(viewType){
            1    -> parent.viewHolderFrom(ItemOnGroceryToBasketBinding::inflate)
            else -> parent.viewHolderFrom(ItemOnGroceryDescriptionBinding::inflate)
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<*>, position: Int) {
        when (val item = getItem(position)) {
            is ItemBasket -> holder.typed<ItemOnGroceryToBasketBinding>().bind(item)
            is Item -> holder.typed<ItemOnGroceryDescriptionBinding>().bind(item)
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when (oldItem) {
                is ItemBasket -> oldItem.item.id == (newItem as ItemBasket).item.id
                else -> (oldItem as Item).id == (newItem as Item).id
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem
    }

    @JvmName("bindItemOnGroceryAllBinding")
    private fun BindingViewHolder<ItemOnGroceryToBasketBinding>.bind(item: ItemBasket) {
        binding.apply {
            ivThumbnail.setImage(item.item.thumbnail)
            tvName.text = item.item.name
            tvCode.text = item.item.id
            tvPrice.text = "$${item.item.price}"

            fabAddToCart.visibleIf { item.inBasket == false }
            fabRemoveFromCart.visibleIf { item.inBasket == true }

            listOf(fabAddToCart, fabRemoveFromCart).forEach { view ->
                view.setOnClickListener {
                    this@GroceryAdapter.clickListener?.let { it(item.item.id, view) }
                }
            }
        }
    }

    private fun BindingViewHolder<ItemOnGroceryDescriptionBinding>.bind(item: Item) {
        binding.apply {
            ivThumbnail.setImage(item.thumbnail)
            tvName.text = item.name
            tvCode.text = item.id
            tvPrice.text = "$${item.price}"
        }
    }
}