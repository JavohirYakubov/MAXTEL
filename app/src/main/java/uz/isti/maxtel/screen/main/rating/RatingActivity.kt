package uz.isti.maxtel.screen.main.rating

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hsalf.smileyrating.SmileyRating
import kotlinx.android.synthetic.main.activity_rating.*
import uz.isti.maxtel.R
import uz.isti.maxtel.base.BaseActivity
import uz.isti.maxtel.base.showError
import uz.isti.maxtel.base.showSuccess
import uz.isti.maxtel.model.request.RatingRequest
import uz.isti.maxtel.screen.main.MainViewModel
import uz.isti.maxtel.utils.Prefs

class RatingActivity : BaseActivity() {
    override fun getLayout(): Int = R.layout.activity_rating
    lateinit var viewModel: MainViewModel

    override fun initViews() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.progress.observe(this, Observer {
            setProgress(it)
        })
        viewModel.error.observe(this, Observer {
            showError(it)
        })

        viewModel.successRatingData.observe(this, Observer {
            showSuccess(getString(R.string.rating_updated))
            finish()
        })

        imgBack.setOnClickListener {
            finish()
        }
        cardViewSent.setOnClickListener {
            viewModel.postRating(RatingRequest(
                Prefs.getToken(),
                Prefs.getStore()?.id?.toInt() ?: 0,
                ratingServices.selectedSmiley.rating,
                ratingProducts.selectedSmiley.rating,
                ratingCourier.selectedSmiley.rating,
                edComment.text.toString()
            ))
        }
    }

    override fun loadData() {

    }

    override fun initData() {

        //
        ratingServices.setTitle(SmileyRating.Type.TERRIBLE, getString(R.string.smile_rating_terrible1))
        ratingServices.setTitle(SmileyRating.Type.BAD, getString(R.string.smile_rating_bad1))
        ratingServices.setTitle(SmileyRating.Type.OKAY, getString(R.string.smile_rating_okay1))
        ratingServices.setTitle(SmileyRating.Type.GOOD, getString(R.string.smile_rating_good1))
        ratingServices.setTitle(SmileyRating.Type.GREAT, getString(R.string.smile_rating_great1))

        //
        ratingProducts.setTitle(SmileyRating.Type.TERRIBLE, getString(R.string.smile_rating_terrible1))
        ratingProducts.setTitle(SmileyRating.Type.BAD, getString(R.string.smile_rating_bad1))
        ratingProducts.setTitle(SmileyRating.Type.OKAY, getString(R.string.smile_rating_okay1))
        ratingProducts.setTitle(SmileyRating.Type.GOOD, getString(R.string.smile_rating_good1))
        ratingProducts.setTitle(SmileyRating.Type.GREAT, getString(R.string.smile_rating_great1))

        //
        ratingCourier.setTitle(SmileyRating.Type.TERRIBLE, getString(R.string.smile_rating_terrible1))
        ratingCourier.setTitle(SmileyRating.Type.BAD, getString(R.string.smile_rating_bad1))
        ratingCourier.setTitle(SmileyRating.Type.OKAY, getString(R.string.smile_rating_okay1))
        ratingCourier.setTitle(SmileyRating.Type.GOOD, getString(R.string.smile_rating_good1))
        ratingCourier.setTitle(SmileyRating.Type.GREAT, getString(R.string.smile_rating_great1))

    }

    override fun updateData() {

    }

}
