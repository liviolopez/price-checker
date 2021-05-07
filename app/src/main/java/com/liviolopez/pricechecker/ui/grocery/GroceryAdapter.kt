package com.liviolopez.pricechecker.ui.grocery

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.liviolopez.pricechecker.data.local.model.Item
import com.liviolopez.pricechecker.databinding.ItemOnGroceryDescriptionBinding
import com.liviolopez.pricechecker.databinding.ItemOnGroceryToBasketBinding
import com.liviolopez.pricechecker.ui._components.BindingViewHolder
import com.liviolopez.pricechecker.ui._components.typed
import com.liviolopez.pricechecker.ui._components.viewHolderFrom
import com.liviolopez.pricechecker.utils.extensions.setImage
import com.liviolopez.pricechecker.utils.extensions.visibleIf

class GroceryAdapter(
    private val onItemEventListener: OnItemEventListener? = null,
) : ListAdapter<Item, BindingViewHolder<ViewBinding>>(ItemComparator()) {

    interface OnItemEventListener {
        fun onClickGroceryItem(itemId: String, view: View)
    }

    override fun getItemViewType(position: Int) = if ((getItem(position) as Item).inBasket != null) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewBinding> {
        return when(viewType){
            1    -> parent.viewHolderFrom(ItemOnGroceryToBasketBinding::inflate)
            else -> parent.viewHolderFrom(ItemOnGroceryDescriptionBinding::inflate)
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewBinding>, position: Int) {
        val item = getItem(position)

        when (getItemViewType(position)) {
            1 -> holder.typed<ItemOnGroceryToBasketBinding>().bind(item)
            else -> holder.typed<ItemOnGroceryDescriptionBinding>().bind(item)
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
    }

    private fun onClick(itemId: String, vararg views: View) {
        views.forEach { view ->
            view.setOnClickListener { onItemEventListener?.onClickGroceryItem(itemId, view) }
        }
    }

    @JvmName("bindItemOnGroceryAllBinding")
    private fun BindingViewHolder<ItemOnGroceryToBasketBinding>.bind(item: Item) {
        binding.apply {
            ivThumbnail.setImage(item.thumbnail)
            tvName.text = item.name
            tvCode.text = item.id
            tvPrice.text = "$${item.price}"

            fabAddToCart.visibleIf { item.inBasket == false }
            fabRemoveFromCart.visibleIf { item.inBasket == true }

            onClick(item.id, fabAddToCart, fabRemoveFromCart)
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
