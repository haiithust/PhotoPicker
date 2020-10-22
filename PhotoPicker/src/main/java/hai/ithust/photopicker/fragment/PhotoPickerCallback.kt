package hai.ithust.photopicker.fragment

import hai.ithust.photopicker.entity.PhotoDirectory

/**
 * @author conghai on 1/2/19.
 */
interface PhotoPickerCallback {
    fun onGetListPhotoSuccess(directories: List<PhotoDirectory>)
}