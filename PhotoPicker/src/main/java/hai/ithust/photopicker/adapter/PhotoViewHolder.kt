package hai.ithust.photopicker.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import hai.ithust.photopicker.R
import hai.ithust.photopicker.entity.GalleryPhoto
import hai.ithust.photopicker.event.OnPhotoListener
import java.lang.ref.WeakReference

/**
 * @author conghai on 12/20/18.
 */
class PhotoViewHolder(
        itemView: View,
        private val listener: OnPhotoListener
) : RecyclerView.ViewHolder(itemView) {
    private val ivPhoto: ImageView = itemView.findViewById(R.id.iv_photo)
    private val vSelected: TextView = itemView.findViewById(R.id.tv_selected)
    private val frame: View = itemView.findViewById(R.id.frame)

    init {
        ivPhoto.setOnClickListener {
            listener.onItemCheck(adapterPosition)
        }
        vSelected.setOnClickListener {
            listener.onItemCheck(adapterPosition)
        }
    }

    fun bindData(photo: GalleryPhoto, manager: RequestManager, selectedPosition: Int) {
        manager.load(photo.uri)
                .placeholder(R.drawable.picker_bg_item_new_gray)
                .centerCrop()
                .into(ivPhoto)

        if (selectedPosition >= 0) {
            frame.visibility = View.VISIBLE
            vSelected.visibility = View.VISIBLE
            vSelected.text = (selectedPosition + 1).toString()
        } else {
            frame.visibility = View.GONE
            vSelected.visibility = View.GONE
        }
    }
}