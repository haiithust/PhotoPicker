package hai.ithust.photopicker.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import hai.ithust.photopicker.PhotoPicker;
import hai.ithust.photopicker.R;
import hai.ithust.photopicker.adapter.PhotoGridAdapter;
import hai.ithust.photopicker.entity.GalleryPhoto;
import hai.ithust.photopicker.entity.PhotoDirectory;
import hai.ithust.photopicker.event.OnPhotoListener;
import hai.ithust.photopicker.utils.ImageCaptureManager;
import hai.ithust.photopicker.utils.PermissionsConstant;
import hai.ithust.photopicker.utils.PermissionsUtils;

import static android.app.Activity.RESULT_OK;
import static hai.ithust.photopicker.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static hai.ithust.photopicker.PhotoPicker.DEFAULT_MAX_COUNT;
import static hai.ithust.photopicker.PhotoPicker.EXTRA_MAX_COUNT;

/**
 * @author conghai on 12/20/18.
 */
public class PhotoPickerFragment extends Fragment implements OnPhotoListener, PhotoPickerCallback {
    private PhotoPickerPresenter mPresenter;

    private ImageCaptureManager mCaptureManager;
    private PhotoGridAdapter mPhotoAdapter;

    private AlertDialog mSelectDirectoryDialog;
    private TextView mTvTile;
    private ImageView mIvRightAction;

    public static PhotoPickerFragment newInstance(Bundle bundle) {
        PhotoPickerFragment fragment = new PhotoPickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<String> originalPhotos = getArguments().getStringArrayList(PhotoPicker.EXTRA_ORIGINAL_PHOTOS);
            int maxCount = getArguments().getInt(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);

            mPhotoAdapter = new PhotoGridAdapter(originalPhotos, maxCount, this);
            mCaptureManager = new ImageCaptureManager(getActivity());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new PhotoPickerPresenter(this);
        mPresenter.create();
        mPresenter.getPhotos(getContext());
    }

    @Override
    public void onDestroyView() {
        if (mSelectDirectoryDialog != null) {
            mSelectDirectoryDialog.dismiss();
        }
        mPresenter.destroy();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.picker_fragment_photo_picker, container, false);
        int column = getArguments() != null ? getArguments().getInt(PhotoPicker.EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER) : DEFAULT_COLUMN_NUMBER;

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(column, OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mPhotoAdapter);

        mTvTile = rootView.findViewById(R.id.tv_directory_category);
        mIvRightAction = rootView.findViewById(R.id.iv_right_action);

        mTvTile.setOnClickListener(v -> {
            if (isResumed()) {
                mSelectDirectoryDialog = getDirectoryDialog(mPhotoAdapter.getDirectoryCategoryIndex());
                mSelectDirectoryDialog.show();
            }
        });

        mIvRightAction.setOnClickListener(view -> {
            if (mPhotoAdapter != null && mPhotoAdapter.isSelectPhoto()) {
                setResult();
            } else {
                openCamera();
            }
        });

        rootView.findViewById(R.id.iv_navigation).setOnClickListener(view -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (mCaptureManager == null) {
                mCaptureManager = new ImageCaptureManager(getContext());
            }

            mCaptureManager.galleryAddPic();
            String path = mCaptureManager.getCurrentPhotoPath();
            if (mPhotoAdapter != null && !TextUtils.isEmpty(path)) {
                mPhotoAdapter.addNewPhoto(new GalleryPhoto(path.hashCode(), path));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PermissionsConstant.REQUEST_CAMERA:
                case PermissionsConstant.REQUEST_EXTERNAL_WRITE:
                    if (PermissionsUtils.checkWriteStoragePermission(this) &&
                            PermissionsUtils.checkCameraPermission(this)) {
                        openCamera();
                    }
                    break;
            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mCaptureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        mCaptureManager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onItemCheck(View view, int position) {
        if (mPhotoAdapter != null) {
            boolean isUpdated = mPhotoAdapter.updatePhoto(position);

            if (mPhotoAdapter.isSelectPhoto()) {
                mIvRightAction.setImageResource(R.drawable.picker_ic_done);
            } else {
                mIvRightAction.setImageResource(R.drawable.picker_ic_camera);
            }
            if (!isUpdated) {
                Toast.makeText(getContext(), getString(R.string.__picker_over_max_count_tips, mPhotoAdapter.getMaxItem()), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onOpenCamera() {
        if (PermissionsUtils.checkWriteStoragePermission(this) &&
                PermissionsUtils.checkCameraPermission(this)) {
            openCamera();
        }
    }

    @Override
    public void onGetListPhotoSuccess(List<PhotoDirectory> directories) {
        mPhotoAdapter.setPhotoDirectories(directories);
    }

    private void openCamera() {
        try {
            Intent intent = mCaptureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (Exception ignore) {
        }
    }

    private void setResult() {
        if (getActivity() != null) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS, mPhotoAdapter.getSelectedPhotos());
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private AlertDialog getDirectoryDialog(int position) {
        List<String> directories = mPhotoAdapter.getPhotoDirectories();
        directories.add(0, getString(R.string.pp_gallery));
        CharSequence[] items = directories.toArray(new CharSequence[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.pp_select_directory_title)
                .setSingleChoiceItems(items, position, (dialog, which) -> {
                    mTvTile.setText(items[which]);
                    mPhotoAdapter.setCurrentDirectoryIndex(which);

                    dialog.dismiss();
                });
        return builder.create();
    }
}
