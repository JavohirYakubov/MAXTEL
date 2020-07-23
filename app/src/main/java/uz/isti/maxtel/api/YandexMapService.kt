package uz.isti.maxtel.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import uz.isti.maxtel.model.ISTIBaseResponse
import uz.isti.maxtel.model.SearchByNameResponse
import java.util.*

interface YandexMapService {
    @GET("v1/")
    fun searchByName(@Query("text") text: String,
                     @Query("ll") ll: String = "71.786774,40.366687",
                     @Query("apikey") apikey: String = "59cfb685-9632-40bc-9149-a58f4c6b5408",
                     @Query("format") format: String = "json",
                     @Query("results") results: Int = 30,
                     @Query("type") type: String = "biz",
                     @Query("spn") spn: String = "5,5",
                     @Query("lang") lang: String = "ru_RU"
    )
    : Observable<SearchByNameResponse>
}