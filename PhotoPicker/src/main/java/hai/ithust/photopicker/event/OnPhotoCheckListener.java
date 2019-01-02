package hai.ithust.photopicker.event;

import android.view.View;

public interface OnPhotoCheckListener {

  void onItemCheck(View view, int position);

  void onOpenCamera();
}
