package hai.ithust.photopicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import hai.ithust.photopicker.fragment.PhotoPickerFragment;

/**
 * @author conghai on 12/20/18.
 */
public class PhotoPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, PhotoPickerFragment.newInstance(getIntent().getExtras()), PhotoPickerFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }
}
