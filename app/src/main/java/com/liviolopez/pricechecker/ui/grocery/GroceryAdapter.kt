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

class GroceryAdapter(
    private val onItemEventListener: OnItemEventListener? = null,
) : ListAdapter<Any, BindingViewHolder<*>>(ItemComparator()) {

    interface OnItemEventListener {
        fun onClickGroceryItem(itemId: String, view: View)
    }

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

    private fun onClick(itemId: String, vararg views: View) {
        views.forEach { view ->
            view.setOnClickListener { onItemEventListener?.onClickGroceryItem(itemId, view) }
        }
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

            onClick(item.item.id, fabAddToCart, fabRemoveFromCart)
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