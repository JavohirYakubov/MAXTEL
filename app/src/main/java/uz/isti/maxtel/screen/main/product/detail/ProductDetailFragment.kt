package uz.isti.maxtel.screen.main.product.detail

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.product_item_layout.view.*
import org.greenrobot.eventbus.EventBus
import uz.isti.maxtel.App

import uz.isti.maxtel.R
import uz.isti.maxtel.base.formattedAmount
import uz.isti.maxtel.base.loadImage
import uz.isti.maxtel.base.showSuccess
import uz.isti.maxtel.base.showWarning
import uz.isti.maxtel.model.BasketModel
import uz.isti.maxtel.model.EventModel
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Constants.Companion.EVENT_UPDATE_BASKET
import uz.isti.maxtel.utils.Prefs

/**
 * A simple [Fragment] subclass.
 */
interface ProductDetailListener{
    fun onHideDialog()
}

class ProductDetailFragment(val listener: ProductDetailListener) : BottomSheetDialogFragment() {
    lateinit var viewModel: MainViewModel
    var id: String = ""
    var cartCount = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SheetDialog)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.progressProductDetail.observe(this, Observer {
            if (it){
                flProgress.visibility = View.VISIBLE
            }else{
                flProgress.visibility = View.GONE
            }
        })

        viewModel.productDetailData.observe(this, Observer {
            setProduct()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgFavourite.setOnClickListener {
            val product = viewModel.productDetailData.value
            if (product != null){
                product.favourite = !product.favourite
                Prefs.setFavouriteItem(product.id!!, product.favourite)
                setProduct()
                EventBus.getDefault().post(EventModel(Constants.EVENT_UPDATE_FAVOURITES, 0))
            }
        }

        imgMinus.setOnClickListener {
            if (cartCount > 0){
                cartCount--
                tvProductCount.setText(cartCount.toString())
                updateCartAmounts()
            }
        }

        imgPlus.setOnClickListener {
            val product = viewModel.productDetailData.value
            if (cartCount > product!!.productCount){
                activity?.showWarning("Вы выбрали максимальное количество товара.")
                return@setOnClickListener
            }

            cartCount++
            tvProductCount.setText(cartCount.toString())
            updateCartAmounts()
        }

        cardViewAdd.setOnClickListener {
            val product = viewModel.productDetailData.value
            if (product != null){
                var cart = BasketModel(product.id!!, cartCount)
                Prefs.add2Cart(cart)
                if (cartCount > 0){
                    activity?.showSuccess(getString(R.string.cart_updated))
                }else{
                    activity?.showSuccess(getString(R.string.product_removed))
                }
                listener.onHideDialog()
                EventBus.getDefault().post(EventModel(EVENT_UPDATE_BASKET, 0))
                dismiss()
            }
        }

        tvProductCount.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0){
                    val productCount = tvProductCount.text.toString().toIntOrNull() ?: 0

                    val product = viewModel.productDetailData.value
                    if (productCount > product!!.productCount){
                        activity?.showWarning("Вы выбрали максимальное количество товара.")
                        tvProductCount.setText("${product.productCount}")
                        cartCount = product.productCount
                    }else{
                        cartCount = productCount
                        updateCartAmounts()
                    }
                }
            }
        })
        viewModel.getProductDetail(id)
    }

    fun setProduct(){
        lyCart.visibility = View.VISIBLE
        val product = viewModel.productDetailData.value

        Prefs.getCartList().forEach {
            if (it.id == product?.id){
                cartCount = it.count
            }
        }

        if (product?.discount_percent!! > 0){
            cardViewDiscount.visibility = View.VISIBLE
            flProductOldPrice.visibility = View.VISIBLE

            tvDiscountPercent.text = "-" + product?.discount_percent.toString() + "%"
            tvProductOldPrice.text = product?.old_price?.formattedAmount()
        }else{
            cardViewDiscount.visibility = View.GONE
            flProductOldPrice.visibility = View.GONE
        }

        imgProduct.loadImage(App.imageBaseUrl + product?.image)
        tvTitle.text = product?.name
        tvDescription.text = product?.information
        if (product?.information.isNullOrEmpty()){
            tvDescription.visibility = View.VISIBLE
        }
        tvPrice.text = product?.price?.formattedAmount()
        product?.favourite = Prefs.isFavourite(product?.id!!)

        if (product?.favourite == true){
            imgFavourite.setImageResource(R.drawable.ic_fill_heart)
        }else{
            imgFavourite.setImageResource(R.drawable.ic_heart)
        }
        updateCartAmounts()
    }

    fun updateCartAmounts(){
        val product = viewModel.productDetailData.value
        tvTotalAmount.text = ((product?.price ?: 0.0) * cartCount).formattedAmount()
    }


}
