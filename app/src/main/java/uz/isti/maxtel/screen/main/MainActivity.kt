package uz.isti.maxtel.screen.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet_language.view.*
import kotlinx.android.synthetic.main.nav_layout.view.*
import kotlinx.android.synthetic.main.select_currency_dialog.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import uz.isti.maxtel.BuildConfig
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.*
import uz.isti.maxtel.model.enum.CurrencyEnum
import uz.isti.maxtel.screen.auth.SignActivity
import uz.isti.maxtel.screen.main.aboutapp.AboutAppActivity
import uz.isti.maxtel.screen.main.actreport.ActReportActivity
import uz.isti.maxtel.screen.main.cart.CartFragment
import uz.isti.maxtel.screen.main.favourite.FavouriteFragment
import uz.isti.maxtel.screen.main.home.HomeFragment
import uz.isti.maxtel.screen.main.manufacturer.ManufacturerListActivity
import uz.isti.maxtel.screen.main.news.NewsActivity
import uz.isti.maxtel.screen.main.orders.fragment.OrdersFragment
import uz.isti.maxtel.screen.main.profile.ProfileFragment
import uz.isti.maxtel.screen.main.profile.edit.ProfileEditActivity
import uz.isti.maxtel.screen.main.rating.RatingActivity
import uz.isti.maxtel.screen.main.search.SearchProductActivity
import uz.isti.maxtel.screen.main.stores.StoresFragment
import uz.isti.maxtel.screen.main.webview.AppWebViewActivity
import uz.isti.maxtel.screen.splash.SplashActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Constants.Companion.EVENT_UPDATE_BASKET
import uz.isti.maxtel.utils.LocaleManager.setNewLocale
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.BaseAdapterListener
import uz.isti.maxtel.view.adapter.CategoriesAdapter
import uz.isti.maxtel.view.adapter.SectionsAdapter
import uz.isti.maxtel.view.custom.SaleFragment
import java.io.Serializable

class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {
    override fun getLayout(): Int = R.layout.activity_main
    lateinit var viewModel: MainViewModel
    val storesFragment = StoresFragment()
    val homeFragment = HomeFragment()
    val favouriteFragment = FavouriteFragment()
    val cartFragment = CartFragment()
    val ordersFragment = OrdersFragment()
    var badge: Badge? = null

    override fun initViews() {
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.clientInfoData.observe(this, Observer {
            Prefs.setClientInfo(it)
            setClientDataNav()
        })

        viewModel.cartProductsData.observe(this, Observer {
            val menu = nav_bottom.menu
            val item = menu.findItem(R.id.cartFragment)
            val badge = nav_bottom.getOrCreateBadge(item.itemId)
            badge.isVisible = it?.count() != 0
            badge.number = it?.count() ?: 0
            badge.badgeTextColor = Color.WHITE

            var totalAmount = 0.0
            it?.forEach {
                totalAmount += it.cartCount * it.price!!
            }
        })

        viewModel.storeInfoData.observe(this, Observer {
            Prefs.setStoreInfo(it)
        })

        nav_bottom.setOnNavigationItemSelectedListener { item: MenuItem ->
            if (Prefs.getStore() == null){
                showWarning("Пожалуйста, сначала выберите склад.")
                return@setOnNavigationItemSelectedListener false
            }

            return@setOnNavigationItemSelectedListener when(item.itemId){
                R.id.homeStores -> {
                    imgSearch.visibility = View.GONE

                    tvTitle.visibility = View.VISIBLE
                    edSearch.visibility = View.GONE
                    if (storesFragment.isAdded && storesFragment.isVisible){

                    }else{
                        hideFragments()
                        if (!storesFragment.isAdded){
                            supportFragmentManager.beginTransaction()
                                .add(R.id.container, storesFragment)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .show(storesFragment)
                                .commitAllowingStateLoss()
                        }
                    }
                    true
                }
                R.id.homeFragment -> {
                    imgSearch.setImageResource(R.drawable.ic_search_black_24dp)
                    imgSearch.visibility = View.VISIBLE

                    imgSearch.setOnClickListener {
                        startActivity<SearchProductActivity>()
                    }

                    tvTitle.visibility = View.VISIBLE
                    edSearch.visibility = View.GONE
                    if (homeFragment.isAdded && homeFragment.isVisible){

                    }else{
                        hideFragments()
                        if (!homeFragment.isAdded){
                            supportFragmentManager.beginTransaction()
                                .add(R.id.container, homeFragment)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .show(homeFragment)
                                .commitAllowingStateLoss()
                        }
                    }
                    true
                }
                R.id.favouriteFragment -> {
                    imgSearch.setImageResource(R.drawable.ic_search_black_24dp)
                    imgSearch.visibility = View.VISIBLE

                    imgSearch.setOnClickListener {
                        startActivity<SearchProductActivity>()
                    }

                    tvTitle.visibility = View.VISIBLE
                    edSearch.visibility = View.GONE
                    if (favouriteFragment.isAdded && favouriteFragment.isVisible){

                    }else{
                        hideFragments()
                        if (!favouriteFragment.isAdded){
                            supportFragmentManager.beginTransaction()
                                .add(R.id.container, favouriteFragment)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .show(favouriteFragment)
                                .commitAllowingStateLoss()
                        }
                    }
                    true
                }
                R.id.cartFragment -> {
                    imgSearch.setImageResource(R.drawable.ic_clear_black_24dp)
                    imgSearch.visibility = View.VISIBLE
                    imgSearch.setOnClickListener {
                        Prefs.clearCart()
                        EventBus.getDefault().post(EventModel(Constants.EVENT_UPDATE_BASKET, Prefs.getCartList().count()))
                    }

                    tvTitle.visibility = View.VISIBLE
                    edSearch.visibility = View.GONE
                    if (cartFragment.isAdded && cartFragment.isVisible){

                    }else{
                        hideFragments()
                        if (!cartFragment.isAdded){
                            supportFragmentManager.beginTransaction()
                                .add(R.id.container, cartFragment)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .show(cartFragment)
                                .commitAllowingStateLoss()
                        }
                    }
                    true
                }
                R.id.ordersFragment -> {
                    imgSearch.visibility = View.GONE
                    tvTitle.visibility = View.VISIBLE
                    edSearch.visibility = View.GONE
                    if (ordersFragment.isAdded && ordersFragment.isVisible){

                    }else{
                        hideFragments()
                        if (!ordersFragment.isAdded){
                            supportFragmentManager.beginTransaction()
                                .add(R.id.container, ordersFragment)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .show(ordersFragment)
                                .commitAllowingStateLoss()
                        }
                    }
                    true
                }
                else -> true
            }
        }

        val toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigation.setNavigationItemSelectedListener(this)

        imgMore.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

        tvTitle.text = Prefs.getStore()?.name

        imgSearch.setOnClickListener {
            startActivity<SearchProductActivity>()
        }

        if (intent.hasExtra(Constants.EXTRA_DATA_START_FRAGMENT)){
            nav_bottom.selectedItemId = intent.getIntExtra(Constants.EXTRA_DATA_START_FRAGMENT, R.id.homeStores)
        }
        if (nav_bottom.selectedItemId == R.id.homeStores){
            pushFragment(R.id.container, storesFragment, storesFragment.tag ?: "")
        }

        setClientDataNav()

        NetworkUtils.registerNetworkStatusChangedListener(object: NetworkUtils.OnNetworkStatusChangedListener{
            override fun onConnected(networkType: NetworkUtils.NetworkType?) {
                showConnection(notConnection = false)
            }

            override fun onDisconnected() {
                showConnection(notConnection = true)
                loadData()
            }
        })
    }

    fun setClientDataNav(){
        val user = Prefs.getClientInfo()
        val headerView = navigation.getHeaderView(0)
        headerView.tvPersonName.text = user?.name
        headerView.tvPhone.text = user?.phone
        headerView.imgCurrency.setImageResource(Prefs.getCurrency().getImage())
        headerView.imgCurrency.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
            val view = layoutInflater.inflate(R.layout.select_currency_dialog, null)
            bottomSheetDialog.setContentView(view)
            if (Prefs.getCurrency() == CurrencyEnum.USD){
                view.imgUSDChecked.visibility = View.VISIBLE
                view.imgUZSChecked.visibility = View.GONE
            }else{
                view.imgUZSChecked.visibility = View.VISIBLE
                view.imgUSDChecked.visibility = View.GONE
            }

            view.lyUSD.setOnClickListener {
                Prefs.setCurrency(CurrencyEnum.USD)
                headerView.imgCurrency.setImageResource(Prefs.getCurrency().getImage())
                bottomSheetDialog.dismiss()
                EventBus.getDefault().post(EventModel(EVENT_UPDATE_BASKET, 0))
            }

            view.lyUZS.setOnClickListener {
                Prefs.setCurrency(CurrencyEnum.UZS)
                headerView.imgCurrency.setImageResource(Prefs.getCurrency().getImage())
                bottomSheetDialog.dismiss()
                EventBus.getDefault().post(EventModel(EVENT_UPDATE_BASKET, 0))
            }

            bottomSheetDialog.show()
        }
    }

    override fun updateStore() {
        super.updateStore()
        viewModel.getStoreInfo()
        homeFragment.loadData()
        tvTitle.text = Prefs.getStore()?.name

        tvTitle.visibility = View.VISIBLE
        edSearch.visibility = View.GONE

        nav_bottom.selectedItemId = R.id.homeFragment
//        if (homeFragment.isAdded && homeFragment.isVisible){
//
//        }else{
//            hideFragments()
//            if (!homeFragment.isAdded){
//                supportFragmentManager.beginTransaction()
//                    .add(R.id.container, homeFragment)
//                    .commitAllowingStateLoss()
//            }else{
//                supportFragmentManager.beginTransaction()
//                    .show(homeFragment)
//                    .commitAllowingStateLoss()
//            }
//        }
//        true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun loadData() {
        viewModel.clientInfo(ClientInfoRequest(fcm_token = Prefs.getFCM() ?: ""))
        viewModel.getCartProducts()
        if (Prefs.getStore() != null){
            viewModel.getStoreInfo()
        }
    }

    override fun initData() {

    }

    override fun updateData() {

    }

    override fun onRefresh() {

    }

    @Subscribe
    fun onEvent(event: EventModel<Int>){
        if (event.event == Constants.EVENT_UPDATE_BADGE_COUNT){
            val menu = nav_bottom.menu
            val item = menu.findItem(R.id.cartFragment)
            val badge = nav_bottom.getOrCreateBadge(item.itemId)
            badge.isVisible = event.data != 0
            badge.number = event.data
            badge.badgeTextColor = Color.WHITE
            loadData()
        }else if (event.event == Constants.EVENT_LOGOUT){
            Prefs.clearAll()
            startClearActivity<SplashActivity>()
            finish()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        if (p0.itemId == R.id.actionNews){
            startActivity<NewsActivity>()
        }else if (p0.itemId == R.id.actionAct){
            startActivity<ActReportActivity>()
        }else if (p0.itemId == R.id.actionShareApp){
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT, "Друзья, я предлагаю вам это приложение: "
                        + getString(R.string.app_name)
                        + "\n https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")

            startActivity(Intent.createChooser(shareIntent,"Отправить своим друзьям."))
        }else if(p0.itemId == R.id.actionProfile){
            startActivity<ProfileEditActivity>()
        }else if(p0.itemId == R.id.actionLogout){
            Prefs.clearAll()
            startClearActivity<SplashActivity>()
            finish()
        }else if(p0.itemId == R.id.actionLanguage){
            val bottomSheetDialog = BottomSheetDialog(this)
            val viewLang = layoutInflater.inflate(R.layout.bottomsheet_language, null)
            bottomSheetDialog.setContentView(viewLang)
            viewLang.tvUzbCr.setOnClickListener {
                Prefs.setLang("uz")
                setNewLocale(this,"uz")
                bottomSheetDialog?.dismiss()
                startClearActivity<SplashActivity>()
                finish()
            }
            viewLang.tvRu.setOnClickListener {
                Prefs.setLang("en")
                setNewLocale(this,"en")
                bottomSheetDialog?.dismiss()
                startClearActivity<SplashActivity>()
                finish()
            }

            bottomSheetDialog.show()
        }else{
            startActivity<AboutAppActivity>()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {
            // Permission has already been granted
            callPhone()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 42) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            } else {
                // permission denied, boo! Disable the
                // functionality
            }
            return
        }
    }

    @SuppressLint("MissingPermission")
    fun callPhone(){
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+998 71 200 47 00"))
        startActivity(intent)
    }
}
