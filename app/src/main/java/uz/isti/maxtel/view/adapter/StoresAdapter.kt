package uz.isti.maxtel.view.adapter

import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.district_item_layout.view.*
import kotlinx.android.synthetic.main.district_item_layout.view.tvTitle
import kotlinx.android.synthetic.main.store_item_layout.view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.model.StoreSimpleModel
import uz.isti.maxtel.utils.Prefs

class StoresAdapter(val list: List<StoreSimpleModel>, listener: BaseAdapterListener): BaseAdapter(list.toMutableList(), R.layout.store_item_layout, listener){
    val colors = listOf(
        R.color.random1,
        R.color.random2,
        R.color.random3,
        R.color.random4,
        R.color.random5,
        R.color.random6
    )
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.itemView.cardView.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context,
            colors[position%6]))
        holder.itemView.setOnClickListener {
            if (Prefs.getCartList().count() > 0){

            }else{
                list.forEach {
                    it.checked = false
                }
                item.checked = true
                notifyDataSetChanged()
            }
            listener?.onClickItem(item)
        }

        holder.itemView.tvTitle.text = item.name
        holder.itemView.tvPhone.text = item.phone
        if (item.checked){
            holder.itemView.imgChecked.visibility = View.VISIBLE
        }else{
            holder.itemView.imgChecked.visibility = View.GONE
        }
    }
}