package hai.ithust.photopicker.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import hai.ithust.photopicker.R;
import hai.ithust.photopicker.entity.GalleryPhoto;
import hai.ithust.photopicker.entity.PhotoDirectory;
import hai.ithust.photopicker.event.OnPhotoListener;

/**
 * @author conghai on 12/20/18.
 */
public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private static final int ALL_PHOTO = 0;
    private static final int SCROLL_THRESHOLD = 30;

    private List<PhotoDirectory> mPhotoDirectories = new ArrayList<>();
    private List<GalleryPhoto> mPhotos = new ArrayList<>();
    private ArrayList<String> mSelectedPhotos = new ArrayList<>();
    private int mDirectoryCategoryIndex = ALL_PHOTO;
    private int mMaxItem;

    private RecyclerView mRecyclerView;
    private RequestManager mGlide;
    private OnPhotoListener mOnItemCheckListener;
    private RecyclerView.OnScrollListener mScrollListener;

    public PhotoGridAdapter(ArrayList<String> originalPhotos, int maxItem, OnPhotoListener listener) {
        mMaxItem = maxItem;
        if (originalPhotos != null) mSelectedPhotos.addAll(originalPhotos);
        mOnItemCheckListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mGlide = Glide.with(mRecyclerView.getContext());

        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) < SCROLL_THRESHOLD) {
                    resumeLoadImage();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeLoadImage();
                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    pauseLoadImage();
                }
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView.removeOnScrollListener(mScrollListener);
        mGlide.onDestroy();
    }

    @Override
    @NonNull
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_item_photo, parent, false);
        return new PhotoViewHolder(itemView, mOnItemCheckListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.bindData(mPhotos.get(position), mGlide, mSelectedPhotos.indexOf(mPhotos.get(position).getPath()));
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    // return true if update success, false if not. It means max image can selected
    public boolean updatePhoto(int position) {
        if (position >= 0 && position < mPhotos.size()) {
            GalleryPhoto photo = mPhotos.get(position);
            int selectedNumber = mSelectedPhotos.indexOf(photo.getPath());
            if (selectedNumber > -1) {
                // user unselected photo
                toggleSelection(photo);
                notifyItemChanged(position);

                // if user unselected last number, we no need calculate index again
                if (selectedNumber < mSelectedPhotos.size()) {
                    for (int i = 0; i < mPhotos.size(); i++) {
                        if (isSelected(mPhotos.get(i))) {
                            notifyItemChanged(i);
                        }
                    }
                }
                return true;
            } else {
                // user selected photo
                if (mSelectedPhotos.size() < mMaxItem) {
                    toggleSelection(photo);
                    notifyItemChanged(position);
                    return true;
                }
            }
        }
        return false;
    }

    public void setPhotoDirectories(List<PhotoDirectory> photoDirectories) {
        mPhotoDirectories.clear();
        if (!photoDirectories.isEmpty()) {
            mPhotoDirectories.addAll(photoDirectories);
        }

        if (mDirectoryCategoryIndex == ALL_PHOTO) {
            for (PhotoDirectory directory : mPhotoDirectories) {
                if (directory != null) {
                    mPhotos.addAll(directory.getPhotos());
                }
            }
        } else if (mDirectoryCategoryIndex >= 0 && mDirectoryCategoryIndex < mPhotoDirectories.size()) {
            mPhotos.addAll(mPhotoDirectories.get(mDirectoryCategoryIndex).getPhotos());
        }

        notifyDataSetChanged();
    }

    public void addNewPhoto(GalleryPhoto photo) {
        if (photo != null) {
            mPhotos.add(0, photo);
            if (mPhotoDirectories.size() > 0) {
                mPhotoDirectories.get(0).addPhoto(photo);
            }
            notifyItemInserted(0);
            mRecyclerView.scrollToPosition(0);
        }
    }

    public List<String> getPhotoDirectories() {
        ArrayList<String> directories = new ArrayList<>();
        for (PhotoDirectory directory : mPhotoDirectories) {
            directories.add(directory.getName());
        }
        return directories;
    }

    public int getDirectoryCategoryIndex() {
        return mDirectoryCategoryIndex;
    }

    public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
        if (currentDirectoryIndex != mDirectoryCategoryIndex) {
            mPhotos.clear();
            if (currentDirectoryIndex == ALL_PHOTO) {
                mDirectoryCategoryIndex = ALL_PHOTO;
                for (PhotoDirectory directory : mPhotoDirectories) {
                    if (directory != null) {
                        mPhotos.addAll(directory.getPhotos());
                    }
                }
            } else if (currentDirectoryIndex > 0 && currentDirectoryIndex <= mPhotoDirectories.size()) {
                mDirectoryCategoryIndex = currentDirectoryIndex;
                mPhotos.addAll(mPhotoDirectories.get(currentDirectoryIndex - 1).getPhotos());
            }
            notifyDataSetChanged();
        }
    }


    public ArrayList<String> getSelectedPhotos() {
        return mSelectedPhotos;
    }

    public boolean isSelectPhoto() {
        return !mSelectedPhotos.isEmpty();
    }

    public int getMaxItem() {
        return mMaxItem;
    }

    private void resumeLoadImage() {
        if (mGlide.isPaused()) {
            mGlide.resumeRequests();
        }
    }

    private void pauseLoadImage() {
        if (!mGlide.isPaused()) {
            mGlide.pauseRequests();
        }
    }

    private boolean isSelected(GalleryPhoto photo) {
        return mSelectedPhotos.contains(photo.getPath());
    }

    private void toggleSelection(GalleryPhoto photo) {
        if (mSelectedPhotos.contains(photo.getPath())) {
            mSelectedPhotos.remove(photo.getPath());
        } else {
            mSelectedPhotos.add(photo.getPath());
        }
    }
}
