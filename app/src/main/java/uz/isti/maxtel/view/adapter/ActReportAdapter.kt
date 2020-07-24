package uz.isti.maxtel.view.adapter

import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.act_report_item.view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.model.ActReportModel

class ActReportAdapter(val data: ActReportModel): BaseAdapter(data.table.toMutableList(), R.layout.act_report_item){
    override fun getItemCount(): Int {
        return data.table.count() + 3
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (position == 0){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.random5))
            holder.itemView.tvId.text = "*"
            holder.itemView.tvTitle.text = data.saldo.title
            holder.itemView.tvPlus.text = data.saldo.plus_summa.toString()
            holder.itemView.tvMinus.text = data.saldo.minus_summa.toString()
        }else if (position == (data.table.count() + 1)){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.status_waiting))
            holder.itemView.tvId.text = "*"
            holder.itemView.tvTitle.text = data.oborot.title
            holder.itemView.tvPlus.text = data.oborot.plus_summa.toString()
            holder.itemView.tvMinus.text = data.oborot.minus_summa.toString()
        }else if (position == (data.table.count() + 2)){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.status_completed))
            holder.itemView.tvId.text = "*"
            holder.itemView.tvTitle.text = data.dolg.title
            holder.itemView.tvPlus.text = data.dolg.plus_summa.toString()
            holder.itemView.tvMinus.text = data.dolg.minus_summa.toString()
        }else {
            val item = data.table[position - 1]
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.itemView.tvId.text = "${item.id}"
            holder.itemView.tvTitle.text = item.title
            holder.itemView.tvPlus.text = item.plus_summa.toString()
            holder.itemView.tvMinus.text = item.minus_summa.toString()
        }
    }
}