package uz.isti.maxtel.screen.main.cart.makeorder

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.addTextChangedListener
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
    var discountAmount = 0.0
    var deliveryAmount = 0.0

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
        discountAmount = intent.getDoubleExtra(Constants.EXTRA_DATA_3, 0.0)

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

        rbUSD.setOnClickListener {
            rbCashback.isChecked = true
        }

        rbPayme.setOnClickListener {
            if (rbUSD.isChecked){
                rbCashback.isChecked = true
            }
        }

        rbDelivery.setOnClickListener {
            flAddress.visibility = View.VISIBLE
            if (rbDelivery.isChecked){
                tvDeliveryAmount.visibility = View.VISIBLE
            }else{
                tvDeliveryAmount.visibility = View.GONE
            }
        }

        rbPickUp.setOnClickListener {
            flAddress.visibility = View.GONE
            if (rbDelivery.isChecked){
                tvDeliveryAmount.visibility = View.VISIBLE
            }else{
                tvDeliveryAmount.visibility = View.GONE
            }
        }

        cardViewOk.setOnClickListener {
            if (address == null && rbDelivery.isChecked){
                showWarning(getString(R.string.select_delivery_address))
                return@setOnClickListener
            }
            var items = products.map {
                MakeOrderProductModel(
                    it.name,
                    it.id,
                    if(rbUSD.isChecked) it.price else it.price * Prefs.getClientInfo()!!.currency,
                    it.cartCount.toDouble(),
                    edComment.text.toString(),
                    (if(rbUSD.isChecked) it.price else it.price * Prefs.getClientInfo()!!.currency) * it.cartCount
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
                if (rbDelivery.isChecked) 0 else 1,
                if (rbDelivery.isChecked) deliveryAmount else 0.0,
                rbPayme.isChecked,
                address?.address ?: "",
                totalAmount,
                items,
                false,
                chbCashback.isChecked,
                discountAmount,
                edCashbackAmount.text.toString().toDoubleOrNull() ?: 0.0
            )

            startActivity<PreOrderActivity>(Constants.EXTRA_DATA, order)
        }

        lyCashback.visibility = View.GONE

        chbCashback.setOnClickListener {
            if (!chbCashback.isChecked){
                lyCashbackAmount.visibility = View.GONE
            }else{
                lyCashbackAmount.visibility = View.VISIBLE
                tvDiscountPercent.text = Prefs.getClientInfo()!!.cashback.formattedAmountWithCurrency("сум")
            }
        }

        edCashbackAmount.addTextChangedListener {
            if (edCashbackAmount.text.toString().toDoubleOrNull() != null){
                if (edCashbackAmount.text.toString().toDouble() > Prefs.getClientInfo()!!.cashback){
                    edCashbackAmount.setText(String.format("%.0f", Prefs.getClientInfo()!!.cashback))
                }
            }else{

            }
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

        if (Prefs.getClientInfo()!!.cashback > 0){
            lyCashback.visibility = View.VISIBLE
            tvDiscountPercent.text = Prefs.getClientInfo()!!.cashback.formattedAmountWithCurrency("сум")
        }else{
            lyCashback.visibility = View.GONE
        }

        if (Prefs.getCurrency() == CurrencyEnum.USD){
            rbUSD.isChecked = true
            rbCashback.isChecked = true
        }else{
            rbUZS.isChecked = true
        }

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
        val userInfo = Prefs.getClientInfo()
        if (address != null && Prefs.getStoreInfo() != null && userInfo != null){
            val store = Prefs.getStoreInfo()
            var results = FloatArray(10)
            Location.distanceBetween(store?.latitude?.toDoubleOrNull() ?: 0.0, store?.longitude?.toDoubleOrNull() ?: 0.0,
                address?.lat ?: 0.0, address?.lon ?: 0.0, results)
            var km = results[0] / 1000.0

//            if (km.toInt() > (store?.radius ?: 0) && (store?.radius ?: 0) != 0){
//                showWarning(getString(R.string.max_radius_delivery, (store?.radius ?: 0).toString()))
//                address = null
//                return
//            }
            deliveryAmount = userInfo.minimalDostavkaSumma
            if (km > userInfo.minimalDostavkaKm){
                deliveryAmount += userInfo.kmSumma * (km - userInfo.minimalDostavkaKm)
            }

            tvDeliveryAmount.text = getString(R.string.delivery_price) + " " + deliveryAmount.formattedAmountWithoutRate(true, currency = "сум")
            edAddress.setText(address?.address)

            if (rbDelivery.isChecked){
                tvDeliveryAmount.visibility = View.VISIBLE
            }else{
                tvDeliveryAmount.visibility = View.GONE
            }
        }
    }

}
