package uz.isti.maxtel.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import uz.isti.maxtel.R

class GlideUtils {
    companion object{
        fun loadImage(imageView: ImageView, url: String?){
            if (url == null){
                imageView.setImageDrawable(null)
                return
            }
            Glide.with(imageView).load(url)
                .placeholder(R.drawable.placeholder)
                .into(imageView)
        }
        fun loadCircleImage(imageView: CircleImageView, url: String?){
            if (url == null){
                imageView.setImageDrawable(null)
                return
            }
            Glide.with(imageView).load(url).into(imageView)
        }
    }
}