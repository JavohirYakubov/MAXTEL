package uz.isti.maxtel.screen.main.orders.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_orders.*

import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseFragment
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.base.startActivity
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.orders.detail.OrderDetailActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.view.adapter.BaseAdapterListener
import uz.isti.maxtel.view.adapter.OrdersAdapter
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 */
class OrdersFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun getLayout(): Int = R.layout.fragment_orders
    lateinit var viewModel: MainViewModel

    override fun setupViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.progress.observe(this, Observer {
            swipeRefresh.isRefreshing = it
        })

        viewModel.error.observe(this, Observer {
            activity?.showError(it)
        })

        viewModel.ordersData.observe(this, Observer {
            setData()
        })

        swipeRefresh.setOnRefreshListener(this)
    }

    override fun loadData() {
        viewModel.getOrders()
    }

    override fun setData() {
        if (viewModel.ordersData.value != null){
            recycler.layoutManager = LinearLayoutManager(activity)
            recycler.adapter = OrdersAdapter(viewModel.ordersData.value ?: emptyList(), object :
                BaseAdapterListener {
                override fun onClickItem(item: Any?) {
                    getBaseActivity {
                        it.startActivity<OrderDetailActivity>(Constants.EXTRA_DATA, item as Serializable)
                    }

                }
            })
        }else{

        }
    }

    override fun onRefresh() {
        loadData()
    }

}
