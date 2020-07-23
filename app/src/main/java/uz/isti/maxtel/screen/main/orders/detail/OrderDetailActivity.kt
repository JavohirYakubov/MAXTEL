package uz.isti.maxtel.screen.main.orders.detail

import android.graphics.BitmapFactory
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_detail.*
import rx.functions.Action1
import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.base.startActivity
import uz.isti.maxtel.base.startActivityToOpenUrlInBrowser
import uz.isti.maxtel.model.OrderModel
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.webview.AppWebViewActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.DateUtils
import uz.isti.maxtel.view.adapter.OrderProductAdapter
import java.util.*
import java.util.concurrent.TimeUnit


class OrderDetailActivity : BaseActivity(), OnMapReadyCallback {
    override fun getLayout(): Int = R.layout.activity_order_detail
    private var interval: Disposable? = null
    lateinit var viewModel: MainViewModel
    lateinit var order: OrderModel
    private var googleMap: GoogleMap? = null
    var storeMarker: Marker? = null

    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        order = intent.getSerializableExtra(Constants.EXTRA_DATA) as OrderModel
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        imgBack.setOnClickListener {
            finish()
        }

        viewModel.deliveryLocation.observe(this, Observer {
            if (storeMarker != null){
                storeMarker!!.position = LatLng(it.lat.toDoubleOrNull() ?: 0.0, it.long.toDoubleOrNull() ?: 0.0)
                if (order.delivery == 0){
                    val cameraPosition = CameraPosition.Builder()
                        .target(storeMarker!!.position)
                        .zoom(googleMap?.cameraPosition?.zoom ?: 11f)
                        .build()

                    googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }else{
                if (order.status < 2 || order.delivery == 1){
                    val storeIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.store_center))
                    storeMarker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(LatLng(it.lat.toDoubleOrNull() ?: 0.0, it.long.toDoubleOrNull() ?: 0.0))
                            .title(it.title)
                            .icon(storeIcon)
                    )
                }else{
                    val storeIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.transports))
                    storeMarker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(LatLng(it.lat.toDoubleOrNull() ?: 0.0, it.long.toDoubleOrNull() ?: 0.0))
                            .title(it.title)
                            .icon(storeIcon)
                    )
                }
            }
        })

        cardViewOk.setOnClickListener {
            startActivityToOpenUrlInBrowser(
                order.PaymeURL ?: ""
            )
        }
        if (order.status >= 2){
            startTimer()
        }
    }

    fun startTimer(){
        interval = Observable.interval(15, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                runOnUiThread {
                    loadData()
                }
            }
    }

    override fun loadData() {
        viewModel.getDeliveryLocation(order.refKey)
    }

    override fun onDestroy() {
        super.onDestroy()
        interval?.dispose()
    }

    override fun initData() {
        step_view.setSteps(
            listOf(
                getString(R.string.accepted),
                getString(R.string.making_status),
                getString(R.string.order_maked),
                getString(R.string.on_way),
                getString(R.string.completed)
            )
        )
        step_view.go(order.status, true)

        tvStore.text = order.skladname

        tvTitle.text =  getString(R.string.order_number_) + "${order.number}"
        when(order.cashbox){
            0 ->{
                tvPaymentType.text = getString(R.string.cashback)
            }
            1 ->{
                tvPaymentType.text = getString(R.string.terminal)
            }
            2 ->{
                tvPaymentType.text = getString(R.string.payme)
            }
            3 ->{
                tvPaymentType.text = getString(R.string.transfer)
            }
        }

        if (order.Payme){
            tvPaymentType.text = getString(R.string.payme)
        }

        if (order.delivery == 1){
            tvDeliveryType.text = getString(R.string.pickup)
        }else{
            tvDeliveryType.text = getString(R.string.delivery)
        }
        var totalAmount = order.deliverySumma.toDouble()
        var productAmount = 0.0
        order.array.forEach {
            productAmount += it.psumma
        }

        totalAmount += (productAmount - order.discount)

        tvProductAmount.text = productAmount.formattedAmount()
        tvDeliveryAmount.text = if(order.deliverySumma > 0) order.deliverySumma.toDouble().formattedAmount() else getString(R.string.free)
        tvSale.text = (-order.discount.toDouble()).formattedAmount()
        tvTotalAmount.text = totalAmount.formattedAmount()

        tvDate.text = DateUtils.getTimeFromServerTime(order.date)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = OrderProductAdapter(order.array)

        if (order.Payme && (order.paidSumma < order.summa)){
            cardViewOk.visibility = View.VISIBLE
        }else{
            cardViewOk.visibility = View.GONE
        }

        if (googleMap != null && order.lat.isNotEmpty() && order.long.isNotEmpty()){

            var clientLatLng = LatLng(order.lat.toDoubleOrNull() ?: 0.0, order.long.toDoubleOrNull() ?: 0.0)

            val clientIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.client_center))
            val clientMarker = googleMap?.addMarker(
                MarkerOptions()
                    .position(clientLatLng)
                    .title(getString(R.string.order_number_) + "${order.number}")
                    .icon(clientIcon)
            )

            val cameraPosition = CameraPosition.Builder()
                .target(clientLatLng)
                .zoom(10f)
                .build()

            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun updateData() {

    }


    override fun onMapReady(map: GoogleMap?) {
        googleMap = map?: return
        initData()
    }

}
