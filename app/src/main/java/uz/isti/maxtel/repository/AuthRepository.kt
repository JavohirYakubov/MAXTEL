package uz.isti.maxtel.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import uz.isti.maxtel.api.BaseResponse
import uz.isti.maxtel.api.CallbackWrapper
import uz.isti.maxtel.model.LoginConfirmResponse
import uz.isti.maxtel.model.PhoneCheckResponse
import uz.isti.maxtel.model.UserSettings
import uz.isti.maxtel.model.request.CodeConfirmRequest
import uz.isti.maxtel.model.request.LoginRequest
import uz.isti.maxtel.utils.Prefs

class AuthRepository : BaseRepository(){
    fun login(phone: String, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, success: MutableLiveData<PhoneCheckResponse>){
        compositeDisposable.clear()
        compositeDisposable.add(api.login(phone)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<PhoneCheckResponse?>>(error){
                override fun onSuccess(t: BaseResponse<PhoneCheckResponse?>) {
                    if (!t.error){
                        success.value = t.data
                    }else{
                        error.value = t.message
                    }
                }
            })
        )
    }

    fun loginConfirm(phone: String, code: String, marketName: String?, fullname: String?, datebirth: String, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<LoginConfirmResponse?>){
        compositeDisposable.clear()
        val request = CodeConfirmRequest(phone, code, marketName, fullname, date = datebirth)
        compositeDisposable.add(api.loginConfirm(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<LoginConfirmResponse?>>(error){
                override fun onSuccess(t: BaseResponse<LoginConfirmResponse?>) {
                    if (!t.error){
                        data.value =  t.data
                    }else{
                        error.value = t.message
                    }
                }
            })
        )
    }
}