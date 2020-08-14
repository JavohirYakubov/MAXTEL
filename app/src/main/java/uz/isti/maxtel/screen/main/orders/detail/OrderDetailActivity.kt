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
import kotlinx.android.synthetic.main.activity_order_detail.imgBack
import kotlinx.android.synthetic.main.activity_order_detail.recycler
import kotlinx.android.synthetic.main.activity_order_detail.tvCurrency
import kotlinx.android.synthetic.main.activity_order_detail.tvOrderType
import kotlinx.android.synthetic.main.activity_order_detail.tvPaymentType
import kotlinx.android.synthetic.main.activity_order_detail.tvProductAmount
import kotlinx.android.synthetic.main.activity_order_detail.tvTitle
import kotlinx.android.synthetic.main.activity_order_detail.tvTotalAmount
import rx.functions.Action1
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
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
    lateinit var viewModel: MainViewModel
    lateinit var order: OrderModel
    private var googleMap: GoogleMap? = null
    var storeMarker: Marker? = null
    var deliveryMarker: Marker? = null
    var orderyMarker: Marker? = null
    private var interval: Disposable? = null

    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        order = intent.getSerializableExtra(Constants.EXTRA_DATA) as OrderModel
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        imgBack.setOnClickListener {
            finish()
        }

        viewModel.deliveryLocation.observe(this, Observer {
            if (googleMap != null && it.lat.isNotEmpty() && it.long.isNotEmpty()){

                var clientLatLng = LatLng(it.lat.toDoubleOrNull() ?: 0.0, it.long.toDoubleOrNull() ?: 0.0)

                if (deliveryMarker != null){
                    deliveryMarker?.position = clientLatLng
                    return@Observer
                }

                val clientIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.transports))
                deliveryMarker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(clientLatLng)
                        .title( "\uD83D\uDE9A " + order.deliveryPerson + "\n\uD83D\uDCF1" + order.deliveryPersonPhone)
                        .icon(clientIcon)
                )

                val cameraPosition = CameraPosition.Builder()
                    .target(clientLatLng)
                    .zoom(10f)
                    .build()

                googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        })

        if (order.status == 2){
            startTimer()
        }
    }

    override fun loadData() {
        if (order.status == 2){
            viewModel.getDeliveryLocation(order.refKey)
        }
    }


    override fun initData() {
        step_view.setSteps(
            listOf(
                getString(R.string.accepted),
                getString(R.string.making_status),
                getString(R.string.on_way),
                getString(R.string.completed)
            )
        )
        step_view.go(order.status, true)

        tvStore.text = order.skladname

        if (order.delivery == 0){
            tvOrderType.text = getString(R.string.delivery)
        }else{
            tvOrderType.text = getString(R.string.pickup)
        }

        if (order.payme){
            tvPaymentType.text = getString(R.string.payme)
        }else{
            tvPaymentType.text = getString(R.string.cashback)
        }


        tvTitle.text =  getString(R.string.order_number_) + "${order.number}"
        if (order.dollar){
            tvCurrency.text = "$"
        }else{
            tvCurrency.text = "сум"
        }
        var totalAmount = order.deliverySumma.toDouble()
        var productAmount = 0.0
        order.array.forEach {
            productAmount += it.psumma
        }

        totalAmount += productAmount

        tvProductAmount.text = productAmount.formattedAmountWithoutRate(true, if (order.dollar){
            "$"
        }else{
           "сум"
        })

        tvTotalAmount.text = totalAmount.formattedAmountWithoutRate(true, if (order.dollar){
            "$"
        }else{
            "сум"
        })

        tvDeliveryAmount.text = order.deliverySumma.formattedAmountWithoutRate(true, "сум")

        tvDate.text = DateUtils.getTimeFromServerTime(order.date)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = OrderProductAdapter(order.array)

        if (googleMap != null && order.store_lat.isNotEmpty() && order.store_long.isNotEmpty() && storeMarker == null){

            var clientLatLng = LatLng(order.store_lat.toDoubleOrNull() ?: 0.0, order.store_long.toDoubleOrNull() ?: 0.0)

            val clientIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.store_center))
            storeMarker = googleMap?.addMarker(
                MarkerOptions()
                    .position(clientLatLng)
                    .title(order.skladname)
                    .icon(clientIcon)
            )

            val cameraPosition = CameraPosition.Builder()
                .target(clientLatLng)
                .zoom(10f)
                .build()

            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        if (googleMap != null && order.lat.isNotEmpty() && order.long.isNotEmpty() && orderyMarker == null){

            var clientLatLng = LatLng(order.lat.toDoubleOrNull() ?: 0.0, order.long.toDoubleOrNull() ?: 0.0)

            val clientIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.client_center))
            orderyMarker = googleMap?.addMarker(
                MarkerOptions()
                    .position(clientLatLng)
                    .title(order.skladname)
                    .icon(clientIcon)
            )
        }
    }

    override fun updateData() {

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

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map?: return
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        interval?.dispose()
    }
}
