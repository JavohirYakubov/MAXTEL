package uz.isti.maxtel.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
interface BaseAdapterListener{
    fun onClickItem(item: Any?)
}

abstract class BaseAdapter(var items: MutableList<Any?>, val layout: Int, val listener: BaseAdapterListener? = null): RecyclerView.Adapter<BaseAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.itemView.setOnClickListener {
            listener?.onClickItem(items[position])
        }
    }

    fun <T> getItem(position: Int): T{
        return items[position] as T
    }

    inner class ItemHolder(view: View): RecyclerView.ViewHolder(view)
}