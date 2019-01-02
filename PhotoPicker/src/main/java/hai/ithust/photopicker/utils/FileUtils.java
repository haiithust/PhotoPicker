package hai.ithust.photopicker.utils;

import java.io.File;

/**
 * @author conghai on 12/20/18.
 */
public class FileUtils {
    public static boolean fileIsExists(String path) {
        if (path == null || path.trim().length() <= 0) {
            return false;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
