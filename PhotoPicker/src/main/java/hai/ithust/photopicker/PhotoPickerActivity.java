package hai.ithust.photopicker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import hai.ithust.photopicker.fragment.PhotoPickerFragment;

/**
 * @author conghai on 12/20/18.
 */
public class PhotoPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            int themeId = getIntent().getIntExtra(PhotoPicker.EXTRA_THEME, -1);
            if (themeId > 0) {
                setTheme(themeId);
            }
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, PhotoPickerFragment.newInstance(getIntent().getExtras()), PhotoPickerFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }
}
