package uz.isti.maxtel.screen.main.cart.makeorder.preorder.success

import kotlinx.android.synthetic.main.activity_success_order.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.screen.main.MainActivity
import uz.isti.maxtel.screen.main.webview.AppWebViewActivity
import uz.isti.maxtel.utils.Constants

class SuccessOrderActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_success_order
    lateinit var url: String
    var summa = 0

    override fun initViews() {
        url = intent.getStringExtra(Constants.EXTRA_DATA)
        summa = intent.getIntExtra(Constants.EXTRA_DATA_2, 0)

        if (summa > 0){
           tvOkButtonText.text = getString(R.string.do_pay)
            cardViewOk.setOnClickListener {
                startClearActivity<MainActivity>()
                startActivityToOpenUrlInBrowser(url)
            }
        }else{
            cardViewOk.setOnClickListener {
                startClearTopActivity<MainActivity>()
            }
        }
    }

    override fun onBackPressed() {
        startClearActivity<MainActivity>()
    }

    override fun loadData() {

    }

    override fun initData() {

    }

    override fun updateData() {

    }

}
