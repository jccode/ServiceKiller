package tk.jcchen.servicekiller.util;

import tk.jcchen.servicekiller.R;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.widget.ImageView;

public class AppIconAsyncLoadUtils {

	private Resources mResources;
	private Bitmap mPlaceHolder;
	
	public AppIconAsyncLoadUtils(Resources resources) {
		mResources = resources;
		mPlaceHolder = BitmapFactory.decodeResource(mResources, R.drawable.empty_photo);
	}
	
	public AppIconAsyncLoadUtils(Resources resources, Bitmap placeHolder) {
		this(resources);
		mPlaceHolder = placeHolder;
	}
	
	public void loadImageView(ActivityInfo info, PackageManager pm, ImageView imageView) {
		if(cancelPotentialWork(info, imageView)) {
			// Before executing the BitmapWorkerTask, you create an AsyncDrawable and bind it to the target ImageView:
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mPlaceHolder, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(new Pair<ActivityInfo, PackageManager>(info, pm));
		}
	}
	
	/**
	 * The cancelPotentialWork method referenced in the code sample above checks 
	 * if another running task is already associated with the ImageView. 
	 * If so, it attempts to cancel the previous task by calling cancel(). 
	 * In a small number of cases, the new task data matches the existing task and nothing further needs to happen. 
	 * 
	 * @param data
	 * @param imageView
	 * @return
	 */
	public static boolean cancelPotentialWork(ActivityInfo data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		if(bitmapWorkerTask != null) {
			final ActivityInfo bitmapData = bitmapWorkerTask.getData();
			
			// if bitmapData is not yet set or it differs from the new data
			if(bitmapData == null || !bitmapData.equals(data)) {
				bitmapWorkerTask.cancel(true);
			} else {
				return false;
			}
		}
		
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}
	
	
	public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if(imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if(drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}
}
