package uz.isti.maxtel.view.adapter

import kotlinx.android.synthetic.main.order_product_item_layout.view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.model.MakeOrderProductModel

class OrderProductAdapter(val list: List<MakeOrderProductModel>): BaseAdapter(list.toMutableList(), R.layout.order_product_item_layout){
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.itemView.tvTitle.text = if(item.name.isNullOrEmpty()) item.pname else item.name
        holder.itemView.tvCount.text = if (item.blok > 0) item.blok.toString() else item.dona.toString()
        holder.itemView.tvAmount.text = item.price.toDouble().formattedAmount(false)
        holder.itemView.tvTotalAmount.text = item.psumma.toDouble().formattedAmount(false)
    }
}