package uz.isti.maxtel.screen.main.actreport

import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_act_report.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.model.StoreSimpleModel
import uz.isti.maxtel.model.enum.CurrencyEnum
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.ActReportAdapter
import uz.isti.maxtel.view.custom.DateRangeFragment
import uz.isti.maxtel.view.custom.DateRangeFragmentListener


class ActReportActivity : BaseActivity(), DateRangeFragmentListener {
    override fun getLayout(): Int = R.layout.activity_act_report
    lateinit var viewModel: MainViewModel

    var startDate = ""
    var endDate = ""
    var storeId = Prefs.getStore()?.id
    var currency = Prefs.getCurrency()
    var stores: List<StoreSimpleModel>? = null

    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.progress.observe(this, Observer {
            setProgress(it)
        })

        viewModel.progressRegions.observe(this, Observer {
            setProgress(it)
        })

        viewModel.actReport.observe(this, Observer {
            updateData()
        })

        viewModel.storesData.observe(this, Observer {
            initData()
        })

        imgBack.setOnClickListener{
            finish()
        }

        lyDate.setOnClickListener {
            val fragment = DateRangeFragment(this)
            fragment.show(supportFragmentManager, fragment.tag)
        }

        lyStore.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            val menu = popupMenu.menu
            var i = 0
            viewModel.storesData.value?.forEach {
                menu.add(i, i, i, it.name)
            }

            popupMenu.setOnMenuItemClickListener {
                val item = viewModel.storesData.value!![it.itemId]
                tvStore.text = item.name
                storeId = item.id ?: ""
                loadAct()
                return@setOnMenuItemClickListener true
            }

            popupMenu.show()
        }

        lyCurrency.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            val menu = popupMenu.menu
            menu.add(1, 1, 1, "Сум")
            menu.add(2, 2, 2, "$")
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == 1){
                    currency = CurrencyEnum.UZS
                }else{
                    currency = CurrencyEnum.USD
                }
                tvCurrency.text = currency.name
                loadAct()
                return@setOnMenuItemClickListener true
            }

            popupMenu.show()
        }
    }

    override fun loadData() {
        viewModel.getStores()
    }

    override fun initData() {
        tvStore.text = Prefs.getStore()?.name
        tvCurrency.text = Prefs.getCurrency().getName()
    }

    override fun updateData() {
        if (viewModel.actReport.value != null){
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = ActReportAdapter(viewModel.actReport.value!!)
        }
    }

    override fun onResult(startDate: String, endDate: String) {
        this.startDate = startDate
        this.endDate = endDate
        tvStartDate.text = startDate
        tvEndDate.text = endDate
        loadAct()
    }

    fun loadAct(){
        viewModel.getActReport(startDate.replace(".", ""), endDate.replace(".", ""),  storeId ?: "", if(currency == CurrencyEnum.USD) 1 else 0)
    }
}
