package hai.ithust.photopicker.fragment

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import hai.ithust.photopicker.PhotoPicker
import hai.ithust.photopicker.R
import hai.ithust.photopicker.adapter.PhotoGridAdapter
import hai.ithust.photopicker.entity.GalleryPhoto
import hai.ithust.photopicker.entity.PhotoDirectory
import hai.ithust.photopicker.event.OnPhotoListener
import hai.ithust.photopicker.utils.ImageCaptureManager
import hai.ithust.photopicker.utils.PermissionsConstant
import hai.ithust.photopicker.utils.PermissionsUtils.checkCameraPermission
import hai.ithust.photopicker.utils.PermissionsUtils.checkWriteStoragePermission

/**
 * @author conghai on 12/20/18.
 */
class PhotoPickerFragment : Fragment(R.layout.picker_fragment_photo_picker), OnPhotoListener, PhotoPickerCallback {
    private lateinit var tvTitle: TextView
    private lateinit var ivRightAction: ImageView
    private lateinit var presenter: PhotoPickerPresenter
    private lateinit var photoAdapter: PhotoGridAdapter
    private lateinit var captureManager: ImageCaptureManager

    private var mSelectDirectoryDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = PhotoPickerPresenter(this)
        val originalPhotos = requireArguments().getParcelableArrayList<Uri>(PhotoPicker.EXTRA_ORIGINAL_PHOTOS)
        val maxCount = requireArguments().getInt(PhotoPicker.EXTRA_MAX_COUNT, PhotoPicker.DEFAULT_MAX_COUNT)
        photoAdapter = PhotoGridAdapter(originalPhotos, maxCount, this)
        captureManager = ImageCaptureManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val column = requireArguments().getInt(PhotoPicker.EXTRA_GRID_COLUMN, PhotoPicker.DEFAULT_COLUMN_NUMBER)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_photos)

        recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(column, OrientationHelper.VERTICAL)
            itemAnimator = null
            setHasFixedSize(true)
            adapter = photoAdapter
        }

        tvTitle = view.findViewById(R.id.tv_directory_category)
        ivRightAction = view.findViewById(R.id.iv_right_action)

        tvTitle.setOnClickListener {
            if (isResumed) {
                mSelectDirectoryDialog = getDirectoryDialog(photoAdapter.directoryCategoryIndex)
                mSelectDirectoryDialog!!.show()
            }
        }

        ivRightAction.setOnClickListener {
            if (photoAdapter.isSelectPhoto) {
                setResult()
            } else {
                onOpenCamera()
            }
        }

        view.findViewById<View>(R.id.iv_navigation).setOnClickListener {
            activity?.finish()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.getPhotos(requireContext())
    }

    override fun onDestroyView() {
        mSelectDirectoryDialog?.dismiss()
        presenter.destroy()
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            captureManager.galleryAddPic()
            captureManager.currentUri?.let {
                photoAdapter.addNewPhoto(GalleryPhoto(it))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PermissionsConstant.REQUEST_CAMERA, PermissionsConstant.REQUEST_EXTERNAL_WRITE -> onOpenCamera()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        captureManager.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        captureManager.onRestoreInstanceState(savedInstanceState)
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onItemCheck(position: Int) {
        val isUpdated = photoAdapter.updatePhoto(position)
        updateRightAction()
        if (!isUpdated) {
            Toast.makeText(context, getString(R.string.__picker_over_max_count_tips, photoAdapter.maxItem), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOpenCamera() {
        if (checkWriteStoragePermission(this) &&
                checkCameraPermission(this)) {
            openCamera()
        }
    }

    override fun onGetListPhotoSuccess(directories: List<PhotoDirectory>) {
        photoAdapter.setPhotoDirectories(directories)
        updateRightAction()
    }

    private fun openCamera() {
        try {
            val intent = captureManager.dispatchTakePictureIntent()
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO)
        } catch (ignore: Exception) {
        }
    }

    private fun setResult() {
        val intent = Intent()
        intent.putParcelableArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS, photoAdapter.selectedPhotos)
        if (targetFragment != null) {
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        } else {
            requireActivity().setResult(Activity.RESULT_OK, intent)
            requireActivity().finish()
        }
    }

    private fun getDirectoryDialog(position: Int): AlertDialog {
        val directories = mutableListOf(getString(R.string.pp_gallery))
        directories.addAll(photoAdapter.photoDirectoryNames)
        val items = directories.toTypedArray<CharSequence>()
        val builder = AlertDialog.Builder(requireContext())
                .setTitle(R.string.pp_select_directory_title)
                .setSingleChoiceItems(items, position) { dialog: DialogInterface, which: Int ->
                    tvTitle.text = items[which]
                    photoAdapter.setCurrentDirectoryIndex(which)
                    dialog.dismiss()
                }
        return builder.create()
    }

    private fun updateRightAction() {
        if (photoAdapter.isSelectPhoto) {
            ivRightAction.setImageResource(R.drawable.picker_ic_done)
        } else {
            ivRightAction.setImageResource(R.drawable.picker_ic_camera)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle?): PhotoPickerFragment {
            return PhotoPickerFragment().apply {
                arguments = bundle
            }
        }
    }
}