package uz.isti.maxtel.view.adapter

import android.view.View
import kotlinx.android.synthetic.main.cart_product_item_layout.view.*
import uz.isti.maxtel.App
import uz.isti.maxtel.R
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.base.loadImage
import uz.isti.maxtel.base.showWarning
import uz.isti.maxtel.model.*
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs

interface CartProductsListener: BaseAdapterListener{
    fun refreshTotalAmount()
}
class CartProductsAdapter(val list: List<ProductModel>, val handler: CartProductsListener): BaseAdapter(list.toMutableList(), R.layout.cart_product_item_layout, handler){
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem<ProductModel>(position)
        holder.itemView.tvTitle.text = item.name
        holder.itemView.imgProduct.loadImage(App.imageBaseUrl + item.image)
        holder.itemView.tvPrice.text =  ((item?.price) * item.cartCount).formattedAmount()

        holder.itemView.tvProductCount.text = item.cartCount.toString()
        holder.itemView.tvDiscountPercent.text = item.discount_percent.toString() + "%"
        holder.itemView.tvDiscountPercent.visibility = if (item.discount_percent > 0) View.VISIBLE else View.GONE

        holder.itemView.imgMinus.setOnClickListener {
            if (item.cartCount > 0){
                item.cartCount--
                if (item.cartCount == 0){
                    items.removeAt(position)
                    notifyItemRemoved(position)
                }
                Prefs.add2Cart(BasketModel(item.id!!, item.cartCount))
                notifyItemChanged(position)
                handler.refreshTotalAmount()
            }
        }

        holder.itemView.imgPlus.setOnClickListener {
            if (item.cartCount > item!!.productCount){
                holder.itemView.context?.showWarning("Вы выбрали максимальное количество товара.")
                return@setOnClickListener
            }

            item.cartCount++
            Prefs.add2Cart(BasketModel(item.id!!, item.cartCount))
            notifyItemChanged(position)
            handler.refreshTotalAmount()
        }
    }
}