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

    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        order = intent.getSerializableExtra(Constants.EXTRA_DATA) as OrderModel
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        imgBack.setOnClickListener {
            finish()
        }
    }

    override fun loadData() {
        viewModel.getDeliveryLocation(order.refKey)
    }


    override fun initData() {
        step_view.setSteps(
            listOf(
                getString(R.string.accepted),
                getString(R.string.making_status),
                getString(R.string.order_maked)
            )
        )
        step_view.go(order.status - 1, true)

        tvStore.text = order.skladname

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

        tvDate.text = DateUtils.getTimeFromServerTime(order.date)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = OrderProductAdapter(order.array)

        if (googleMap != null && order.store_lat.isNotEmpty() && order.store_long.isNotEmpty()){

            var clientLatLng = LatLng(order.store_lat.toDoubleOrNull() ?: 0.0, order.store_long.toDoubleOrNull() ?: 0.0)

            val clientIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.store_center))
            googleMap?.addMarker(
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
    }

    override fun updateData() {

    }


    override fun onMapReady(map: GoogleMap?) {
        googleMap = map?: return
        initData()
    }

}
