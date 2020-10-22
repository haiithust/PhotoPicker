package hai.ithust.photopicker.fragment

import android.content.Context
import hai.ithust.photopicker.entity.PhotoDirectory
import hai.ithust.photopicker.utils.MediaStoreHelper.getPhotoDirs
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

/**
 * @author conghai on 1/2/19.
 */
internal class PhotoPickerPresenter(private val callback: PhotoPickerCallback) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    fun destroy() {
        disposable.clear()
    }

    fun getPhotos(context: Context) {
        disposable.add(Observable.fromCallable { getPhotoDirs(context.applicationContext) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list: List<PhotoDirectory> ->
                    callback.onGetListPhotoSuccess(list)
                }) {
                    callback.onGetListPhotoSuccess(emptyList())
                })
    }
}