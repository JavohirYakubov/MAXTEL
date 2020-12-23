package uz.isti.maxtel.view.adapter

import android.view.View
import kotlinx.android.synthetic.main.product_item_layout.view.*
import kotlinx.android.synthetic.main.product_item_layout.view.tvTitle
import kotlinx.android.synthetic.main.store_item_layout.view.*
import org.greenrobot.eventbus.EventBus
import uz.isti.maxtel.App
import uz.isti.maxtel.R
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.base.loadImage
import uz.isti.maxtel.base.showSuccess
import uz.isti.maxtel.model.BasketModel
import uz.isti.maxtel.model.EventModel
import uz.isti.maxtel.model.ProductModel
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

interface ProductsAdapterListener: BaseAdapterListener{
    fun getPage(index: Int)
}

class ProductsAdapter(var list: List<ProductModel>, val handler: ProductsAdapterListener): BaseAdapter(list.toMutableList(), R.layout.product_item_layout, handler){
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem<ProductModel>(position)
        holder.itemView.tvTitle.text = item.name
        holder.itemView.imgProduct.loadImage(App.imageBaseUrl + item.image)

        holder.itemView.tvPrice.text = item?.price?.formattedAmount()

        if (item.cartCount > 0){
            holder.itemView.imgPlus.visibility = View.GONE
            holder.itemView.textViewCartCount.visibility = View.VISIBLE
            holder.itemView.textViewCartCount.text = item.cartCount.toString()
        }else{

            holder.itemView.imgPlus.visibility = View.VISIBLE
            holder.itemView.textViewCartCount.visibility = View.GONE
        }

        if (item.discount_percent > 0){
            holder.itemView.tvDiscountPercent.visibility = View.VISIBLE
            holder.itemView.flProductOldPrice.visibility = View.VISIBLE

            holder.itemView.tvDiscountPercent.text = "-" + item.discount_percent.toString() + "%"
            holder.itemView.tvProductOldPrice.text = item.old_price.formattedAmount()
        }else{
            holder.itemView.tvDiscountPercent.visibility = View.GONE
            holder.itemView.flProductOldPrice.visibility = View.GONE
        }
        if (Prefs.getToken().isNullOrEmpty()){
            holder.itemView.tvPrice.visibility = View.INVISIBLE
            holder.itemView.flProductOldPrice.visibility = View.INVISIBLE
        }else{
            holder.itemView.tvPrice.visibility = View.VISIBLE
            holder.itemView.tvPrice.visibility = View.VISIBLE
        }

        holder.itemView.imgPlus.setOnClickListener {
            if (item.cartCount == 0){
                var cart = BasketModel(item.id!!, 1)
                Prefs.add2Cart(cart)
                item.cartCount = 1
                EventBus.getDefault().post(EventModel(Constants.EVENT_UPDATE_BASKET, 0))
                EventBus.getDefault().post(EventModel(Constants.EVENT_UPDATE_CART, 0))
                notifyItemChanged(position)
            }
        }
    }

    fun updateItems(items: List<ProductModel>){
        this.list = items
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }
}