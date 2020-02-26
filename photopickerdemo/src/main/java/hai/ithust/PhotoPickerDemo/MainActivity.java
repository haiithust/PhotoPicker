package hai.ithust.PhotoPickerDemo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hai.ithust.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity {
    private PhotoAdapter photoAdapter;
    private ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(selectedPhotos, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(PhotoAdapter.MAX)
                        .setPreviewEnabled(false)
                        .setSelected(selectedPhotos)
                        .start(MainActivity.this);
            }
        });

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);

        findViewById(R.id.button_no_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(7)
                        .setPreviewEnabled(false)
                        .start(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE)) {
            if (data != null) {
                List<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                photoAdapter.setPhotoPaths(photos);
            }
        }
    }

}
