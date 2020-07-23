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
        val item = list[position]
        holder.itemView.tvStore.text = item.skladname
        holder.itemView.tvDeliveryType.text = if (item.delivery == 0) holder.itemView.context.getString(R.string.delivery_type) else holder.itemView.context.getString(R.string.pickup)
        holder.itemView.tvId.text = "№${item.number}"
        holder.itemView.tvCreateDate.text = DateUtils.getTimeFromServerTime(item.date)
        holder.itemView.tvTotalAmount.text = item.summa.toDouble().formattedAmount()

        when(item.cashbox){
            0 ->{
                holder.itemView.tvPaymentType.text = holder.itemView.context.getString(R.string.cashback)
            }
            1 ->{
                holder.itemView.tvPaymentType.text = holder.itemView.context.getString(R.string.terminal)
            }
            2 ->{
                holder.itemView.tvPaymentType.text = holder.itemView.context.getString(R.string.payme)
            }
            3 ->{
                holder.itemView.tvPaymentType.text = holder.itemView.context.getString(R.string.transfer)
            }
        }

        if (item.Payme){
            holder.itemView.tvPaymentType.text = holder.itemView.context.getString(R.string.payme)
        }

//        if (item.Payme && (item.paidSumma < item.summa)){
//            holder.itemView.tvStatus.text = holder.itemView.context.getString(R.string.payment_waiting)
//        }else{
//            when(item.status){
//                0, 1 ->{
//                    holder.itemView.tvStatus.text = if(!item.isAccepted) holder.itemView.context.getString(R.string.waiting___) else holder.itemView.context.getString(
//                                            R.string.accepted)
//                }
//                1 ->{
//                   holder.itemView.tvStatus.text = holder.itemView.context.getString(R.string.making_status)
//                }
//                2 ->{
//                   holder.itemView.tvStatus.text = holder.itemView.context.getString(R.string.order_maked)
//                }
//                3 ->{
//                   holder.itemView.tvStatus.text = holder.itemView.context.getString(R.string.on_way)
//                }
//                else ->{
//                   holder.itemView.tvStatus.text = holder.itemView.context.getString(R.string.completed)
//                }
//            }
//
//        }
    }
}