package hai.ithust.photopicker.fragment;

import java.util.List;

import hai.ithust.photopicker.entity.PhotoDirectory;

/**
 * @author conghai on 1/2/19.
 */
public interface PhotoPickerCallback {
    void onGetListPhotoSuccess(List<PhotoDirectory> directories);
}
