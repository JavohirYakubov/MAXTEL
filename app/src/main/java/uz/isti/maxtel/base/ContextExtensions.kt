package uz.isti.maxtel.base

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import java.io.File
import java.io.Serializable
import android.webkit.CookieSyncManager
import android.webkit.CookieManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty
import uz.isti.maxtel.R
import uz.isti.maxtel.utils.GlideUtils
import uz.isti.maxtel.utils.TextUtils
import java.time.temporal.TemporalAmount
import java.util.ArrayList

/*
    Toast
 */

fun Context.showToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_LONG).show()
}

fun Context.showShortToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(string: Int) {
    Toast.makeText(this, string, Toast.LENGTH_LONG).show()
}

fun Context.showError(message: String){
    Toasty.error(this, message, Toast.LENGTH_LONG, true).show()
}

fun Context.showWarning(message: String){
    Toasty.warning(this, message, Toast.LENGTH_SHORT, true).show()
}

fun Context.showInfo(message: String){
    Toasty.info(this, message, Toast.LENGTH_SHORT, true).show()
}

fun Context.showSuccess(message: String){
    Toasty.success(this, message, Toast.LENGTH_LONG, true).show()
}

/*
    Activity
 */

inline fun <reified T : Activity> Context.startActivity() =
        this.startActivity(newIntent<T>())

inline fun <reified T : Activity> Context.startActivity(key: String, value: String) {
    var intent = newIntent<T>(key, value)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    this.startActivity(intent)
}

inline fun <reified T : Activity> Context.startActivity(key: String, value: Int) =
        this.startActivity(newIntent<T>(key, value))

inline fun <reified T : Activity> Context.startActivity(key: String, value: ArrayList<String>) =
        this.startActivity(newIntent<T>(key, value))

inline fun <reified T : Activity> Context.startActivity(key: String, value: Serializable) =
        this.startActivity(newIntent<T>(key, value))

inline fun <reified T : Activity> Context.startActivity(key: String, value: Serializable, key2: String, value2: String) =
        this.startActivity(newIntent<T>(key, value, key2, value2))

inline fun <reified T : Activity> Context.startActivity(key: String, value: Serializable, key2: String, value2: Serializable) =
        this.startActivity(newIntent<T>(key, value, key2, value2))

inline fun <reified T : Activity> Context.startActivity(key: String, value: String, key2: String, value2: String, key3: String, value3: String) =
        this.startActivity(newIntent<T>(key, value, key2, value2, key3, value3))

inline fun <reified T : Activity> Context.startActivity(key: String, value: String, key2: String, value2: String, key3: String, value3: Serializable?) =
        this.startActivity(newIntent<T>(key, value, key2, value2, key3, value3))

inline fun <reified T : Activity> Context.startActivity(key: String, value: Parcelable) =
        this.startActivity(newIntent<T>(key, value))

inline fun <reified T : Activity> Context.startActivity(intent: Intent) {
    return this.startActivity(newIntent<T>().putExtras(intent))
}

inline fun Context.startActivityToShareText(sharedText: String) {
    val intent = Intent(android.content.Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(android.content.Intent.EXTRA_TEXT, sharedText)
    startActivity(Intent.createChooser(intent, "Share"))
}

inline fun Context.startActivityToOpenUrlInBrowser(url: String?) {
    val browserIntent = newIntentToOpenUrlInBrowser(url)
    if (browserIntent == null) return
    browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
    try {
        startActivity(browserIntent)
    } catch (e: Exception) {
    }
}

inline fun <reified T : Activity> Context.startActivityNewTask() {
    var intent = newIntent<T>()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}

inline fun <reified T : Activity> Context.startActivityNewTask(key: String, value: Serializable) {
    var intent = newIntent<T>(key, value)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}

inline fun <reified T : Activity> Context.startClearActivity() {
    var intent = newIntent<T>()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    this.startActivity(intent)
}

inline fun <reified T : Activity> Context.startClearTopActivity() {
    var intent = newIntent<T>()
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    this.startActivity(intent)
}

inline fun <reified T : Activity> Context.startClearTopActivity(key: String, value: Serializable?) {
    var intent = newIntent<T>()
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    intent.putExtra(key, value)
    this.startActivity(intent)
}

inline fun <reified T : Activity> Context.startClearTopActivity(key1: String, value1: String?, key2: String, value2: String?) {
    var intent = newIntent<T>()
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    intent.putExtra(key1, value1)
    intent.putExtra(key2, value2)
    this.startActivity(intent)
}

inline fun <reified T : Activity> Context.startClearActivity(key: String, value: String) {
    var intent = newIntent<T>()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    intent.putExtra(key, value)
    this.startActivity(intent)
}

inline fun <reified T : Activity> Context.startClearActivity(key: String, value: Int) {
    var intent = newIntent<T>()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    intent.putExtra(key, value)
    this.startActivity(intent)
}


inline fun <reified T : Activity> Context.startActivityForResult(requestCode: Int) {
    val intent = newIntent<T>()
    (this as Activity).startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Context.startActivityForResult(requestCode: Int, key: String, value: String) {
    val intent = newIntent<T>()
    intent.putExtra(key, value)
    (this as Activity).startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Context.startActivityForResult(requestCode: Int, key: String, value: Serializable) {
    val intent = newIntent<T>()
    intent.putExtra(key, value)
    (this as Activity).startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Context.startActivityForResult(requestCode: Int, longitude: Double, latitude: Double) {
    val intent = newIntent<T>()
    intent.putExtra("longitude", longitude)
    intent.putExtra("latitude", latitude)
    (this as Activity).startActivityForResult(intent, requestCode)
}

inline fun Context.startPdfActivity(pdfFilePath: String?) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(pdfFilePath)
        var fileUri = Uri.fromFile(file)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        }
        intent.setDataAndType(fileUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
    } catch (e: Exception) {
        showToast("Cannot open pdf")
    }
}

inline fun Context.startEmailActivity(email: String?, subject: String?, body: String?) {
    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", email, null))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    emailIntent.putExtra(Intent.EXTRA_TEXT, body)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayListOf(email))
    startActivity(Intent.createChooser(emailIntent, "Send email"))
}

/*
    Intent
 */

inline fun <reified T : Activity> Context.newIntent(): Intent =
        Intent(this, T::class.java)

inline fun <reified T : Activity> Context.newIntent(key: String, value: String): Intent =
        Intent(this, T::class.java).putExtra(key, value)

inline fun <reified T : Activity> Context.newIntent(key: String, value: Int): Intent =
        Intent(this, T::class.java).putExtra(key, value)

inline fun <reified T : Activity> Context.newIntent(key: String, value: java.util.ArrayList<String>): Intent =
        Intent(this, T::class.java).putExtra(key, value)

inline fun <reified T : Activity> Context.newIntent(key: String, value: Serializable): Intent =
        Intent(this, T::class.java).putExtra(key, value)


inline fun <reified T : Activity> Context.newIntent(key: String, value: Serializable, key2: String, value2: String): Intent =
        Intent(this, T::class.java).putExtra(key, value).putExtra(key2, value2)

inline fun <reified T : Activity> Context.newIntent(key: String, value: String, key2: String, value2: String): Intent =
        Intent(this, T::class.java).putExtra(key, value).putExtra(key2, value2)

inline fun <reified T : Activity> Context.newIntent(key: String, value: Serializable, key2: String, value2: Serializable): Intent =
        Intent(this, T::class.java).putExtra(key, value).putExtra(key2, value2)

inline fun <reified T : Activity> Context.newIntent(key: String, value: String?, key2: String, value2: String?, key3: String, value3: String?): Intent =
        Intent(this, T::class.java).putExtra(key, value).putExtra(key2, value2).putExtra(key3, value3)

inline fun <reified T : Activity> Context.newIntent(key: String, value: Boolean?, key2: String, value2: String?, key3: String, value3: String?): Intent =
        Intent(this, T::class.java).putExtra(key, value).putExtra(key2, value2).putExtra(key3, value3)

inline fun <reified T : Activity> Context.newIntent(key: String, value: String?, key2: String, value2: String?, key3: String, value3: Serializable?): Intent =
        Intent(this, T::class.java).putExtra(key, value).putExtra(key2, value2).putExtra(key3, value3)

inline fun <reified T : Activity> Context.newIntent(key: String, value: String?, key2: String, value2: String?, key3: String, value3: String?, key4: String, value4: Array<String>): Intent =
        Intent(this, T::class.java).putExtra(key, value).putExtra(key2, value2).putExtra(key3, value3).putExtra(key4, value4)

inline fun <reified T : Activity> Context.newIntent(key: String, value: Parcelable): Intent =
        Intent(this, T::class.java).putExtra(key, value)

inline fun <reified T : Activity> Context.newIntent(action: String): Intent =
        Intent(this, T::class.java).setAction(action)

inline fun <reified T : Activity> Context.newIntent(key: String, value: String, bundle: Bundle) =
        Intent(this, T::class.java).putExtra(key, value).putExtras(bundle)

fun Context.newIntentToOpenUrlInBrowser(url: String?): Intent? {
    if (url == null || url!!.isEmpty()) return null
    var fullUrl = url
    if (!url.startsWith("http://") && !url.startsWith("https://"))
        fullUrl = "http://$url"
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
    return browserIntent
}


// Fragment

fun Context.startFragment(fragmentContainerId: Int, fragment: Fragment) {
    val manager = (this as AppCompatActivity).supportFragmentManager
    if (manager.findFragmentByTag(fragment.javaClass.name) != null) return
    val transaction = manager.beginTransaction()
    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)

    transaction.replace(fragmentContainerId, fragment, fragment.javaClass.name)
    transaction.commitAllowingStateLoss()
}

fun Context.getActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun Context.clearCookies() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    } else {
        val cookieSyncMngr = CookieSyncManager.createInstance(this)
        cookieSyncMngr.startSync()
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        cookieManager.removeSessionCookie()
        cookieSyncMngr.stopSync()
        cookieSyncMngr.sync()
    }
}

fun Double?.formattedAmount(withCurrency: Boolean = true): String{
    return TextUtils.getFormattedAmount(this, withCurrency)
}

fun View.getColor(color: Int): Int{
    return ContextCompat.getColor(this.context, color)
}

fun ImageView.loadImage(url: String?){
    if (url != null){
        GlideUtils.loadImage(this, url)
    }else{
        this.setImageResource(R.drawable.placeholder)
    }
}
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
}

val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()