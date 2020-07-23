package uz.isti.maxtel.screen.auth

import android.content.IntentFilter
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.redmadrobot.inputmask.MaskedTextChangedListener
import cuz.isti.maxtel.services.sms.SmsReceiverService
import kotlinx.android.synthetic.main.activity_sign.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.AddressModel
import uz.isti.maxtel.screen.main.MainActivity
import uz.isti.maxtel.screen.main.map.MapActivity
import uz.isti.maxtel.screen.main.profile.edit.ProfileEditActivity
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs

enum class SignState{
    PHONE,
    CODE,
    REGISTRATION
}

class SignActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_sign

    lateinit var viewModel: SignViewModel

    var phoneListener: MaskedTextChangedListener? = null
    var codeListener: MaskedTextChangedListener? = null
    var dateListener: MaskedTextChangedListener? = null

    var state = SignState.PHONE


    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(SignViewModel::class.java)

        phoneListener = MaskedTextChangedListener(
            "+998 ([00]) [000] [00] [00]",
            true,
            edPhone,
            null,
            null
        )
        edPhone.addTextChangedListener(phoneListener)
        edPhone.onFocusChangeListener = phoneListener

        codeListener = MaskedTextChangedListener(
            "[0000]",
            true,
            edCode,
            null,
            null
        )

        edCode.addTextChangedListener(codeListener)
        edCode.onFocusChangeListener = codeListener

        dateListener = MaskedTextChangedListener(
            "[00].[00].[0000]",
            true,
            edBirthDay,
            null,
            null
        )
        edBirthDay.addTextChangedListener(dateListener)
        edBirthDay.onFocusChangeListener = dateListener

        btnContinue.setOnClickListener {
            changeState()
        }

        viewModel.progress.observe(this, Observer {
            setProgress(it)
        })

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.loginResponse.observe(this, Observer {
            if (it.isRegistered){
                state = SignState.CODE
            }else{
                state = SignState.REGISTRATION
            }
            updateData()
        })

        viewModel.loginConfirmResponse.observe(this, Observer {
            if (it != null){
                Prefs.setToken(it.token)
                startClearActivity<MainActivity>()
                finish()
            }
        })

        initReceiver()

        updateData()

        Prefs.setShowSale(true)
    }

    override fun loadData() {

    }

    override fun initData() {

    }

    override fun updateData() {
        tilFullName.visibility = View.GONE
        tilCode.visibility = View.GONE

        when(state){
            SignState.PHONE ->{
                tvTitle.text = getString(R.string.sign)
                tvComment.text = getString(R.string.input_phone_number)
                edPhone.visibility = View.VISIBLE
                edPhone.isEnabled = true
            }

            SignState.CODE ->{
                tvTitle.text = getString(R.string.confirm)
                tvComment.text = getString(R.string.please_input_code)
                edPhone.isEnabled = false
                tilCode.visibility = View.VISIBLE
            }

            SignState.REGISTRATION ->{
                tvTitle.text = "Регистрация"
                tvComment.text = getString(R.string.reg_thanks)
                edPhone.isEnabled = false
                tilCode.visibility = View.VISIBLE
                tilFullName.visibility = View.VISIBLE
                tilStore.visibility = View.VISIBLE
                tilBirthDay.visibility = View.VISIBLE
            }
        }
    }

    fun changeState(){
        when(state){
            SignState.PHONE ->{
                val phone = edPhone.text.toString().trim().replace("+", "").replace("(", "").replace(")", "").replace("\\s".toRegex(), "")
                if (phone.length == 12){
                    viewModel.login(phone)
                }else{
                    showWarning(getString(R.string.please_input_all_fields))
                }
            }
            SignState.CODE ->{
                if (edCode.text.toString().length == 4) {
                    viewModel.loginConfirm(edCode.text.toString(), "", "", "")
                }else{
                    showWarning(getString(R.string.please_input_all_fields))
                }
            }
            SignState.REGISTRATION ->{
                if (edCode.text.toString().length == 4 && edFullName.text.length > 2) {
                    viewModel.loginConfirm(edCode.text.toString(), edStore.text.toString(), edFullName.text.toString(),
                        edBirthDay.text.toString().replace(".", ""))
                }else{
                    showWarning(getString(R.string.please_input_all_fields))
                }
            }
        }
    }

    fun initReceiver(){
        try{
            val smsReceiver = SmsReceiverService()
            smsReceiver?.setSmsListener {
                edCode.setText(it)
            }
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)

            val client = SmsRetriever.getClient(this)

            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }

            task.addOnFailureListener {
                // Fail to start API
            }
        }catch (e: Exception){}
    }


}
