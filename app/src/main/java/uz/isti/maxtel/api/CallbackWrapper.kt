package uz.isti.maxtel.api

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.stream.MalformedJsonException
import io.reactivex.exceptions.CompositeException
import io.reactivex.observers.DisposableObserver
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Error
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

abstract class CallbackWrapper<T>(private val error: MutableLiveData<String>): DisposableObserver<T>(){
    override fun onComplete() {

    }

    protected abstract fun onSuccess(t: T)

    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        when (e) {
            is HttpException -> {
                when (e.code()) {
                    503 -> error.value = "ServerMaintainanceError"
                    401 -> error.value = "UserNotFound"
                    else -> {
                        val responseBody = e.response()?.errorBody()
                        error.value = responseBody?.string()
                    }
                }

            }
            is MalformedJsonException -> error.value = "MalformedJsonException"
            is SocketTimeoutException -> error.value = "timeout"
            is IOException -> error.value = "network error"
            is UnknownHostException -> error.value = "unknown error"
            is SSLHandshakeException -> error.value = "Незащищенное соединение"
            is KotlinNullPointerException -> error.value = "KotlinNullPointerException"
            is CompositeException -> {
                if (e.exceptions.size > 0) {
                    var innerException = e.exceptions[0]
                    onError(innerException)
                }
            }
            else ->{
                error.value = "unknown error"
            }
        }
    }

}