package tk.jcchen.servicekiller.ui;

import java.util.List;

import tk.jcchen.servicekiller.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class AppGridActivity extends Activity {
	
	GridView appGrid;
	private List<ResolveInfo> mApps;
	private final int IMG_WIDTH = 80;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApps = loadApps();
		
		setContentView(R.layout.activity_app_grid);
		// Show the Up button in the action bar.
		setupActionBar();
		
		appGrid = (GridView) findViewById(R.id.grid_apps);
		appGrid.setAdapter(new AppsAdapter(this));
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
//		getMenuInflater().inflate(R.menu.app_grid, menu);
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
		
		private LayoutInflater mLayoutInflater;
		
		public AppsAdapter(Context context) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			
			ActivityInfo info = mApps.get(position).activityInfo;
			Drawable img = info.loadIcon(getPackageManager());
			CharSequence name = info.loadLabel(getPackageManager());
			
			CheckableView view;
			if(convertView == null) {
				view = new CheckableView(AppGridActivity.this, parent);
			} else {
				view = (CheckableView) convertView;
			}
			view.setView(img, name);
			return view;
		}
		
	}
	
	
	public class CheckableView extends FrameLayout implements Checkable {

		private boolean mChecked;
		private FrameLayout mView;
		
		public CheckableView(Context context, ViewGroup parent) {
			super(context);
			mView = (FrameLayout) getLayoutInflater().inflate(R.layout.app_grid_cell, parent, false);
			this.addView(mView);
		}
		
		public CheckableView(Context context, ViewGroup parent, Drawable img, CharSequence name) {
			this(context, parent);
			setView(img, name);
		}
		
		protected void setView(Drawable img, CharSequence name) {
			((ImageView)mView.findViewById(R.id.app_img)).setImageDrawable(img);
			((TextView)mView.findViewById(R.id.app_name)).setText(name);
		}

		@Override
		public boolean isChecked() {
			return mChecked;
		}

		@Override
		public void setChecked(boolean checked) {
			mChecked = checked;
			
			if(checked) {
				mView.findViewById(R.id.app_overlay).setVisibility(VISIBLE);
			} else {
				mView.findViewById(R.id.app_overlay).setVisibility(INVISIBLE);
			}
		}

		@Override
		public void toggle() {
			setChecked(!mChecked);
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
