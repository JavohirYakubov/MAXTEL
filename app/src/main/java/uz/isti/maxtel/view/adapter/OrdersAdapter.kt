package uz.isti.maxtel.view.adapter

import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.order_item_layout.view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.model.OrderModel
import uz.isti.maxtel.utils.DateUtils

class OrdersAdapter(val list: List<OrderModel>, listener: BaseAdapterListener): BaseAdapter(list.toMutableList(), R.layout.order_item_layout, listener){

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val status = listOf(
            holder.itemView.context.getString(R.string.accepted),
            holder.itemView.context.getString(R.string.making_status),
            holder.itemView.context.getString(R.string.order_maked)
        )

        val item = list[position]
        holder.itemView.tvId.text = holder.itemView.context.getString(R.string.order_number) + " #${item.number}"
        holder.itemView.tvStatus.text = status[item.status - 1]
    }
}