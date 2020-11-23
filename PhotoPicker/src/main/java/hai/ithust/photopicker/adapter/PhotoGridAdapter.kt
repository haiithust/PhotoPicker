package hai.ithust.photopicker.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import hai.ithust.photopicker.R
import hai.ithust.photopicker.entity.GalleryPhoto
import hai.ithust.photopicker.entity.PhotoDirectory
import hai.ithust.photopicker.event.OnPhotoListener
import java.util.ArrayList
import kotlin.math.abs

/**
 * @author conghai on 12/20/18.
 */
class PhotoGridAdapter(
        originalPhotos: List<Uri>?,
        val maxItem: Int,
        private val listener: OnPhotoListener
) : RecyclerView.Adapter<PhotoViewHolder>() {
    private val photoDirectories: MutableList<PhotoDirectory> = ArrayList()
    private val photos: MutableList<GalleryPhoto> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var glide: RequestManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    val selectedPhotos = arrayListOf<Uri>()
    var directoryCategoryIndex = ALL_PHOTO
        private set

    init {
        if (originalPhotos != null) selectedPhotos.addAll(originalPhotos)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        glide = Glide.with(this.recyclerView.context)
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (abs(dy) < SCROLL_THRESHOLD) {
                    resumeLoadImage()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeLoadImage()
                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    pauseLoadImage()
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(scrollListener)
        glide.onDestroy()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.picker_item_photo, parent, false)
        return PhotoViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bindData(photos[position], glide, selectedPhotos.indexOf(photos[position].uri))
    }

    override fun getItemCount(): Int = photos.size

    // return true if update success, false if not. It means max image can selected
    fun updatePhoto(position: Int): Boolean {
        if (position in photos.indices) {
            val photo = photos[position]
            val selectedNumber = selectedPhotos.indexOf(photo.uri)
            if (selectedNumber > -1) {
                // user unselected photo
                toggleSelection(photo)
                notifyItemChanged(position)

                // if user unselected last number, we no need calculate index again
                if (selectedNumber < selectedPhotos.size) {
                    for (i in photos.indices) {
                        if (isSelected(photos[i])) {
                            notifyItemChanged(i)
                        }
                    }
                }
                return true
            } else {
                // user selected photo
                if (selectedPhotos.size < maxItem) {
                    toggleSelection(photo)
                    notifyItemChanged(position)
                    return true
                }
            }
        }
        return false
    }

    fun setPhotoDirectories(photoDirectories: List<PhotoDirectory>) {
        this.photoDirectories.clear()
        if (photoDirectories.isNotEmpty()) {
            this.photoDirectories.addAll(photoDirectories)
        }
        if (directoryCategoryIndex == ALL_PHOTO) {
            for (directory in this.photoDirectories) {
                photos.addAll(directory.photos)
            }
        } else if (directoryCategoryIndex >= 0 && directoryCategoryIndex < this.photoDirectories.size) {
            photos.addAll(this.photoDirectories[directoryCategoryIndex].photos)
        }
        notifyDataSetChanged()
    }

    fun addNewPhoto(photo: GalleryPhoto) {
        photos.add(0, photo)
        if (photoDirectories.isNotEmpty()) {
            photoDirectories[0].photos.add(photo)
        }
        notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
    }

    val photoDirectoryNames: List<String>
        get() = photoDirectories.filter { !it.name.isNullOrEmpty() }.map { it.name!! }

    fun setCurrentDirectoryIndex(currentDirectoryIndex: Int) {
        if (currentDirectoryIndex != directoryCategoryIndex) {
            photos.clear()
            if (currentDirectoryIndex == ALL_PHOTO) {
                directoryCategoryIndex = ALL_PHOTO
                for (directory in photoDirectories) {
                    photos.addAll(directory.photos)
                }
            } else if (currentDirectoryIndex > 0 && currentDirectoryIndex <= photoDirectories.size) {
                directoryCategoryIndex = currentDirectoryIndex
                photos.addAll(photoDirectories[currentDirectoryIndex - 1].photos)
            }
            notifyDataSetChanged()
        }
    }

    val isSelectPhoto: Boolean
        get() = selectedPhotos.isNotEmpty()

    private fun resumeLoadImage() {
        if (glide.isPaused) {
            glide.resumeRequests()
        }
    }

    private fun pauseLoadImage() {
        if (!glide.isPaused) {
            glide.pauseRequests()
        }
    }

    private fun isSelected(photo: GalleryPhoto): Boolean {
        return selectedPhotos.contains(photo.uri)
    }

    private fun toggleSelection(photo: GalleryPhoto) {
        if (selectedPhotos.contains(photo.uri)) {
            selectedPhotos.remove(photo.uri)
        } else {
            selectedPhotos.add(photo.uri)
        }
    }

    companion object {
        private const val ALL_PHOTO = 0
        private const val SCROLL_THRESHOLD = 30
    }
}