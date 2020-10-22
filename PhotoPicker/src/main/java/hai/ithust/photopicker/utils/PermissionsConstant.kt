package hai.ithust.photopicker.utils

import android.Manifest

/**
 * @author conghai on 12/20/18.
 */
object PermissionsConstant {
    const val REQUEST_CAMERA = 1
    const val REQUEST_EXTERNAL_READ = 2
    const val REQUEST_EXTERNAL_WRITE = 3

    @JvmField
    val PERMISSIONS_CAMERA = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    @JvmField
    val PERMISSIONS_EXTERNAL_WRITE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    @JvmField
    val PERMISSIONS_EXTERNAL_READ = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
    )
}