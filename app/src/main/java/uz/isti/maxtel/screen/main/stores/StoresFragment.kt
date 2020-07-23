package uz.isti.maxtel.screen.main.stores

import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_stores.*
import uz.isti.maxtel.App

import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.StoreSimpleModel
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.BaseAdapterListener
import uz.isti.maxtel.view.adapter.StoresAdapter

/**
 * A simple [Fragment] subclass.
 */
class StoresFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun getLayout(): Int = R.layout.fragment_stores
    lateinit var viewModel: MainViewModel

    override fun setupViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.progressRegions.observe(this, Observer { inProgress ->
            swipeRefresh.isRefreshing = inProgress
        })

        viewModel.error.observe(this, Observer { message ->
            getBaseActivity {
                it.showError(message)
            }
        })

        viewModel.storesData.observe(this, Observer {
            setData()
        })

        viewModel.successAdsData.observe(this, Observer {
            setAds()
        })

        swipeRefresh.setOnRefreshListener(this)
    }

    override fun loadData() {
        viewModel.getStores()
        viewModel.getAds()
    }

    fun setAds(){
        if (viewModel.successAdsData.value != null){
            val data = viewModel.successAdsData.value

            carouselView.setImageListener { position, imageView ->
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                if (position == 0){
                    imageView.loadImage(App.imageBaseUrl + data?.reklama1)
                }
                if (position == 1){
                    imageView.loadImage(App.imageBaseUrl + data?.reklama2)
                }
                if (position == 2){
                    imageView.loadImage(App.imageBaseUrl + data?.reklama3)
                }
            }

            carouselView.pageCount = 3
            carouselView.visibility = View.VISIBLE
        }
    }

    override fun setData() {
        if (viewModel.storesData.value != null){
            viewModel.storesData.value!!.forEach { store ->
                if (Prefs.getStore()?.id == store.id){
                    store.checked = true
                }
            }
            recycler.layoutManager = LinearLayoutManager(activity)
            recycler.adapter = StoresAdapter(viewModel.storesData.value ?: emptyList(), object : BaseAdapterListener{
                override fun onClickItem(item: Any?) {
                    getBaseActivity {
                        if (Prefs.getCartList().count() > 0){
                            activity?.showWarning(getString(R.string.please_clear_cart))
                            return@getBaseActivity
                        }

                        Prefs.setStore(item as StoreSimpleModel)

                        it.updateStore()
                        it.showSuccess(getString(R.string.store_selected))
                    }
                }
            })
        }
    }

    override fun onRefresh() {
        loadData()
    }


}
