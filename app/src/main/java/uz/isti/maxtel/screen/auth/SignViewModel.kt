package uz.isti.maxtel.screen.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.isti.maxtel.model.LoginConfirmResponse
import uz.isti.maxtel.model.PhoneCheckResponse
import uz.isti.maxtel.model.UserSettings
import uz.isti.maxtel.repository.AuthRepository
import uz.isti.maxtel.repository.UserRepository

class SignViewModel: ViewModel(){

    val authRepository = AuthRepository()

    val progress = MutableLiveData<Boolean>()
    val progressRegions = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val loginResponse = MutableLiveData<PhoneCheckResponse>()
    val loginConfirmResponse = MutableLiveData<LoginConfirmResponse?>()

    var phone = ""


    fun login(phone: String){
        this.phone = phone
        authRepository.login(phone, progress, error, loginResponse)
    }

    fun loginConfirm(code: String, marketName: String?, fullName: String?, datebirth: String){
        authRepository.loginConfirm(phone, code, marketName, fullName, datebirth, progress, error, loginConfirmResponse)
    }
}