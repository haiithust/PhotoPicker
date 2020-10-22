package hai.ithust.photopicker.entity

import android.net.Uri
import android.text.TextUtils
import java.util.ArrayList

/**
 * @author conghai on 12/20/18.
 */
class PhotoDirectory(
        val id: String,
        private val coverUri: Uri,
        val name: String?,
        val photos: MutableList<GalleryPhoto> = mutableListOf(GalleryPhoto(coverUri))
) {

    fun addPhoto(uri: Uri) {
        photos.add(GalleryPhoto(uri))
    }
}