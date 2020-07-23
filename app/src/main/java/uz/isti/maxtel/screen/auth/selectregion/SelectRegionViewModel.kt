package uz.isti.maxtel.screen.auth.selectregion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.isti.maxtel.model.StoreSimpleModel
import uz.isti.maxtel.repository.UserRepository

class SelectRegionViewModel : ViewModel(){
    val repository = UserRepository()

    val progress = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val regionsData = MutableLiveData<List<StoreSimpleModel>>()

    fun getRegions(){
        repository.getStores(progress, error, regionsData)
    }

}