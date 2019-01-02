package hai.ithust.photopicker.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @author conghai on 12/20/18.
 */
public class PermissionsUtils {

    public static boolean checkReadStoragePermission(Activity activity) {
        if (!(ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_EXTERNAL_READ,
                    PermissionsConstant.REQUEST_EXTERNAL_READ);
            return false;
        }
        return true;
    }

    public static boolean checkWriteStoragePermission(Fragment fragment) {
        if (!(ContextCompat.checkSelfPermission(fragment.getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            fragment.requestPermissions(PermissionsConstant.PERMISSIONS_EXTERNAL_WRITE,
                    PermissionsConstant.REQUEST_EXTERNAL_WRITE);
            return false;
        }
        return true;
    }

    public static boolean checkCameraPermission(Fragment fragment) {
        if (!(ContextCompat.checkSelfPermission(fragment.getContext(), CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            fragment.requestPermissions(PermissionsConstant.PERMISSIONS_CAMERA,
                    PermissionsConstant.REQUEST_CAMERA);
            return false;
        }
        return true;
    }
}
