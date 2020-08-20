
package uz.isti.maxtel.screen.main.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search_prodict.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.CartEventModel
import uz.isti.maxtel.model.EventModel
import uz.isti.maxtel.model.ProductModel
import uz.isti.maxtel.screen.auth.SignActivity
import uz.isti.maxtel.screen.main.MainActivity
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.product.detail.ProductDetailFragment
import uz.isti.maxtel.screen.main.product.detail.ProductDetailListener
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.ProductsAdapter
import uz.isti.maxtel.view.adapter.ProductsAdapterListener

class SearchProductActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_search_prodict
    lateinit var viewModel: MainViewModel

    override fun initViews() {
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
        imgBack.setOnClickListener {
            finish()
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
//
//        viewModel.progress.observe(this, Observer {
//            setProgress(it)
//        })

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.searchProductsData.observe(this, Observer {
            initData()
        })

        edSearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edSearch.text.length >= 2){
                    viewModel.getProductsByName(edSearch.text.toString())
                }else{
                    recycler.adapter = null
                }
            }
        })

        flCart.setOnClickListener {
            startClearActivity<MainActivity>(Constants.EXTRA_DATA_START_FRAGMENT, R.id.cartFragment)
        }
    }

    override fun loadData() {

    }

    override fun initData() {
        val cart = Prefs.getCartModel()
        if (cart?.count ?: 0 > 0){
            flCart.visibility = View.VISIBLE
            tvCartCount.text = getString(R.string.cart_).toUpperCase() + " (${cart?.count.toString()})"
            tvCartAmount.text = cart?.totalAmount.formattedAmount()
        }else{
            flCart.visibility = View.GONE
        }

        if (viewModel.searchProductsData.value != null){
            recycler.layoutManager = GridLayoutManager(this, 2)
            recycler.adapter = ProductsAdapter(viewModel.searchProductsData.value ?: emptyList(), object : ProductsAdapterListener{
                override fun getPage(index: Int) {

                }

                override fun onClickItem(item: Any?) {
                    if (Prefs.getToken().isNullOrEmpty()){
                        showWarning(getString(R.string.please_use_all_features_registr))
                        startActivity<SignActivity>()
                        return
                    }
                    val item = item as ProductModel
                    val fragment = ProductDetailFragment(object: ProductDetailListener {
                        override fun onHideDialog() {
                            EventBus.getDefault().post(EventModel(Constants.EVENT_UPDATE_BASKET, Prefs.getCartList().count()))
                            loadData()
                        }
                    })
                    fragment.id = item?.id
                    fragment.show(supportFragmentManager, fragment.tag)
                }
            })
        }
    }

    override fun updateData() {

    }
    @Subscribe
    fun onEvent(cartEventModel: CartEventModel){
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
