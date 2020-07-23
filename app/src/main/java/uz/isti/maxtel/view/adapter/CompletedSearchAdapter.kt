package uz.isti.maxtel.view.adapter

import kotlinx.android.synthetic.main.search_item_layout.view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.model.FeaturesItem

class CompletedSearchAdapter(items: List<FeaturesItem>, listener: BaseAdapterListener): BaseAdapter(items.toMutableList(), R.layout.search_item_layout, listener){
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = items[position] as FeaturesItem

        holder.itemView.tvName.text = "${item.properties?.name ?: ""}, ${item.properties?.description ?: ""}"
    }
}