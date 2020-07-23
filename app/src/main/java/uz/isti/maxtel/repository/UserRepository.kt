package uz.isti.maxtel.repository

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import uz.isti.maxtel.api.BaseResponse
import uz.isti.maxtel.api.CallbackWrapper
import uz.isti.maxtel.model.*
import uz.isti.maxtel.model.request.LoginRequest
import uz.isti.maxtel.model.request.RatingRequest
import uz.isti.maxtel.utils.Constants
import uz.isti.maxtel.utils.Prefs

class UserRepository: BaseRepository() {

    fun getSections(progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<SectionModel>>){
        compositeDisposable.add(api.getSections()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<SectionModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<SectionModel>?>) {
                    if (!t.error && t.data != null){
                        data.value = t.data
                    }else{
                        error.value = t.message
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun getProducts(id: String, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<ProductModel>>){
        compositeDisposable.add(api.getProducts(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<ProductModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<ProductModel>?>) {
                    if (!t.error && t.data != null){
                        val cartProducts = Prefs.getCartList()
                        t.data.forEach {
                            cartProducts.forEach { cart ->
                                if (it.id == cart.id){
                                    it.cartCount = cart.count
                                }
                            }
                        }

                        data.value = t.data
                    }else{
                        error.value = t.message
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun getBrands(sectionId: String, categoryId: String, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<BrandModel>>){
        compositeDisposable.add(api.getBrand(sectionId, categoryId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<BrandModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<BrandModel>?>) {
                    if (!t.error && t.data != null){
                        data.value = t.data
                    }else{
                        error.value = t.message
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun getFavouriteProducts(progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<ProductModel>>){
        val request = GetTovarByFavourite(
            Prefs.getStore()?.id?.toIntOrNull() ?: 0,
            Prefs.getFavourites().map { IdValModel(it) }
        )
        compositeDisposable.add(api.getTovarByIds(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<ProductModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<ProductModel>?>) {
                    if (!t.error && t.data != null){
                        data.value = t.data
                    }else{
                        error.value = t.message
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun getCartProducts(progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<ProductModel>?>){
        val request = GetTovarByFavourite(
            Prefs.getStore()?.id?.toIntOrNull() ?: 0,
            Prefs.getCartList().map { IdValModel(it.id) }
        )
        compositeDisposable.add(api.getTovarByIds(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<ProductModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<ProductModel>?>) {
                    if (!t.error && t.data != null){
                        val cartProducts = Prefs.getCartList()
                        val items: MutableList<ProductModel> = emptyList<ProductModel>().toMutableList()
                        t.data.forEach { product ->
                            cartProducts.forEach {
                                if (product.id == it.id){
                                    product.cartCount = it.count
                                    items.add(product)
                                }
                            }
                        }

                        data.value = items.toList()

                        var totalAmount = 0.0
                        items?.forEach {
                            val item = it as ProductModel
                            if (item?.unity == "Блок"){
                                totalAmount += item.cartCount * item.blokprice!!
                            }else{
                                totalAmount += item.cartCount * item.price!!
                            }

                        }

                        Prefs.setCartModel(CartEventModel(Constants.EVENT_UPDATE_CART, items.count(), totalAmount))
                        EventBus.getDefault().post(Prefs.getCartModel())
                    }else{
                        error.value = t.message
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun getProductDetail(id: String, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<ProductModel>){
        compositeDisposable.add(api.getProductById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<ProductModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<ProductModel>?>) {
                    if (!t.error && t.data != null){
                        data.value = t.data[0]
                    }else{
                        error.value = t.message
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun getStores(progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<StoreSimpleModel>>){
        compositeDisposable.add(api.getRegions()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<StoreSimpleModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<StoreSimpleModel>?>) {
                    if (!t.error){
                        data.value = t.data
                    }else{
                        error.value = t.message ?: ""
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun getStoreInfo(progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<StoreModel>){
        compositeDisposable.add(api.getStoreInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = false }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<StoreModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<StoreModel>?>) {
                    if (!t.error){
                        data.value = t.data!![0]
                    }else{
                        error.value = t.message ?: ""
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun createOrder(request: MakeOrderModel, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<PostBronModel>){
        compositeDisposable.add(api.createOrder(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<PostBronModel?>>(error){
                override fun onSuccess(t: BaseResponse<PostBronModel?>) {
                    if (!t.error){
                        data.value = t.data
                    }else{
                        error.value = t.message ?: ""
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun clientInfo(request: ClientInfoRequest, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<ClientInfoModel>){
        compositeDisposable.add(api.getInfo(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = false }
            .subscribeWith(object: CallbackWrapper<BaseResponse<ClientInfoModel?>>(error){
                override fun onSuccess(t: BaseResponse<ClientInfoModel?>) {
                    if (!t.error){
                        data.value = t.data
                    }else{
                        error.value = t.message ?: ""
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }


    fun getOrders(progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<OrderModel>>){
        compositeDisposable.add(api.getOrders()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<OrderModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<OrderModel>?>) {
                    if (!t.error){
                        data.value = t.data
                    }else{
                        error.value = t.message ?: ""
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun postRating(request: RatingRequest, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, success: MutableLiveData<Boolean>){
        compositeDisposable.add(api.postRating(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<Any?>>(error){
                override fun onSuccess(t: BaseResponse<Any?>) {
                    if (!t.error){
                        success.value = true
                    }else{
                        error.value = t.message ?: ""
                        if (t.error_code == 405){
                            EventBus.getDefault().post(EventModel(Constants.EVENT_LOGOUT, 0))
                        }
                    }
                }
            })
        )
    }

    fun getAds(progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, success: MutableLiveData<AdModel>){
        compositeDisposable.add(api.getAds()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<AdModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<AdModel>?>) {
                    if (!t.error && (t.data?.count() ?: 0) > 0){
                        success.value = t.data?.get(0)
                    }else{
                        error.value = t.message ?: ""
                    }
                }
            })
        )
    }

    fun getNews(progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<NewsModel>>){
        compositeDisposable.add(api.getNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<NewsModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<NewsModel>?>) {
                    if (!t.error){
                        data.value = t.data
                    }else{
                        error.value = t.message ?: ""
                    }
                }
            })
        )
    }

    fun searchProductsByName(keyword: String, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<List<ProductModel>>){
        compositeDisposable.add(api.getProductsByName(keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progress.value = false }
            .doOnSubscribe { progress.value = true }
            .subscribeWith(object: CallbackWrapper<BaseResponse<List<ProductModel>?>>(error){
                override fun onSuccess(t: BaseResponse<List<ProductModel>?>) {
                    if (!t.error && t.data != null){
                        val cartProducts = Prefs.getCartList()
                        t.data.forEach {
                            cartProducts.forEach { cart ->
                                if (it.id == cart.id){
                                    it.cartCount = cart.count
                                }
                            }
                        }

                        data.value = t.data
                    }else{
                        error.value = t.message
                    }
                }
            })
        )
    }

    fun getDeliveryLocation(id: String, progress: MutableLiveData<Boolean>, error: MutableLiveData<String>, data: MutableLiveData<DeliveryLocation>){
        compositeDisposable.add(api.getDeliveryLocation(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { }
            .doOnSubscribe {  }
            .subscribeWith(object: CallbackWrapper<BaseResponse<DeliveryLocation?>>(error){
                override fun onSuccess(t: BaseResponse<DeliveryLocation?>) {
                    if (!t.error && t.data != null){
                        data.value = t.data
                    }else{
                        error.value = t.message
                    }
                }
            })
        )
    }

}