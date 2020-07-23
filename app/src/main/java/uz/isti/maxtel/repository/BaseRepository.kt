package uz.isti.maxtel.repository

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import uz.isti.maxtel.App
import uz.isti.maxtel.api.Api
import uz.isti.maxtel.api.Client

open class BaseRepository {

    val api = Client.getInstance(App.app).create(Api::class.java)

    val compositeDisposable = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable): Disposable {
        compositeDisposable.add(disposable)
        return disposable
    }
}