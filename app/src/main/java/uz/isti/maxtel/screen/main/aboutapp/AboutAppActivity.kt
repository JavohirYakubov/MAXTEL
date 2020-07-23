package uz.isti.maxtel.screen.main.aboutapp
import kotlinx.android.synthetic.main.activity_about_app.*
import uz.isti.maxtel.BuildConfig
import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.startActivityToOpenUrlInBrowser

class AboutAppActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_about_app

    override fun initViews() {
        tvVersion.text = "version: ${BuildConfig.VERSION_NAME}"

        imgBack.setOnClickListener { finish() }

        lyDeveloper.setOnClickListener {
            startActivityToOpenUrlInBrowser("http://isti.uz")
        }
    }

    override fun loadData() {

    }

    override fun initData() {

    }

    override fun updateData() {

    }

}
