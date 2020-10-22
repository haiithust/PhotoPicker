package hai.ithust.photopicker.utils

import android.content.ContentUris
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import hai.ithust.photopicker.entity.PhotoDirectory
import java.util.ArrayList

/**
 * @author conghai on 12/20/18.
 */
object MediaStoreHelper {
    @JvmStatic
    fun getPhotoDirs(context: Context): List<PhotoDirectory> {
        val directories: MutableList<PhotoDirectory> = mutableListOf()
        val protections = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.SIZE
        )
        val data = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                protections,
                MediaColumns.MIME_TYPE + "=? or " + MediaColumns.MIME_TYPE + "=? or " + MediaColumns.MIME_TYPE + "=? " + "",
                arrayOf("image/jpeg", "image/png", "image/jpg"),
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        )
        if (data != null) {
            while (data.moveToNext()) {
                val size = data.getInt(data.getColumnIndexOrThrow(MediaColumns.SIZE)).toLong()
                if (size < 1) continue

                val imageId = data.getInt(data.getColumnIndexOrThrow(BaseColumns._ID))
                val bucketId = data.getString(data.getColumnIndexOrThrow(MediaColumns.BUCKET_ID))
                val name = data.getString(data.getColumnIndexOrThrow(MediaColumns.BUCKET_DISPLAY_NAME))
                val photoUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId.toLong())
                val index = directories.indexOfFirst { it.id == bucketId }
                if (index in directories.indices) {
                    directories[index].addPhoto(photoUri)
                } else {
                    directories.add(PhotoDirectory(bucketId, photoUri, name))
                }
            }
            data.close()
        }
        return directories
    }
}