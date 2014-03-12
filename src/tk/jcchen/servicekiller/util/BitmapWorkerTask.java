package tk.jcchen.servicekiller.util;

import java.lang.ref.WeakReference;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<Pair<ActivityInfo, PackageManager>, Void, Drawable> {

	private final WeakReference<ImageView> imageViewRef;
	private ActivityInfo data = null;
	
	public BitmapWorkerTask(ImageView imageView) {
		imageViewRef = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Drawable doInBackground(Pair<ActivityInfo, PackageManager>... params) {
		Pair p = params[0];
		ActivityInfo info = (ActivityInfo) p.first;
		PackageManager pm = (PackageManager) p.second;
		data = info;
		return info.loadIcon(pm);
	}

	@Override
	protected void onPostExecute(Drawable result) {
		if(isCancelled()) {
			result = null;
		}
		if(imageViewRef != null && result != null) {
			final ImageView imageView = imageViewRef.get();
			final BitmapWorkerTask bitmapWorkTask = AppIconAsyncLoadUtils.getBitmapWorkerTask(imageView);
			if(bitmapWorkTask != null && imageView != null) {
				imageView.setImageDrawable(result);
			}
		}
	}

	public ActivityInfo getData() {
		return data;
	}
}


