package uz.isti.maxtel.screen.main.home

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_home.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.CategoryModel
import uz.isti.maxtel.model.SectionModel
import uz.isti.maxtel.model.enum.OfferTypeEnum
import uz.isti.maxtel.screen.auth.SignActivity
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.manufacturer.ManufacturerListActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.*
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun getLayout(): Int = R.layout.fragment_home
    lateinit var viewModel: MainViewModel
    var selectedCategory: CategoryModel? = null
    var isSetup = false

    override fun setupViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        isSetup = true

        viewModel.error.observe(this, Observer {
            activity?.showError(it)
        })

        viewModel.progressCategories.observe(this, Observer { inProgress ->
            getBaseActivity {
                it.setProgress(inProgress)
            }
        })

        viewModel.progressSections.observe(this, Observer {
            swipeRefresh.isRefreshing = it
        })

        viewModel.sectionsData.observe(this, Observer {
            setSections()
        })

        swipeRefresh.setOnRefreshListener(this)
    }

    fun setSections(){
        if (viewModel.sectionsData.value != null){
            recyclerSubCategory.layoutManager = GridLayoutManager(activity, 1)
            recyclerSubCategory.adapter = SectionsAdapter(viewModel.sectionsData.value!!, object : BaseAdapterListener{
                override fun onClickItem(item: Any?) {
                    activity?.startActivity<ManufacturerListActivity>(Constants.EXTRA_DATA, (item as SectionModel) as Serializable, Constants.EXTRA_DATA_2, selectedCategory as Serializable)
                }
            })
        }
    }
    override fun loadData() {
        if (isSetup){
            viewModel.getSections()
        }
    }

    override fun setData() {

    }


    override fun onRefresh() {
        loadData()
    }

}
