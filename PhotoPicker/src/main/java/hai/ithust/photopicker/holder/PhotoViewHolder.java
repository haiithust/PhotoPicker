package hai.ithust.photopicker.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;

import java.io.File;
import java.lang.ref.WeakReference;

import hai.ithust.photopicker.R;
import hai.ithust.photopicker.adapter.PhotoGridAdapter;
import hai.ithust.photopicker.entity.GalleryPhoto;
import hai.ithust.photopicker.event.OnPhotoListener;

/**
 * @author conghai on 12/20/18.
 */
public class PhotoViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivPhoto;
    private View vSelected;
    private WeakReference<OnPhotoListener> weakListener;

    public PhotoViewHolder(View itemView, OnPhotoListener listener, boolean isCamera) {
        super(itemView);
        ivPhoto = itemView.findViewById(R.id.iv_photo);
        vSelected = itemView.findViewById(R.id.v_selected);
        weakListener = new WeakReference<>(listener);

        if (isCamera) {
            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (weakListener != null && weakListener.get() != null) {
                        weakListener.get().onOpenCamera();
                    }
                }
            });
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            ivPhoto.setImageResource(R.drawable.picker_ic_camera);
            vSelected.setVisibility(View.GONE);
        } else {
            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (weakListener != null && weakListener.get() != null) {
                        weakListener.get().onItemCheck(view, getAdapterPosition());
                    }
                }
            });
            vSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (weakListener != null && weakListener.get() != null) {
                        weakListener.get().onItemCheck(view, getAdapterPosition());
                    }
                }
            });
        }
    }

    public void bindData(GalleryPhoto photo, RequestManager manager, int imageSize, boolean isSelected) {
        if (photo.getId() != PhotoGridAdapter.CAMERA_ITEM_ID) {
            manager.load(new File(photo.getPath()))
                    .dontAnimate()
                    .override(imageSize, imageSize)
                    .placeholder(R.drawable.picker_bg_item_new_gray)
                    .into(ivPhoto);

            vSelected.setSelected(isSelected);
            ivPhoto.setSelected(isSelected);
        }
    }
}

