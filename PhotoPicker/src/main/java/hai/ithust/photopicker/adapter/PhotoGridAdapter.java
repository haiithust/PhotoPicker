package hai.ithust.photopicker.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import hai.ithust.photopicker.R;
import hai.ithust.photopicker.entity.GalleryPhoto;
import hai.ithust.photopicker.entity.PhotoDirectory;
import hai.ithust.photopicker.event.OnPhotoListener;
import hai.ithust.photopicker.holder.PhotoViewHolder;

/**
 * @author conghai on 12/20/18.
 */
public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private static final int VIEW_TYPE_PHOTO = 1;
    private static final int VIEW_TYPE_CAMERA = 2;

    private static final int ALL_PHOTO = -1;
    public static final int CAMERA_ITEM_ID = -1;

    private List<PhotoDirectory> mPhotoDirectories = new ArrayList<>();
    private List<GalleryPhoto> mPhotos = new ArrayList<>();
    private ArrayList<String> mSelectedPhotos = new ArrayList<>();
    private int mDirectoryCategory = ALL_PHOTO;
    private int mMaxItem;
    private int mImageSize;

    private RequestManager mGlide;
    private OnPhotoListener mOnItemCheckListener;

    public PhotoGridAdapter(RequestManager requestManager, ArrayList<String> originalPhotos, int imageSize, int maxItem, OnPhotoListener listener) {
        mImageSize = imageSize;
        mGlide = requestManager;
        mMaxItem = maxItem;
        if (originalPhotos != null) mSelectedPhotos.addAll(originalPhotos);

        // init item camera
        mPhotos.add(new GalleryPhoto(CAMERA_ITEM_ID, ""));
        mOnItemCheckListener = listener;
    }

    // return true if update success, false if not. It means max image can selected
    public boolean updatePhoto(int position) {
        if (position >= 0 && position < mPhotos.size()) {
            GalleryPhoto photo = mPhotos.get(position);
            if (isSelected(photo)) {
                // user unselected photo
                toggleSelection(photo);
                notifyItemChanged(position);
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
        if (mPhotoDirectories != null && !photoDirectories.isEmpty()) {
            mPhotoDirectories.addAll(photoDirectories);
        }

        if (mDirectoryCategory == ALL_PHOTO) {
            for (PhotoDirectory directory : mPhotoDirectories) {
                if (directory != null) {
                    mPhotos.addAll(directory.getPhotos());
                }
            }
        } else if (mDirectoryCategory >= 0 && mDirectoryCategory < mPhotoDirectories.size()) {
            mPhotos.addAll(mPhotoDirectories.get(mDirectoryCategory).getPhotos());
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mPhotos.get(position).getId() == CAMERA_ITEM_ID) {
            return VIEW_TYPE_CAMERA;
        }
        return VIEW_TYPE_PHOTO;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_item_photo, parent, false);
        return new PhotoViewHolder(itemView, mOnItemCheckListener, viewType == VIEW_TYPE_CAMERA);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.bindData(mPhotos.get(position), mGlide, mImageSize, isSelected(mPhotos.get(position)));
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void addNewPhoto(GalleryPhoto photo) {
        if (photo != null) {
            if (mPhotos.size() > 1) {
                mPhotos.add(1, photo);
                if (mPhotoDirectories.size() > 0) {
                    mPhotoDirectories.get(0).addPhoto(photo);
                }
                notifyItemInserted(1);
            }
        }
    }

    public List<PhotoDirectory> getPhotoDirectories() {
        return mPhotoDirectories;
    }

    public boolean isSelected(GalleryPhoto photo) {
        return mSelectedPhotos.contains(photo.getPath());
    }

    public void toggleSelection(GalleryPhoto photo) {
        if (mSelectedPhotos.contains(photo.getPath())) {
            mSelectedPhotos.remove(photo.getPath());
        } else {
            mSelectedPhotos.add(photo.getPath());
        }
    }

    public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
        if (currentDirectoryIndex != mDirectoryCategory) {
            mPhotos.clear();
            mPhotos.add(new GalleryPhoto(CAMERA_ITEM_ID, ""));
            if (currentDirectoryIndex == ALL_PHOTO) {
                mDirectoryCategory = ALL_PHOTO;
                for (PhotoDirectory directory : mPhotoDirectories) {
                    if (directory != null) {
                        mPhotos.addAll(directory.getPhotos());
                    }
                }
            } else if (currentDirectoryIndex >= 0 && currentDirectoryIndex < mPhotoDirectories.size()) {
                mDirectoryCategory = currentDirectoryIndex;
                mPhotos.addAll(mPhotoDirectories.get(currentDirectoryIndex).getPhotos());
            }
            notifyDataSetChanged();
        }
    }


    public ArrayList<String> getSelectedPhotos() {
        return mSelectedPhotos;
    }

    public int getMaxItem() {
        return mMaxItem;
    }
}
