package hai.ithust.PhotoPickerDemo;

import android.content.Intent;
import android.net.Uri;
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
    private final ArrayList<Uri> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(selectedPhotos, view -> pickPhoto());

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);

        findViewById(R.id.button_no_camera).setOnClickListener(v -> pickPhoto());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE)) {
            if (data != null) {
                List<Uri> photos = data.getParcelableArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                selectedPhotos.clear();
                if (photos != null) {
                    selectedPhotos.addAll(photos);
                }
                photoAdapter.setPhotoPaths(photos);
            }
        }
    }

    private void pickPhoto() {
        PhotoPicker.builder()
                .setPhotoCount(PhotoAdapter.MAX)
                .setPreviewEnabled(false)
                .setSelected(selectedPhotos)
                .start(MainActivity.this);
    }
}
