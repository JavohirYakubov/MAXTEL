package uz.isti.maxtel.screen.main.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.android.synthetic.main.activity_app_web_view.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.showInfo
import uz.isti.maxtel.utils.Constants

class AppWebViewActivity : BaseActivity() {
    lateinit var url: String
    override fun getLayout(): Int =  R.layout.activity_app_web_view

    override fun initViews() {
        url = intent.getStringExtra(Constants.EXTRA_DATA)

        imgBack.setOnClickListener {
            finish()
        }

        webview.settings.javaScriptEnabled = true

        webview.webChromeClient = object : WebChromeClient() {

        }
        webview.loadUrl(url)


        NetworkUtils.registerNetworkStatusChangedListener(object: NetworkUtils.OnNetworkStatusChangedListener{
            override fun onConnected(networkType: NetworkUtils.NetworkType?) {
                showConnection(notConnection = false)
                webview.loadUrl(url)
            }

            override fun onDisconnected() {
                showConnection(notConnection = true)
            }
        })
    }

    override fun loadData() {

    }

    override fun initData() {

    }

    override fun updateData() {

    }

}
