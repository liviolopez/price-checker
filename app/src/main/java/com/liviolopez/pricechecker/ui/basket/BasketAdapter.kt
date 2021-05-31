package com.liviolopez.pricechecker.ui.basket

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.liviolopez.pricechecker.data.local.model.BasketItem
import com.liviolopez.pricechecker.databinding.ItemOnBasketBinding
import com.liviolopez.pricechecker.ui._components.BindingViewHolder
import com.liviolopez.pricechecker.ui._components.typed
import com.liviolopez.pricechecker.ui._components.viewHolderFrom
import com.liviolopez.pricechecker.utils.extensions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@FlowPreview
class BasketAdapter(
    val removeItemListener: (basketId: Int) -> Unit,
    val quantityChanged: (basketId: Int, quantity: Int) -> Unit
) : ListAdapter<BasketItem, BindingViewHolder<*>>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*> {
        return parent.viewHolderFrom(ItemOnBasketBinding::inflate)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<*>, position: Int) {
        val item = getItem(position)
        holder.typed<ItemOnBasketBinding>().bind(item)
    }

    class ItemComparator : DiffUtil.ItemCallback<BasketItem>() {
        override fun areItemsTheSame(oldItem: BasketItem, newItem: BasketItem) = oldItem.basket.id == newItem.basket.id

        override fun areContentsTheSame(oldItem: BasketItem, newItem: BasketItem) = oldItem == newItem
    }

    private fun BindingViewHolder<ItemOnBasketBinding>.bind(basketItem: BasketItem) {
        binding.apply {
            ivThumbnail.setImage(basketItem.item.thumbnail)
            tvName.text = basketItem.item.name
            tvCode.text = basketItem.item.id
            tvPrice.text = "$${basketItem.item.price}"
            tvTotalPrice.text = "$${basketItem.item.price * basketItem.basket.quantity}"

            npQuantity.apply {
                minValue = 1
                maxValue = 50
                wrapSelectorWheel = true
                value = basketItem.basket.quantity

                CoroutineScope(Dispatchers.IO).launch {
                    getValueChangeStateFlow(value)
                        .debounce(500)
                        .distinctUntilChanged()
                        .collectLatest {
                            this@BasketAdapter.quantityChanged(basketItem.basket.id, it)
                        }
                }
            }

            btnRemoveFromCart.setOnClickListener {
                this@BasketAdapter.removeItemListener(basketItem.basket.id)
            }
        }
    }
}