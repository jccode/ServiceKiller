package tk.jcchen.servicekiller.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

public class CheckableImageView extends ImageView implements Checkable {
		
		private boolean mChecked;
		
		public CheckableImageView(Context context) {
			super(context);
		}
		
		public CheckableImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		public CheckableImageView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}
		

		@Override
		public boolean isChecked() {
			return mChecked;
		}

		@Override
		public void setChecked(boolean checked) {
			mChecked = checked;
			
			// set style of checked. repaint
			invalidate();
		}

		@Override
		public void toggle() {
			setChecked(!mChecked);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if(mChecked)
				drawOverlay(canvas);
		}
		
		private void drawOverlay(Canvas canvas) {
			Paint paint = new Paint();
			paint.setColor(Color.CYAN);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(10);
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
			
			paint.setStyle(Paint.Style.FILL);
			paint.setAlpha(80);
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
		}
		
	}