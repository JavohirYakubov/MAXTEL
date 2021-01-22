package uz.isti.maxtel.screen.main.cart.makeorder.preorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_pre_order.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.base.startActivity
import uz.isti.maxtel.model.MakeOrderModel
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.main.cart.makeorder.preorder.success.SuccessOrderActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs
import uz.isti.maxtel.view.adapter.OrderProductAdapter

class PreOrderActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_pre_order
    lateinit var order: MakeOrderModel
    lateinit var viewModel: MainViewModel

    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        order = intent.getSerializableExtra(Constants.EXTRA_DATA) as MakeOrderModel

        imgBack.setOnClickListener {
            finish()
        }

        viewModel.progress.observe(this, Observer {
            setProgress(it)
        })

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.createOrderData.observe(this, Observer {
            Prefs.clearCart()
            startActivity<SuccessOrderActivity>(Constants.EXTRA_DATA, it.PaymeURL ?: "", Constants.EXTRA_DATA_2,
                0)
        })

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = OrderProductAdapter(order.array)

        var productAmount = 0.0
        order.array.forEach {
            productAmount += it.psumma
        }


        if (order.delivery == 0){
            tvOrderType.text = getString(R.string.delivery)
        }else{
            tvOrderType.text = getString(R.string.pickup)
        }

        if (order.payme){
            tvPaymentType.text = getString(R.string.payme)
        }else{
            tvPaymentType.text = getString(R.string.cashback)
        }

        tvProductAmount.text = String.format("%.2f", productAmount).replace(",", ".") + " " + (if (order.dollar) "$" else "сум")
        tvDeliveryAmount.text = String.format("%.2f", order.deliverySumma).replace(",", ".") + " сум"
        tvCurrency.text =  (if (order.dollar) "$" else "сум")

        tvTotalAmount.text = String.format("%.2f", order.summa).replace(",", ".")  + " " + Prefs.getCurrency().getName()
        tvCashback.text = String.format("%.2f", order.cashbackForOrder).replace(",", ".")  + " сум"
        tvCashbackAmount.text = String.format("%.2f", order.cashbackAmount).replace(",", ".")  + " сум"

        cardViewOk.setOnClickListener {
            viewModel.createOrder(order)
        }
    }

    override fun loadData() {

    }

    override fun initData() {

    }

    override fun updateData() {

    }
}
