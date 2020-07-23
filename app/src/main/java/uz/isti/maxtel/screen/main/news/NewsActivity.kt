package uz.isti.maxtel.screen.main.news

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import uz.isti.maxtel.R

import kotlinx.android.synthetic.main.activity_news.*
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.view.adapter.NewsAdapter

class NewsActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_news
    lateinit var viewModel: MainViewModel

    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.progress.observe(this, Observer {
            setProgress(it)
        })

        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.newsData.observe(this,  Observer {
            initData()
        })

        imgBack.setOnClickListener {
            finish()
        }
    }

    override fun loadData() {
        viewModel.getNews()
    }

    override fun initData() {
        if (viewModel.newsData.value != null){
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = NewsAdapter(viewModel.newsData.value ?: emptyList())
        }
    }

    override fun updateData() {

    }

}
