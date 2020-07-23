package uz.isti.maxtel.view.adapter

import kotlinx.android.synthetic.main.category_item_layout.view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.getColor
import uz.isti.maxtel.model.CategoryModel

class CategoriesAdapter(var list: List<CategoryModel>, listener: BaseAdapterListener): BaseAdapter(list.toMutableList(), R.layout.category_item_layout, listener){
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]

        holder.itemView.setOnClickListener {
            if (!item.selected){
                list.forEach { it.selected = false }
                item.selected = true
                listener?.onClickItem(item)
                notifyDataSetChanged()
            }
        }

        if (item.selected){
            holder.itemView.cardView.setCardBackgroundColor(holder.itemView.getColor(R.color.colorAccent))
        }else{
            holder.itemView.cardView.setCardBackgroundColor(holder.itemView.getColor(R.color.white))
        }
        holder.itemView.tvTitle.text = item.name
    }
}