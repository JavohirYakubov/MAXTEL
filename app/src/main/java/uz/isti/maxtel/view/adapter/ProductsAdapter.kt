package uz.isti.maxtel.view.adapter

import android.view.View
import kotlinx.android.synthetic.main.product_item_layout.view.*
import uz.isti.maxtel.App
import uz.isti.maxtel.R
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.base.loadImage
import uz.isti.maxtel.model.ProductModel

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
    }

    fun updateItems(items: List<ProductModel>){
        this.list = items
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }
}