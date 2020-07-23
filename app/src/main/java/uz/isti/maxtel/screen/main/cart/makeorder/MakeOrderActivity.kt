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
    lateinit var paymentTypeAdapter: RadioButtonsAdapter
    lateinit var deliveryTypeAdapter: RadioButtonsAdapter
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

        recyclerPaymentType.layoutManager = GridLayoutManager(this, 2)
        paymentTypeAdapter = RadioButtonsAdapter(listOf(
            RadioButtonModel(getString(R.string.cashback), true),
            RadioButtonModel(getString(R.string.payme)),
            RadioButtonModel(getString(R.string.terminal)),
            RadioButtonModel(getString(R.string.transfer))
        ), object: BaseAdapterListener{
            override fun onClickItem(item: Any?) {
                //
            }

        })
        recyclerPaymentType.adapter = paymentTypeAdapter

        recyclerDeliveryType.layoutManager = GridLayoutManager(this, 2)
        deliveryTypeAdapter = RadioButtonsAdapter(listOf(
            RadioButtonModel(getString(R.string.delivery), true),
            RadioButtonModel(getString(R.string.pickup)),
            RadioButtonModel(getString(R.string.pro_delivery))
        ), object: BaseAdapterListener{
            override fun onClickItem(item: Any?) {
                if (deliveryTypeAdapter.getCheckedButtonIndex() == 1){
                    flAddress.visibility = View.GONE
                }else{
                    if ((Prefs.getStoreInfo()?.minimalSavdoSummasi ?: 0) != 0 && (Prefs.getStoreInfo()?.minimalSavdoSummasi ?: 0) > totalAmount && deliveryTypeAdapter.getCheckedButtonIndex() == 0){
                        showWarning(getString(R.string.max_amount_del, (Prefs.getStoreInfo()?.minimalSavdoSummasi ?: 0).toDouble().formattedAmount()))
                        deliveryTypeAdapter.setItemCheck(2)
                        setAddressData()
                    }
                    flAddress.visibility = View.VISIBLE
                }
                setAddressData()
            }

        })

        if ((Prefs.getStoreInfo()?.minimalSavdoSummasi ?: 0) != 0 && (Prefs.getStoreInfo()?.minimalSavdoSummasi ?: 0) > totalAmount && deliveryTypeAdapter.getCheckedButtonIndex() == 0){
            showWarning(getString(R.string.max_amount_del, (Prefs.getStoreInfo()?.minimalSavdoSummasi ?: 0).toDouble().formattedAmount()))
            deliveryTypeAdapter.setItemCheck(2)
            setAddressData()
        }

        flAddress.visibility = View.VISIBLE

        recyclerDeliveryType.adapter = deliveryTypeAdapter

        cardViewOk.setOnClickListener {
            var chashbox = 0
            when(paymentTypeAdapter.getCheckedButtonIndex()){
                1 ->{
                    chashbox = 2
                }
                2 ->{
                    chashbox = 1
                }
                3 ->{
                    chashbox = 3
                }
            }

            var delivery = 1
            var isProDelivery = false
            when(deliveryTypeAdapter.getCheckedButtonIndex()){
                0 ->{
                    delivery = 0
                }
                2 ->{
                    delivery = 0
                    isProDelivery = true
                }
            }

            if (address == null && delivery == 0){
                showWarning("Пожалуйста, выберите адрес доставки.")
                return@setOnClickListener
            }

            val store = Prefs.getStoreInfo()

            var results = FloatArray(10)
            Location.distanceBetween(store?.latitude?.toDoubleOrNull() ?: 0.0, store?.longitude?.toDoubleOrNull() ?: 0.0,
                address?.lat ?: 0.0, address?.lon ?: 0.0, results)
            var km = results[0] / 1000.0

            if (km.toInt() > (store?.radius ?: 0) && (store?.radius ?: 0) != 0){
                showWarning(getString(R.string.max_radius_delivery, (store?.radius ?: 0).toString()))
                address = null
                return@setOnClickListener
            }

            var deliveryPrice = 0.0

            if (deliveryTypeAdapter.getCheckedButtonIndex() == 2){
                if ((store?.DeliveryConstSumma ?: 0) == 0){
                    deliveryPrice = km * (store?.dostavkaKmSumma ?: 0)
                    if (deliveryPrice > (store?.maximalPullikDostavka ?: 0)){
                        deliveryPrice = (store?.maximalPullikDostavka ?: 0).toDouble()
                    }
                }else{
                    deliveryPrice = (store?.DeliveryConstSumma ?: 0).toDouble()
                }
            }

            var items = products.map {
                MakeOrderProductModel(
                    it.name,
                    it.id,
                    if(it.unity == "Блок") it.blokprice else it.price,
                    if (it.unity == "Блок") (it.cartCount * (it.blok?.toInt() ?: 0)) else it.cartCount,
                    if (it.unity == "Блок") it.cartCount.toDouble() else 0.0,
                    it.donalibonus,
                    edComment.text.toString(),
                    if(it.unity == "Блок") (it.blokprice * it.cartCount) else (it.price * it.cartCount)
                )
            }

            var user = Prefs.getClientInfo()
            var totalAmount = deliveryPrice
            var productAmount = 0.0
            items.forEach {
                productAmount += it.psumma
            }
            // productAmount = 120 000
            //usuallyDiscountMin1 = 50 000
            //usuallyDiscountMin2 = 80 000
            //usuallyDiscountMin3 = 100 000
            var salePersent = user?.forRegDiscount ?: 0
            if (user!!.usuallyDiscountMin1 <= productAmount && user.usuallyDiscountMin2 > productAmount){
                salePersent += user.usuallyDiscount1
            }else if (user!!.usuallyDiscountMin2 <= productAmount && user.usuallyDiscountMin3 > productAmount){
                salePersent += user.usuallyDiscount2
            }else if (user!!.usuallyDiscountMin3 <= productAmount){
                salePersent += user.usuallyDiscount3
            }

            var saleAmount = 0.0
            if (salePersent > 0){
                saleAmount = productAmount * (salePersent / 100.0)
            }
            totalAmount += (productAmount - saleAmount)

            val order = MakeOrderModel(
                Prefs.getStore()?.id?.toIntOrNull() ?: 0,
                address?.lon.toString(),
                address?.lat.toString(),
                edAddress.text.toString(),
                1,
                chashbox,
                delivery,
                address?.address ?: "",
                deliveryPrice.toInt(),
                saleAmount.toInt(),
                totalAmount.toInt(),
                chashbox == 2,
                items,
                id = Prefs.getToken()
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
        if (address != null && Prefs.getStoreInfo() != null && deliveryTypeAdapter.getCheckedButtonIndex() != 1){
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

            var deliveryPrice = 0.0

            if (deliveryTypeAdapter.getCheckedButtonIndex() == 2){
                if ((store?.DeliveryConstSumma ?: 0) == 0){
                    deliveryPrice = km * (store?.dostavkaKmSumma ?: 0)
                    if (deliveryPrice > (store?.maximalPullikDostavka ?: 0)){
                        deliveryPrice = (store?.maximalPullikDostavka ?: 0).toDouble()
                    }
                }else{
                    deliveryPrice = (store?.DeliveryConstSumma ?: 0).toDouble()
                }
            }

            tvDelivery.text = if(deliveryPrice > 0) deliveryPrice.formattedAmount() else getString(R.string.free)

            lyDelivery.visibility = View.VISIBLE
            edAddress.setText(address?.address)
        }else{
            lyDelivery.visibility = View.GONE
        }
    }

}
