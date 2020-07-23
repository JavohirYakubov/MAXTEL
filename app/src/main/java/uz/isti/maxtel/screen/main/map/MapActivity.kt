package uz.isti.maxtel.screen.main.map

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_map.*
import org.greenrobot.eventbus.EventBus
import uz.isti.maxtel.R
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.base.showInfo
import uz.isti.maxtel.base.showWarning
import uz.isti.maxtel.model.AddressModel
import uz.isti.maxtel.model.EventModel
import uz.isti.maxtel.screen.main.map.search.CompleteSearchFragment
import uz.isti.maxtel.screen.main.map.search.CompleteSearchFragmentListener
import uz.isti.maxtel.utils.Constants
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraIdleListener {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var marker: com.google.android.gms.maps.model.Marker? = null
    var locationModel: AddressModel? = null
    var locationUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        cardViewSelect.setOnClickListener {
            if (locationModel != null) {
                EventBus.getDefault()
                    .post(EventModel(Constants.EVENT_SELECT_ADDRESS, locationModel))
                finish()
            } else {
                showWarning("Пожалуйста, выберите место доставки.")
            }
        }

        imgSearch.setOnClickListener {
            val fragment = CompleteSearchFragment(object: CompleteSearchFragmentListener{

                override fun onClickItem(lat: Double, long: Double) {
                    try{
                        val cameraPosition = CameraPosition.Builder()
                            .target(LatLng(lat, long))
                            .zoom(17f)
                            .build()

                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        marker?.position = googleMap.cameraPosition.target
                    }catch (e: Exception){

                    }
                }
            })
            fragment.cordianates = "${googleMap.cameraPosition.target?.longitude},${googleMap.cameraPosition.target.latitude}"
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map ?: return
        googleMap.setOnCameraMoveListener(this)
        googleMap.setOnCameraMoveCanceledListener(this)
        googleMap.setOnCameraIdleListener(this)

        var latLng = LatLng(40.787505, 72.333009)

        val icon = BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(
                this.resources,
                R.drawable.location_placeholder
            )
        )
        marker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Tanlangan manzil!")
                .icon(icon)
        )


        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(17f)
            .build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        getMapAddress(latLng)

        if (isPermissionGiven()) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
        } else {
            givePermission()
        }
    }

    private fun isPermissionGiven(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun givePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_CHECK_SETTINGS
        )
    }

    private fun getCurrentLocation() {

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result =
            LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent) {
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result
                    var latLng = LatLng(mLastLocation!!.latitude, mLastLocation.longitude)

                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(mLastLocation.latitude, mLastLocation.longitude))
                        .zoom(17f)
                        .build()
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    marker?.position = googleMap.cameraPosition.target
                    getMapAddress(latLng)
                } else {
                    Toast.makeText(this, "No current location found", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getLastLocation()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    fun getMapAddress(location: LatLng) {
        try {
            runOnUiThread {
                val geocoder = Geocoder(this, Locale.ENGLISH)
                val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (addressList.count() > 0) {
                    var city = addressList[0].locality
                    var state = addressList[0].adminArea
                    var subState = addressList[0].subAdminArea
                    val country = addressList[0].countryName
                    val postalCode = addressList[0].postalCode
                    val knowName = addressList[0].featureName
                    val locality = addressList[0].locality
                    val subLocality = addressList[0].subLocality
                    if (city == null && state != null) {
                        if (subState != null) {
                            city = subState
                        } else {
                            city = ""
                        }
                    } else if (city != null && state == null) {
                        state = city
                        if (subState != null) {
                            city = subState
                        } else if (subLocality != null) {
                            city = subLocality
                        }
                    }

                    var address = "${knowName ?: ""}, ${city ?: ""}, ${state ?: ""}"

                    locationModel =
                        AddressModel(address, location.latitude, location.longitude, state, city)
                    tvAddress.text = address
                } else {
                    tvAddress.text = ""
                    locationModel = null
                }
            }

        } catch (e: Exception) {
            tvAddress.text = ""
            locationModel = null
        }
    }

    override fun onCameraMove() {
        marker?.position = googleMap.cameraPosition.target
    }

    override fun onCameraMoveCanceled() {
        getMapAddress(googleMap.cameraPosition.target)
    }

    override fun onCameraIdle() {
        getMapAddress(googleMap.cameraPosition.target)
    }


}
