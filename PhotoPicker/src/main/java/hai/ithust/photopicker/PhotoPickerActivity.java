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

        setContentView(R.layout.picker_activity_photo_picker);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, PhotoPickerFragment.newInstance(getIntent().getExtras()), PhotoPickerFragment.class.getSimpleName())
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
