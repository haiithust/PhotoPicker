package hai.ithust.photopicker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author conghai on 12/20/18.
 */
public class SquareItemLayout extends FrameLayout {
  public SquareItemLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public SquareItemLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SquareItemLayout(Context context) {
    super(context);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
    int childWidthSize = getMeasuredWidth();
    heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }
}
