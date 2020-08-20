package uz.isti.maxtel.screen.splash

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.bottomsheet_language.view.*
import kotlinx.android.synthetic.main.must_update_layout.view.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import uz.isti.maxtel.App
import uz.isti.maxtel.BuildConfig
import uz.isti.maxtel.R
import uz.isti.maxtel.api.*
import uz.isti.maxtel.base.*
import uz.isti.maxtel.model.ISTIBaseResponse
import uz.isti.maxtel.model.MobileData
import uz.isti.maxtel.screen.auth.SignActivity
import uz.isti.maxtel.screen.auth.selectregion.SelectRegionActivity
import uz.isti.maxtel.screen.main.MainActivity
import uz.isti.maxtel.utils.LocaleManager
import uz.isti.maxtel.utils.Prefs

class SplashActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_splash

    val compositeDisposable = CompositeDisposable()

    val api = ISTClient.retrofit.create(ISTIService::class.java)

    override fun initViews() {
    }

    override fun loadData() {
        getData()
    }

    override fun initData() {

    }

    fun getData(){
        compositeDisposable.add(api.getConfig().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableObserver<ISTIBaseResponse<ISTICheckModel>>(){
                override fun onComplete() {

                }

                override fun onNext(t: ISTIBaseResponse<ISTICheckModel>) {
                    //http://91.196.77.110:60020/MobilTest/odata/standard.odata/
                    if (!t.error){
                        val host = "http://" + t.items.ipaddress + ":" + t.items.ipport + "/" + t.items.href_address + "/"
                        App.imageBaseUrl = "http://" + t.items.ipaddress + ":" + t.items.ipport + "/img/" + t.items.secret_name + "/"
                        Prefs.setServerData(MobileData(
                            host,
                            App.imageBaseUrl,
                            t.items.mobile_username ?: "",
                            t.items.mobile_password ?: ""
                        ))

                        Client.initClient(App.app, host, t.items.mobile_username ?: "", t.items.mobile_password ?: "")
                        if ((t.items.client_version_code?.toIntOrNull() ?: 0) > BuildConfig.VERSION_CODE){
                            showMustUpdate()
                        }else if (Prefs.getToken().isNullOrEmpty()){
                            showLanguageDialog()
                        }else{
                            startClearTopActivity<MainActivity>()
                            finish()
                        }
                    }else{
                        showError(t.message)
                    }
                }

                override fun onError(e: Throwable) {
                    showError(e.localizedMessage)
                    val data = Prefs.getServerData()
                    if (data != null){
                        App.imageBaseUrl = data.imageHost
                        Client.initClient(App.app, data.serverHost, data.username, data.password)

                        if (Prefs.getToken().isNullOrEmpty()){
                            showLanguageDialog()
                        }else{
                            startClearTopActivity<MainActivity>()
                            finish()
                        }
                    }
                }
            }))
    }

    override fun updateData() {

    }

    fun showLanguageDialog(){
        val bottomSheetDialog = BottomSheetDialog(this)
        val viewLang = layoutInflater.inflate(R.layout.bottomsheet_language, null)
        bottomSheetDialog.setContentView(viewLang)
        bottomSheetDialog.setCancelable(false)
        viewLang.tvUzbCr.setOnClickListener {
            Prefs.setLang("uz")
            LocaleManager.setNewLocale(this, "uz")
            bottomSheetDialog?.dismiss()

            startClearTopActivity<MainActivity>()
            finish()
        }
        viewLang.tvRu.setOnClickListener {
            Prefs.setLang("en")
            LocaleManager.setNewLocale(this, "en")
            bottomSheetDialog?.dismiss()

            startClearTopActivity<MainActivity>()
            finish()
        }

        bottomSheetDialog.show()
    }

    fun showMustUpdate(){
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.must_update_layout, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCancelable(false)
        view.cardViewDownload.setOnClickListener {
            startActivityToOpenUrlInBrowser("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
        }

        bottomSheetDialog.show()
    }
}
