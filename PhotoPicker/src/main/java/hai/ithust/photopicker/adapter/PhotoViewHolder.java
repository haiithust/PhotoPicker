package hai.ithust.photopicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
class PhotoViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivPhoto;
    private TextView vSelected;
    private View frame;
    private WeakReference<OnPhotoListener> weakListener;

    PhotoViewHolder(View itemView, OnPhotoListener listener) {
        super(itemView);
        ivPhoto = itemView.findViewById(R.id.iv_photo);
        vSelected = itemView.findViewById(R.id.tv_selected);
        frame = itemView.findViewById(R.id.frame);
        weakListener = new WeakReference<>(listener);

        ivPhoto.setOnClickListener(view -> {
            if (weakListener != null && weakListener.get() != null) {
                weakListener.get().onItemCheck(view, getAdapterPosition());
            }
        });
        vSelected.setOnClickListener(view -> {
            if (weakListener != null && weakListener.get() != null) {
                weakListener.get().onItemCheck(view, getAdapterPosition());
            }
        });
    }

    void bindData(GalleryPhoto photo, RequestManager manager, int selectedPosition) {
        manager.load(new File(photo.getPath()))
                .placeholder(R.drawable.picker_bg_item_new_gray)
                .centerCrop()
                .into(ivPhoto);

        if (selectedPosition >= 0) {
            frame.setVisibility(View.VISIBLE);
            vSelected.setVisibility(View.VISIBLE);
            vSelected.setText(String.valueOf(selectedPosition + 1));
        } else {
            frame.setVisibility(View.GONE);
            vSelected.setVisibility(View.GONE);
        }
    }
}

