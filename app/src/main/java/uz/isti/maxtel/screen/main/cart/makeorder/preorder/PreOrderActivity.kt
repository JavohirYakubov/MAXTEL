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
                if(order.Payme) order.summa else 0)
        })

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = OrderProductAdapter(order.array)

        tvAddress.text = order.address

        when(order.cashbox){
            0 ->{
                tvPaymentType.text = getString(R.string.cashback)
            }
            1 ->{
                tvPaymentType.text = getString(R.string.terminal)
            }
            2 ->{
                tvPaymentType.text = getString(R.string.payme)
            }
            3 ->{
                tvPaymentType.text = getString(R.string.transfer)
            }
        }

        var user = Prefs.getClientInfo()
        var totalAmount = order.deliverySumma.toDouble()
        var productAmount = 0.0
        order.array.forEach {
            productAmount += it.psumma
        }

        var salePersent = user?.forRegDiscount ?: 0
        if (user!!.usuallyDiscountMin1 >= productAmount && user.usuallyDiscountMin2 < productAmount){
            salePersent += user.usuallyDiscount1
        }else if (user!!.usuallyDiscountMin2 >= productAmount && user.usuallyDiscountMin3 < productAmount){
            salePersent += user.usuallyDiscount2
        }else if (user!!.usuallyDiscountMin3 >= productAmount){
            salePersent += user.usuallyDiscount3
        }

        var saleAmount = 0.0
        if (salePersent > 0){
            saleAmount = productAmount * (salePersent / 100.0)
        }
        totalAmount += (productAmount - saleAmount)

        tvProductAmount.text = productAmount.formattedAmount()
        tvDeliveryAmount.text = if(order.deliverySumma > 0) order.deliverySumma.toDouble().formattedAmount() else getString(R.string.free)
        tvSale.text = (-saleAmount).formattedAmount()
        tvTotalAmount.text = totalAmount.formattedAmount()
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
