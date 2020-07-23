package uz.isti.maxtel.screen.main.favourite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_favourite.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseFragment
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.base.showInfo
import uz.isti.maxtel.base.startActivity
import uz.isti.maxtel.model.EventModel
import uz.isti.maxtel.model.ProductModel
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.product.detail.ProductDetailFragment
import uz.isti.maxtel.screen.main.product.detail.ProductDetailListener
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.ProductsAdapter
import uz.isti.maxtel.view.adapter.ProductsAdapterListener

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun getLayout(): Int = R.layout.fragment_favourite
    lateinit var viewModel: MainViewModel
    var adapter: ProductsAdapter? = null

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

        viewModel.progressFavouriteProducts.observe(this, Observer {inProgress ->
            swipeRefresh.isRefreshing = inProgress
        })

        viewModel.favouriteProductsData.observe(this, Observer {
            setItems()
        })

        swipeRefresh.setOnRefreshListener(this)

    }

    override fun loadData() {
        viewModel.getFavouriteProducts()
    }

    fun setItems(){
        if (viewModel.favouriteProductsData.value != null){
            recycler.layoutManager = GridLayoutManager(activity, 2)
            adapter = ProductsAdapter(viewModel.favouriteProductsData?.value ?: emptyList(), object:
                ProductsAdapterListener {
                override fun getPage(index: Int) {
                    //
                }

                override fun onClickItem(item: Any?) {
                    val item = item as ProductModel

                    val fragment = ProductDetailFragment(object: ProductDetailListener {
                        override fun onHideDialog() {
                            EventBus.getDefault().post(EventModel(Constants.EVENT_UPDATE_BASKET, Prefs.getCartList().count()))
                            loadData()
                        }
                    })

                    fragment.id = item?.id
                    fragment.show(activity!!.supportFragmentManager, fragment.tag)
                }
            })
            recycler.adapter = adapter
        }
    }
    override fun setData() {

    }

    override fun onRefresh() {
        adapter = null
        loadData()
    }

    @Subscribe
    fun onEvent(event: EventModel<Any>){
        if (event.event == Constants.EVENT_UPDATE_FAVOURITES){
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
