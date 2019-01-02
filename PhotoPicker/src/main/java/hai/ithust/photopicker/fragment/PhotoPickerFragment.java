package hai.ithust.photopicker.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hai.ithust.photopicker.PhotoPicker;
import hai.ithust.photopicker.R;
import hai.ithust.photopicker.adapter.PhotoGridAdapter;
import hai.ithust.photopicker.adapter.PopupDirectoryListAdapter;
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
    private static final int SCROLL_THRESHOLD = 30;
    private static final int COUNT_MAX = 4;

    private PhotoPickerPresenter mPresenter;

    private ImageCaptureManager mCaptureManager;
    private PhotoGridAdapter mPhotoAdapter;
    private PopupDirectoryListAdapter mListAdapter;

    private ListPopupWindow mListPopupWindow;
    private RequestManager mGlideRequestManager;
    private TextView mTvTile;

    public static PhotoPickerFragment newInstance(Bundle bundle) {
        PhotoPickerFragment fragment = new PhotoPickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlideRequestManager = Glide.with(this);

        if (getArguments() != null) {
            ArrayList<String> originalPhotos = getArguments().getStringArrayList(PhotoPicker.EXTRA_ORIGINAL_PHOTOS);
            int maxCount = getArguments().getInt(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
            int column = getArguments().getInt(PhotoPicker.EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER);

            // calculate size of item in recycler view
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int imageSize = (displayMetrics.widthPixels - getResources().getDimensionPixelOffset(R.dimen.picker_padding_small)) / column;

            mPhotoAdapter = new PhotoGridAdapter(mGlideRequestManager, originalPhotos, imageSize, maxCount, this);
            mListAdapter = new PopupDirectoryListAdapter(mGlideRequestManager);

            mPresenter = new PhotoPickerPresenter(this);
            mPresenter.create();
            mPresenter.getPhotos(getContext());

            mCaptureManager = new ImageCaptureManager(getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mPresenter = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.picker_fragment_photo_picker, container, false);
        int column = getArguments() != null ? getArguments().getInt(PhotoPicker.EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER) : DEFAULT_COLUMN_NUMBER;

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(column, OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mPhotoAdapter);

        mTvTile = rootView.findViewById(R.id.tv_directory_category);

        mListPopupWindow = new ListPopupWindow(getActivity());
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setAnchorView(rootView.findViewById(R.id.action_bar));
        mListPopupWindow.setAdapter(mListAdapter);
        mListPopupWindow.setModal(true);

        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListPopupWindow.dismiss();

                PhotoDirectory directory = mListAdapter.getDirectoryByPos(position);

                if (directory != null) {
                    mTvTile.setText(directory.getName());
                }

                mPhotoAdapter.setCurrentDirectoryIndex(position);
            }
        });

        mTvTile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListPopupWindow.isShowing()) {
                    mListPopupWindow.dismiss();
                } else if (!getActivity().isFinishing()) {
                    mListAdapter.setDirectories(mPhotoAdapter.getPhotoDirectories());
                    adjustHeight();
                    mListPopupWindow.show();
                }
            }
        });

        rootView.findViewById(R.id.button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS, mPhotoAdapter.getSelectedPhotos());
                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                }
            }
        });

        rootView.findViewById(R.id.iv_navigation).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    if (!mGlideRequestManager.isPaused()) {
                        mGlideRequestManager.pauseRequests();
                    }
                } else {
                    resumeRequestsIfNotDestroyed();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeRequestsIfNotDestroyed();
                }
            }
        });

        return rootView;
    }

    private void openCamera() {
        try {
            Intent intent = mCaptureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException ignore) {
        }
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

    public void adjustHeight() {
        if (mListAdapter == null) return;
        int count = mListAdapter.getCount();
        count = count < COUNT_MAX ? count : COUNT_MAX;
        if (mListPopupWindow != null) {
            mListPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.__picker_item_directory_height));
        }
    }

    private void resumeRequestsIfNotDestroyed() {
        if (mGlideRequestManager.isPaused()) {
            mGlideRequestManager.resumeRequests();
        }
    }

    @Override
    public void onItemCheck(View view, int position) {
        boolean isUpdated = mPhotoAdapter.updatePhoto(position);
        if (!isUpdated) {
            Toast.makeText(getContext(), getString(R.string.__picker_over_max_count_tips, mPhotoAdapter.getMaxItem()), Toast.LENGTH_SHORT).show();
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
}
