package uz.isti.maxtel.view.adapter

import android.view.View
import kotlinx.android.synthetic.main.news_item_layout.view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.model.NewsModel
import uz.isti.maxtel.utils.DateUtils

class NewsAdapter(val list: List<NewsModel>): BaseAdapter(list.toMutableList(), R.layout.news_item_layout){
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.itemView.setOnClickListener {
            item.checked = !item.checked
            notifyItemChanged(position)
        }
        holder.itemView.tvDate.text = DateUtils.getTimeFromServerTime(item.data)
        holder.itemView.tvTitle.text = item.title
        holder.itemView.tvComment.text = item.news
        if (item.checked){
            holder.itemView.imgColl.setImageResource(R.drawable.ic_expand_less_black_24dp)
            holder.itemView.tvComment.visibility = View.VISIBLE
        }else{
            holder.itemView.imgColl.setImageResource(R.drawable.ic_expand_more_black_24dp)
            holder.itemView.tvComment.visibility = View.GONE
        }

    }
}