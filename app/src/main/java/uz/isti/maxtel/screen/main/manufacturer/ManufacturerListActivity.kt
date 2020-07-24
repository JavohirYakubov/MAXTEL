package uz.isti.maxtel.screen.main.manufacturer

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_manufacturer_list.*
import kotlinx.android.synthetic.main.select_currency_dialog.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.*
import uz.isti.maxtel.model.enum.CurrencyEnum
import uz.isti.maxtel.screen.main.MainActivity
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.product.ProductListActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.BaseAdapterListener
import uz.isti.maxtel.view.adapter.BrandAdapter
import java.io.Serializable

class ManufacturerListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    override fun getLayout(): Int = R.layout.activity_manufacturer_list
    lateinit var viewModel: MainViewModel
    lateinit var section: SectionModel

    override fun initViews() {
        section = intent.getSerializableExtra(Constants.EXTRA_DATA) as SectionModel

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        imgBack.setOnClickListener {
            finish()
        }

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.progressManufacturers.observe(this, Observer {
            setProgress(it)
        })

        viewModel.brandData.observe(this, Observer {
            updateData()
        })

        flCart.setOnClickListener {
            startClearActivity<MainActivity>(Constants.EXTRA_DATA_START_FRAGMENT, R.id.cartFragment)
        }

        NetworkUtils.registerNetworkStatusChangedListener(object: NetworkUtils.OnNetworkStatusChangedListener{
            override fun onConnected(networkType: NetworkUtils.NetworkType?) {
                showConnection(notConnection = false)
                loadData()
            }

            override fun onDisconnected() {
                showConnection(notConnection = true)
            }
        })
    }

    override fun loadData() {
        viewModel.getBrandList(section.id)
        checkCart()
    }

    override fun initData() {
        tvTitle.text = section.name
        swipeRefresh.setOnRefreshListener(this)
        val cart = Prefs.getCartModel()
        if (cart?.count ?: 0 > 0){
            flCart.visibility = View.VISIBLE
            tvCartCount.text = getString(R.string.cart_).toUpperCase() + " (${cart?.count.toString()})"
            tvCartAmount.text = cart?.totalAmount.formattedAmount()
        }else{
            flCart.visibility = View.GONE
        }
    }

    override fun updateData() {
        if (viewModel.brandData.value != null){
            recycler.layoutManager = GridLayoutManager(this, 1)
            recycler.adapter = BrandAdapter(viewModel.brandData.value!!, object: BaseAdapterListener{
                override fun onClickItem(item: Any?) {
                    startActivity<ProductListActivity>(Constants.EXTRA_DATA, (item as BrandModel) as Serializable, Constants.EXTRA_DATA_2, (section) as Serializable)
                }
            })
        }
    }

    override fun onRefresh() {
        swipeRefresh.isRefreshing = false
        loadData()
    }

    @Subscribe
    fun onEvent(event: EventModel<Int>){
        if (event.event == Constants.EVENT_UPDATE_BASKET){
            checkCart()
        }
    }


    fun checkCart(){
        val cart = Prefs.getCartModel()
        if (cart?.count ?: 0 > 0){
            flCart.visibility = View.VISIBLE
            tvCartCount.text = getString(R.string.cart_).toUpperCase() + " (${cart?.count})"
            tvCartAmount.text = cart?.totalAmount.formattedAmount()
        }else{
            flCart.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

}
