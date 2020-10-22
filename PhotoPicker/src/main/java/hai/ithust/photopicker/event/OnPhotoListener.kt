package hai.ithust.photopicker.event

interface OnPhotoListener {
    fun onItemCheck(position: Int)
    fun onOpenCamera()
}