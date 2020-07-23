package uz.isti.maxtel.screen.main.profile.edit

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.redmadrobot.inputmask.MaskedTextChangedListener
import kotlinx.android.synthetic.main.activity_profile_edit.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.ClientInfoRequest
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.screen.splash.SplashActivity
import uz.isti.maxtel.utils.DateUtils
import uz.isti.maxtel.utils.Prefs

class ProfileEditActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_profile_edit
    lateinit var viewModel: MainViewModel

    var dateListener: MaskedTextChangedListener? = null

    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        imgBack.setOnClickListener {
            finish()
        }

        viewModel.progressClientInfo.observe(this, Observer {
            setProgress(it)
        })

        viewModel.progressRegions.observe(this, Observer {
            setProgress(it)
        })

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.clientInfoData.observe(this, Observer {
            Prefs.setClientInfo(it)
            showSuccess(getString(R.string.refreshed))
            startClearActivity<SplashActivity>()
            finish()
        })

        viewModel.storesData.observe(this, Observer {
            initData()
        })

        dateListener = MaskedTextChangedListener(
            "[00].[00].[0000]",
            true,
            edBirthDay,
            null,
            null
        )
        edBirthDay.addTextChangedListener(dateListener)
        edBirthDay.onFocusChangeListener = dateListener

        btnEdit.setOnClickListener {
            viewModel.clientInfo(ClientInfoRequest(
                Prefs.getToken(),
                fio = edFullName.text.toString(),
                date = edBirthDay.text.toString().replace(".", ""),
                marketName = edStore.text.toString()
            ))
        }


    }

    override fun loadData() {
        initData()
    }

    override fun initData() {
        val user = Prefs.getClientInfo()

        edPhone.setText(user?.phone)
        edFullName.setText(user?.name)
        edStore.setText(user?.marketName)
        edBirthDay.setText(DateUtils.getDateFromServerTime(user?.date))

    }

    override fun updateData() {

    }

}
