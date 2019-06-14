package hai.ithust.photopicker.fragment;

import android.content.Context;

import java.util.ArrayList;

import hai.ithust.photopicker.utils.MediaStoreHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author conghai on 1/2/19.
 */
class PhotoPickerPresenter {
    private PhotoPickerCallback mCallback;
    private CompositeDisposable mDisposable;

    PhotoPickerPresenter(PhotoPickerCallback callback) {
        mCallback = callback;
    }

    void create() {
        mDisposable = new CompositeDisposable();
    }

    void destroy() {
        mDisposable.clear();
        mCallback = null;
    }

    void getPhotos(Context context) {
        mDisposable.add(Observable.fromCallable(() -> MediaStoreHelper.getPhotoDirs(context.getApplicationContext()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (mCallback != null) {
                        mCallback.onGetListPhotoSuccess(list);
                    }
                }, error -> {
                    if (mCallback != null) {
                        mCallback.onGetListPhotoSuccess(new ArrayList<>());
                    }
                }));
    }
}
