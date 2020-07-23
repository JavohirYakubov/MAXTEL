package uz.isti.maxtel.view.adapter

import kotlinx.android.synthetic.main.radiobutton_item.view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.model.RadioButtonModel

class RadioButtonsAdapter(items: List<RadioButtonModel>, var handler: BaseAdapterListener): BaseAdapter(items.toMutableList(), R.layout.radiobutton_item){
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var item = items[position] as RadioButtonModel

        holder.itemView.setOnClickListener {
            var index = 0
            items.forEach {
                if (it is RadioButtonModel){
                    it.checked = index == position
                    index++
                }
            }

            handler.onClickItem(item)
            notifyDataSetChanged()
        }

        holder.itemView.radioButton.isChecked = item.checked
        holder.itemView.radioButton.text = item.name
    }

    fun getCheckedButtonIndex(): Int{
        var index = 0
        items.forEach {
            if (it is RadioButtonModel && it.checked){
                return index
            }

            index++
        }
        return -1
    }

    fun setItemCheck(select: Int){
        var index = 0
        items.forEach {
            if (it is RadioButtonModel){
                it.checked = index == select
                index++
            }
        }

        notifyDataSetChanged()
    }
}