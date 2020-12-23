package uz.isti.maxtel.api

import io.reactivex.Observable
import retrofit2.http.GET
import uz.isti.maxtel.model.ISTIBaseResponse
import java.util.*

interface ISTIService {
    @GET("load_config/MaxtelTest")
    fun getConfig(): Observable<ISTIBaseResponse<ISTICheckModel>>
}