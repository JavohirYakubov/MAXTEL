package uz.isti.maxtel.screen.main.orders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_orders.*
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.activity_profile_edit.imgBack
import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.base.startActivity
import uz.isti.maxtel.model.OrderModel
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.orders.detail.OrderDetailActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.view.adapter.BaseAdapterListener
import uz.isti.maxtel.view.adapter.OrdersAdapter
import java.io.Serializable

class OrdersActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_orders
    lateinit var viewModel: MainViewModel

    override fun initViews() {
        imgBack.setOnClickListener {
            finish()
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.progress.observe(this, Observer {
            setProgress(it)
        })

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.ordersData.observe(this, Observer {
            updateData()
        })
    }

    override fun loadData() {
        viewModel.getOrders()
    }

    override fun initData() {

    }

    override fun updateData() {
        if (viewModel.ordersData.value != null){
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = OrdersAdapter(viewModel.ordersData.value ?: emptyList(), object : BaseAdapterListener{
                override fun onClickItem(item: Any?) {
                    startActivity<OrderDetailActivity>(Constants.EXTRA_DATA, item as Serializable)
                }
            })
        }else{

        }
    }

}
