package hai.ithust.photopicker.event;

import android.view.View;

public interface OnPhotoListener {

  void onItemCheck(View view, int position);

  void onOpenCamera();
}
