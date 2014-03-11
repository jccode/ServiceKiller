package tk.jcchen.servicekiller.ui;

import java.util.List;

import tk.jcchen.servicekiller.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.ImageView;

public class AppGridActivity extends Activity {
	
	GridView appGrid;
	private List<ResolveInfo> mApps;
	private final int IMG_WIDTH = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApps = loadApps();
		
		setContentView(R.layout.activity_app_grid);
		// Show the Up button in the action bar.
		setupActionBar();
		
		appGrid = (GridView) findViewById(R.id.grid_apps);
		appGrid.setAdapter(new AppsAdapter());
		appGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		appGrid.setMultiChoiceModeListener(new MultiChoiceModeListener());
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_grid, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private List<ResolveInfo> loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		return getPackageManager().queryIntentActivities(mainIntent, 0);
	}
	
	
	public class AppsAdapter extends BaseAdapter {
		
		public AppsAdapter() {
		}

		@Override
		public int getCount() {
			return mApps.size();
		}

		@Override
		public Object getItem(int position) {
			return mApps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			CheckableImageView i;
			if(convertView == null) {
				i = new CheckableImageView(AppGridActivity.this);
				i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                	i.setLayoutParams(new GridView.LayoutParams(IMG_WIDTH, IMG_WIDTH));
			} else {
				i = (CheckableImageView)convertView;
			}
			
			ResolveInfo info = mApps.get(position);
			i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
			
			return i;
		}
		
	}
	
	public class CheckableImageView extends ImageView implements Checkable {
		
		private boolean mChecked;
		
		public CheckableImageView(Context context) {
			super(context);
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
	
	
	public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			Resources res = getResources();
			mode.setTitle(R.string.title_select_item);
			mode.setSubtitle(res.getQuantityString(R.plurals.sub_title_select_item, 1, 1));
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			Resources res = getResources();
			int selectCount = appGrid.getCheckedItemCount();
			mode.setSubtitle(res.getQuantityString(R.plurals.sub_title_select_item, selectCount, selectCount));
			
			// Test: get package name
			ResolveInfo info = mApps.get(position);
			String pkgName = info.activityInfo.packageName;
			Log.d("ServiceKiller", pkgName);
		}
		
	}
}
