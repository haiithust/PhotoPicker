package hai.ithust.photopicker.utils

import android.Manifest.permission
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.Objects

/**
 * @author conghai on 12/20/18.
 */
object PermissionsUtils {
    @JvmStatic
    fun checkReadStoragePermission(activity: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(activity, permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_EXTERNAL_READ,
                    PermissionsConstant.REQUEST_EXTERNAL_READ)
            return false
        }
        return true
    }

    @JvmStatic
    fun checkReadStoragePermission(fragment: Fragment): Boolean {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(PermissionsConstant.PERMISSIONS_EXTERNAL_READ,
                    PermissionsConstant.REQUEST_EXTERNAL_READ)
            return false
        }
        return true
    }

    @JvmStatic
    fun checkWriteStoragePermission(fragment: Fragment): Boolean {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(PermissionsConstant.PERMISSIONS_EXTERNAL_WRITE,
                    PermissionsConstant.REQUEST_EXTERNAL_WRITE)
            return false
        }
        return true
    }

    @JvmStatic
    fun checkCameraPermission(fragment: Fragment): Boolean {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(PermissionsConstant.PERMISSIONS_CAMERA,
                    PermissionsConstant.REQUEST_CAMERA)
            return false
        }
        return true
    }
}