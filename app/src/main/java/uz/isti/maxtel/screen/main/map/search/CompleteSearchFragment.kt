package uz.isti.maxtel.screen.main.map.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.complete_search_fragment.*
import uz.isti.maxtel.App
import uz.isti.maxtel.R
import uz.isti.maxtel.api.YandexMapClient
import uz.isti.maxtel.api.YandexMapService
import uz.isti.maxtel.model.FeaturesItem
import uz.isti.maxtel.model.SearchByNameResponse
import uz.isti.maxtel.view.adapter.BaseAdapterListener
import uz.isti.maxtel.view.adapter.CompletedSearchAdapter
import kotlin.random.Random


interface CompleteSearchFragmentListener{
    fun onClickItem(lat: Double, long: Double)
}

class CompleteSearchFragment(private val listener: CompleteSearchFragmentListener): BottomSheetDialogFragment(){
    var compositeDisposable = CompositeDisposable()
    lateinit var api: YandexMapService
    var cordianates = ""
    var apis = listOf(
        "59cfb685-9632-40bc-9149-a58f4c6b5408",
        "bb2b1a57-6df7-41df-b54f-36f45a5d2789",
        "8c1609f0-3844-4e4a-8a0f-7b59b4f21765",
        "38ff1041-8cbe-453c-9a34-efe967b8312f"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        YandexMapClient.initClient(App.app)
        api = YandexMapClient.retrofit.create(YandexMapService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.complete_search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edAddress.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edAddress.text.length < 4){
                    return
                }
                searchWord()
            }
        })

        edAddress.post {
            val inputMethodManager = edAddress.context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(edAddress, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun searchWord(){
        compositeDisposable.clear()
        compositeDisposable.add(
            api.searchByName(edAddress.text.toString(), cordianates, apis.get(Random.nextInt(0, apis.count() - 1)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { progressAnimationView.visibility = View.GONE }
                .doOnSubscribe { progressAnimationView.visibility = View.VISIBLE }
                .subscribeWith(object: DisposableObserver<SearchByNameResponse>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: SearchByNameResponse) {
                        recycler.layoutManager = LinearLayoutManager(activity)
                        recycler.adapter = CompletedSearchAdapter(t.features ?: emptyList(), object: BaseAdapterListener{
                            override fun onClickItem(item: Any?) {
                                val item = item as FeaturesItem
                                if (item.geometry?.coordinates?.count() ?: 0 > 0){
                                    listener.onClickItem(
                                        item.geometry?.coordinates?.get(1) ?: 0.0,
                                        item.geometry?.coordinates?.get(0) ?: 0.0
                                    )
                                    dismiss()
                                }
                            }
                        })
                    }

                    override fun onError(e: Throwable) {
//                        activity?.showError(e.localizedMessage)
                    }
                })
        )
    }
}
