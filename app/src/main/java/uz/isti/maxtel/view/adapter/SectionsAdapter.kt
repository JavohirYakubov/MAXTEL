package uz.isti.maxtel.view.adapter

import kotlinx.android.synthetic.main.section_item_layout.view.*
import uz.isti.maxtel.App
import uz.isti.maxtel.R
import uz.isti.maxtel.base.loadImage
import uz.isti.maxtel.model.CategoryModel
import uz.isti.maxtel.model.SectionModel
import uz.isti.maxtel.utils.Constants

class SectionsAdapter(val list: List<SectionModel>, listener: BaseAdapterListener): BaseAdapter(list.toMutableList(), R.layout.section_item_layout, listener){
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = list[position]
        holder.itemView.tvTitle.text = item.name
        holder.itemView.tvSubTitle.text = "${item.items} hil brend"
        holder.itemView.imgSection.loadImage(App.imageBaseUrl + item.image)
    }
}