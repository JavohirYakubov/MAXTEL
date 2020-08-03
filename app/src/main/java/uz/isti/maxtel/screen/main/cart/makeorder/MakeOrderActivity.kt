package uz.isti.maxtel.screen.main.cart.makeorder

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.android.synthetic.main.activity_make_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.*
import uz.isti.maxtel.model.enum.CurrencyEnum
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.cart.makeorder.preorder.PreOrderActivity
import uz.isti.maxtel.screen.main.map.MapActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.BaseAdapterListener
import uz.isti.maxtel.view.adapter.RadioButtonsAdapter
import java.lang.Math.ceil

class MakeOrderActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_make_order
    lateinit var viewModel: MainViewModel
    var address: AddressModel? = null
    lateinit var products: List<ProductModel>
    var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }
    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        products = intent.getSerializableExtra(Constants.EXTRA_DATA) as List<ProductModel>
        totalAmount = intent.getDoubleExtra(Constants.EXTRA_DATA_2, 0.0)

        viewModel.progress.observe(this, Observer {
            setProgress(it)
        })

        viewModel.progressStoreInfo.observe(this, Observer {
            setProgress(it)
        })

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        imgBack.setOnClickListener {
            finish()
        }

        edAddress.setOnClickListener {
            startActivity<MapActivity>()
        }

        flAddress.visibility = View.VISIBLE

        if (Prefs.getCurrency() == CurrencyEnum.USD){
            rbUSD.isChecked = true
        }else{
            rbUZS.isChecked = true
        }

        cardViewOk.setOnClickListener {

            var items = products.map {
                MakeOrderProductModel(
                    it.name,
                    it.id,
                    if(Prefs.getCurrency() == CurrencyEnum.USD) it.price else it.price * Prefs.getClientInfo()!!.currency,
                    it.cartCount.toDouble(),
                    edComment.text.toString(),
                    (if(Prefs.getCurrency() == CurrencyEnum.USD) it.price else it.price * Prefs.getClientInfo()!!.currency) * it.cartCount
                )
            }

            var totalAmount = 0.0
            var productAmount = 0.0
            items.forEach {
                productAmount += it.psumma
            }

            totalAmount += productAmount

            val order = MakeOrderModel(
                Prefs.getStore()?.id?.toIntOrNull() ?: 0,
                address?.lon.toString(),
                address?.lat.toString(),
                edAddress.text.toString(),
                address?.address ?: "",
                totalAmount,
                items
            )

            startActivity<PreOrderActivity>(Constants.EXTRA_DATA, order)
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
        viewModel.getStoreInfo()
    }

    override fun initData() {
        val userInfo = Prefs.getClientInfo()
        edFullName.setText( userInfo?.name )
        edPhone.setText( userInfo?.phone )
    }

    override fun updateData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onEvent(event: EventModel<AddressModel>){
        if (event.event == Constants.EVENT_SELECT_ADDRESS){
            address = event.data
            setAddressData()
        }
    }

    fun setAddressData(){
        if (address != null && Prefs.getStoreInfo() != null){
            val store = Prefs.getStoreInfo()
            var results = FloatArray(10)
            Location.distanceBetween(store?.latitude?.toDoubleOrNull() ?: 0.0, store?.longitude?.toDoubleOrNull() ?: 0.0,
                address?.lat ?: 0.0, address?.lon ?: 0.0, results)
            var km = results[0] / 1000.0

            if (km.toInt() > (store?.radius ?: 0) && (store?.radius ?: 0) != 0){
                showWarning(getString(R.string.max_radius_delivery, (store?.radius ?: 0).toString()))
                address = null
                return
            }


            edAddress.setText(address?.address)
        }
    }

}
