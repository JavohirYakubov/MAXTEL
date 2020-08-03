package uz.isti.maxtel.screen.main.product

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_product_list.*
import kotlinx.android.synthetic.main.select_currency_dialog.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.base.startClearActivity
import uz.isti.maxtel.model.*
import uz.isti.maxtel.model.enum.CurrencyEnum
import uz.isti.maxtel.screen.main.MainActivity
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.product.detail.ProductDetailFragment
import uz.isti.maxtel.screen.main.product.detail.ProductDetailListener
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.ProductsAdapter
import uz.isti.maxtel.view.adapter.ProductsAdapterListener

class ProductListActivity  : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    override fun getLayout(): Int = R.layout.activity_product_list
    lateinit var viewModel: MainViewModel
    lateinit var brand: BrandModel
    lateinit var section: SectionModel
    var adapter: ProductsAdapter? = null

    override fun initViews() {
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }

        brand = intent.getSerializableExtra(Constants.EXTRA_DATA) as BrandModel
        section = intent.getSerializableExtra(Constants.EXTRA_DATA_2) as SectionModel
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        imgBack.setOnClickListener {
            finish()
        }

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.progressProducts.observe(this, Observer {
            setProgress(it)
        })

        viewModel.productsData.observe(this, Observer {
            updateData()
        })

        flCart.setOnClickListener {
            startClearActivity<MainActivity>(Constants.EXTRA_DATA_START_FRAGMENT, R.id.cartFragment)
        }

        imgCurrency.setOnClickListener {
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
                loadData()
                imgCurrency.setImageResource(Prefs.getCurrency().getImage())
                bottomSheetDialog.dismiss()
            }

            view.lyUZS.setOnClickListener {
                Prefs.setCurrency(CurrencyEnum.UZS)
                loadData()
                imgCurrency.setImageResource(Prefs.getCurrency().getImage())
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }


        imgSearch.setOnClickListener {
            if (edSearch.visibility == View.VISIBLE){
                edSearch.visibility = View.GONE
                imgSearch.setImageResource(R.drawable.ic_search_black_24dp)
                edSearch.setText("")
                tvTitle.visibility = View.VISIBLE
            }else{
                edSearch.visibility = View.VISIBLE
                imgSearch.setImageResource(R.drawable.ic_clear_black_24dp)
                tvTitle.visibility = View.GONE
            }
        }

        edSearch.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               adapter?.updateItems(viewModel.productsData.value?.filter { it.name.toLowerCase().indexOf(edSearch.text.toString().toLowerCase()) != -1 } ?: emptyList())
            }
        })

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
        viewModel.getProductsByBrandId(brand.id, section.id)
        checkCart()
    }

    override fun initData() {
        imgCurrency.setImageResource(Prefs.getCurrency().getImage())
        tvTitle.text = brand.name
        val cart = Prefs.getCartModel()
        if (cart?.count ?: 0 > 0){
            flCart.visibility = View.VISIBLE
            tvCartCount.text = getString(R.string.cart_).toUpperCase() + " (${cart?.count.toString()})"
            tvCartAmount.text = cart?.totalAmount.formattedAmount()
        }else{
            flCart.visibility = View.GONE
        }

        swipeRefresh.setOnRefreshListener(this)
    }

    override fun updateData() {
        if (viewModel.productsData.value != null ){
            recycler.layoutManager = GridLayoutManager(this, 2)
            adapter = ProductsAdapter(viewModel.productsData?.value ?: emptyList(), object:
                ProductsAdapterListener {
                override fun getPage(index: Int) {
                    //
                }

                override fun onClickItem(item: Any?) {
                    val item = item as ProductModel
                    val fragment = ProductDetailFragment(object: ProductDetailListener{
                        override fun onHideDialog() {
                            EventBus.getDefault().post(EventModel(Constants.EVENT_UPDATE_BASKET, Prefs.getCartList().count()))
                            loadData()
                        }
                    })
                    fragment.id = item?.id
                    fragment.show(supportFragmentManager, fragment.tag)
                }
            })
            recycler.adapter = adapter
        }
    }

    override fun onRefresh() {
        swipeRefresh.isRefreshing = false
        adapter = null
        loadData()
    }

    @Subscribe
    fun onEvent(event: EventModel<Int>){

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
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

}
