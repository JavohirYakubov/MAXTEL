package uz.isti.maxtel.screen.main.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import q.rorbin.badgeview.QBadgeView

import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.CartEventModel
import uz.isti.maxtel.model.EventModel
import uz.isti.maxtel.model.ProductModel
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.cart.makeorder.MakeOrderActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Constants.Companion.EVENT_UPDATE_CART
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.CartProductsAdapter
import uz.isti.maxtel.view.adapter.CartProductsListener
import uz.isti.maxtel.view.adapter.ProductsAdapter
import java.io.Serializable
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class CartFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun getLayout(): Int = R.layout.fragment_cart
    lateinit var viewModel: MainViewModel
    var adapter: CartProductsAdapter? = null
    var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }
    override fun setupViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.error.observe(this, Observer {
            activity?.showError(it)
        })

        viewModel.progressCartProducts.observe(this, Observer {inProgress ->
            getBaseActivity {
                it.setProgress(inProgress)
            }
        })

        viewModel.cartProductsData.observe(this, Observer {
            setItems()
        })

        swipeRefresh.setOnRefreshListener(this)

        cardViewAdd.setOnClickListener {
            getBaseActivity {
//                if ((viewModel.storeInfoData.value?.minimalSavdoSummasi ?: 0) != 0 && (Prefs.getStoreInfo()?.minimalSavdoSummasi ?: 0) > totalAmount){
//                    it.showWarning(getString(R.string.minimum_summa, (Prefs.getStoreInfo()?.minimalSavdoSummasi ?: 0).toDouble().formattedAmount()))
//                    return@getBaseActivity
//                }
                it.startActivity<MakeOrderActivity>(Constants.EXTRA_DATA, viewModel.cartProductsData.value as Serializable, Constants.EXTRA_DATA_2, totalAmount)
            }
        }

    }

    override fun loadData() {
        viewModel.getCartProducts()
    }

    fun setItems(){
        getBaseActivity {
            EventBus.getDefault().post(EventModel(Constants.EVENT_UPDATE_BADGE_COUNT, viewModel.cartProductsData.value?.count() ?: 0))
        }

        if (viewModel.cartProductsData.value != null){
            if (viewModel.cartProductsData.value!!.count() > 0){
                cardViewAdd.visibility = View.VISIBLE
            }else{
                cardViewAdd.visibility = View.GONE
            }
            recycler.layoutManager = LinearLayoutManager(activity)
            adapter = CartProductsAdapter(viewModel.cartProductsData.value ?: emptyList(), object : CartProductsListener{
                override fun refreshTotalAmount() {
                    setTotalAmounts()
                    if (adapter?.itemCount == 0){
                        loadData()
                    }
                }

                override fun onClickItem(item: Any?) {
                    //
                }
            })
            recycler.adapter = adapter
            setTotalAmounts()
        }else{
            cardViewAdd.visibility = View.GONE
        }
    }

    fun setTotalAmounts(){
        totalAmount = 0.0
        adapter?.items?.forEach {
            val item = it as ProductModel
            totalAmount += item.cartCount * item.price!!

        }

        Prefs.setCartModel(CartEventModel(Constants.EVENT_UPDATE_CART, adapter?.itemCount ?: 0, totalAmount))
        EventBus.getDefault().post(EventModel(EVENT_UPDATE_CART, 0))
        tvTotalAmount.text = totalAmount.formattedAmount()
    }

    override fun setData() {

    }

    override fun onRefresh() {
        swipeRefresh.isRefreshing = false
        loadData()
    }

    @Subscribe
    fun onEvent(event: EventModel<Int>){
        if (event.event == Constants.EVENT_UPDATE_BASKET){
            loadData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }
}
