package tk.jcchen.servicekiller.ui;

import java.util.ArrayList;
import java.util.List;

import tk.jcchen.servicekiller.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class AppGridActivity extends Activity implements OnQueryTextListener {
	
	private final static String TAG = "ServiceKiller";
	private GridView appGrid;
	private RelativeLayout progressbarComp;
	private AppsAdapter mAdapter;
	private List<ResolveInfo> mApps;
	private final int IMG_WIDTH = 80;
	private List<IconEntity> mLabelIcons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_app_grid);
		// Show the Up button in the action bar.
		setupActionBar();
		
		appGrid = (GridView) findViewById(R.id.grid_apps);
//		appGrid.setAdapter(new AppsAdapter(this));
		appGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		appGrid.setMultiChoiceModeListener(new MultiChoiceModeListener());
		
		progressbarComp = (RelativeLayout) findViewById(R.id.progressbarComp);
		
		
		new AsyncTask<Void, Void, Pair<List<ResolveInfo>,List<IconEntity>>>() {

			@Override
			protected void onPreExecute() {
			}

			@Override
			protected void onPostExecute(Pair<List<ResolveInfo>,List<IconEntity>> result) {
				mApps = result.first;
				mLabelIcons = result.second;
				
				mAdapter = new AppsAdapter(AppGridActivity.this);
				appGrid.setAdapter(mAdapter);
				
				progressbarComp.setVisibility(View.GONE);
				appGrid.setVisibility(View.VISIBLE);
			}

			@Override
			protected Pair<List<ResolveInfo>,List<IconEntity>> doInBackground(Void... params) {
				List<ResolveInfo> apps = loadApps();
				List<IconEntity> labelIcons = initLableIcons(apps);
				return new Pair<List<ResolveInfo>, List<IconEntity>>(apps, labelIcons);
			}
			
			
		}.execute();
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
		MenuItem queryItem = menu.findItem(R.id.action_search);
		SearchView sv = new SearchView(this);
		sv.setOnQueryTextListener(this);
		queryItem.setActionView(sv);
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
	
	private List<IconEntity> initLableIcons(List<ResolveInfo> apps) {
		List<IconEntity> labelIcons = new ArrayList<IconEntity>(apps.size());
		PackageManager pm = getPackageManager();
		for(ResolveInfo info : apps) {
			ActivityInfo i = info.activityInfo;
			labelIcons.add(new IconEntity(i.loadLabel(pm).toString(), i.loadIcon(pm)));
		}
		return labelIcons;
	}
	
//	----- implements OnQueryTextListener ------
	
	@Override
	public boolean onQueryTextChange(String newText) {
		if(TextUtils.isEmpty(newText)) {
			mAdapter.getFilter().filter("");
			appGrid.clearTextFilter();
		} else {
			mAdapter.getFilter().filter(newText.toString());
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}
	
	

//	=================
//	inner class
//	=================
	
	static class IconEntity {
		String name;
		Drawable image;
		public IconEntity(String name, Drawable image) {
			super();
			this.name = name;
			this.image = image;
		}
	}
	
	public class AppsAdapter extends BaseAdapter implements Filterable {
		
		private LayoutInflater mLayoutInflater;
//		private AppIconAsyncLoadUtils mIconUtils;
		private List<IconEntity> origData;
		
		public AppsAdapter(Context context) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			mIconUtils = new AppIconAsyncLoadUtils(getResources());
			origData = new ArrayList<AppGridActivity.IconEntity>(mLabelIcons);
		}

		@Override
		public int getCount() {
			return mLabelIcons.size();
		}

		@Override
		public Object getItem(int position) {
			return mLabelIcons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
//			ActivityInfo info = mApps.get(position).activityInfo;
//			PackageManager pm = getPackageManager();
////			Drawable img = info.loadIcon(pm);
//			CharSequence name = info.loadLabel(pm);
			
			CheckableView view;
			if(convertView == null) {
				view = new CheckableView(AppGridActivity.this, parent);
			} else {
				view = (CheckableView) convertView;
			}
			
//			view.setView(img, name);
//			view.setAppName(name);
//			mIconUtils.loadImageView(info, pm, view.getImageView());
			
			IconEntity lableIcon = mLabelIcons.get(position);
			view.setView(lableIcon.image, lableIcon.name);
			
			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					constraint = constraint.toString().toLowerCase();
					FilterResults result = new FilterResults();
					if(constraint != null && constraint.toString().length() > 0) {
						List<IconEntity> founded = new ArrayList<AppGridActivity.IconEntity>();
						for(IconEntity item : origData) {
							if(item.name.toLowerCase().contains(constraint)) {
								founded.add(item);
							}
						}
						result.values = founded;
						result.count = founded.size();
						
					} else {
						result.values = origData;
						result.count = origData.size();
					}
					
					return result;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					mLabelIcons = (List<IconEntity>) results.values;
					notifyDataSetChanged();
				}
				
			};
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
			getImageView().setImageDrawable(img);
			setAppName(name);
		}
		
		protected void setAppName(CharSequence name) {
			((TextView)mView.findViewById(R.id.app_name)).setText(name);
		}
		
		protected ImageView getImageView() {
			return ((ImageView)mView.findViewById(R.id.app_img));
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
//			Toast.makeText(getBaseContext(), "Action Item clicked!", Toast.LENGTH_SHORT).show();
			
			List<String> checkedApps = new ArrayList<String>();
			int len = appGrid.getCount();
			SparseBooleanArray checked = appGrid.getCheckedItemPositions();
			for(int i = 0; i < len; i++) {
				if(checked.get(i)) {
					checkedApps.add(mLabelIcons.get(i).name);
				}
			}
			Toast.makeText(getBaseContext(), "Selected items:"+checkedApps.toString(), Toast.LENGTH_SHORT).show();
			
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
			MenuItem addItem = menu.add(R.string.action_add);
			addItem.setIcon(R.drawable.add);
			addItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
			Log.d(TAG, pkgName);
		}
		
	}

}
