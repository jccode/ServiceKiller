package tk.jcchen.servicekiller.ui;

import java.util.ArrayList;
import java.util.List;

import tk.jcchen.servicekiller.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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

public class AppGridActivity extends Activity implements RetainedFragment.RetainedCallbacks {
	
	private final static String TAG = "ServiceKiller";
	private GridView appGrid;
	private final int IMG_WIDTH = 80;
	private RetainedFragment mRetainedFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_app_grid);
		// Show the Up button in the action bar.
		setupActionBar();
		
		appGrid = (GridView) findViewById(R.id.grid_apps);
		appGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		appGrid.setMultiChoiceModeListener(new MultiChoiceModeListener());
		
		FragmentManager fm = getFragmentManager();
		mRetainedFragment = (RetainedFragment) fm.findFragmentByTag("work");
		if(mRetainedFragment == null) {
			mRetainedFragment = new RetainedFragment();
			fm.beginTransaction().add(mRetainedFragment, "work").commit();
		} else if(mRetainedFragment.mApps != null) {
			showGrid();
		}
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
		queryItem.setActionView(mRetainedFragment.mSearchView);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onPostExecute() {
		showGrid();
	}

	private void showGrid() {
		appGrid.setAdapter(mRetainedFragment.mAdapter);
		appGrid.setVisibility(View.VISIBLE);
	}
	

	public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			
			ArrayList<String> checkedApps = new ArrayList<String>();
			int len = appGrid.getCount();
			SparseBooleanArray checked = appGrid.getCheckedItemPositions();
			for(int i = 0; i < len; i++) {
				if(checked.get(i)) {
					checkedApps.add(mRetainedFragment.mLabelIcons.get(i).packageName);
				}
			}
//			Toast.makeText(getBaseContext(), "Selected items:"+checkedApps.toString(), Toast.LENGTH_SHORT).show();
			
			// put the selected result to MainActivity
			Intent result = new Intent();
			result.putStringArrayListExtra("result", checkedApps);
			AppGridActivity.this.setResult(RESULT_OK, result);
			AppGridActivity.this.finish();
			
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			Resources res = getResources();
			int count = appGrid.getCheckedItemCount();
			mode.setTitle(R.string.title_select_item);
			mode.setSubtitle(res.getQuantityString(R.plurals.sub_title_select_item, count, count));
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			MenuItem addItem = menu.add(R.string.action_add);
			addItem.setIcon(R.drawable.ic_add);
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
//			ResolveInfo info = mApps.get(position);
//			String pkgName = info.activityInfo.packageName;
//			Log.d(TAG, pkgName);
		}
		
	}



}

class IconEntity {
	String name;
	Drawable image;
	String packageName;
	public IconEntity(String name, Drawable image, String packageName) {
		super();
		this.name = name;
		this.image = image;
		this.packageName = packageName;
	}
}


class CheckableView extends FrameLayout implements Checkable {

	private boolean mChecked;
	private FrameLayout mView;
	
	public CheckableView(Context context, ViewGroup parent) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = (FrameLayout) inflater.inflate(R.layout.app_grid_cell, parent, false);
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

class RetainedFragment extends Fragment implements OnQueryTextListener {
	
	public interface RetainedCallbacks {
		void onPostExecute();
	}
	
	private final static String TAG = "ServiceKiller";
	private RetainedCallbacks mCallback;
	private RelativeLayout progressbarComp;
	List<ResolveInfo> mApps = null;
	List<IconEntity> mLabelIcons = null;
	AppsAdapter mAdapter = null;
	SearchView mSearchView = null;
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (RetainedCallbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		createSearchView();
		
		new AsyncTask<Void, Void, Pair<List<ResolveInfo>,List<IconEntity>>>() {

			@Override
			protected void onPreExecute() {
			}

			@Override
			protected void onPostExecute(Pair<List<ResolveInfo>,List<IconEntity>> result) {
				mApps = result.first;
				mLabelIcons = result.second;
				mAdapter = new AppsAdapter();
				
				progressbarComp.setVisibility(View.GONE);
				mCallback.onPostExecute();
				
				Log.d(TAG, "AsyncTask onPostExecute.");
			}

			@Override
			protected Pair<List<ResolveInfo>,List<IconEntity>> doInBackground(Void... params) {
				List<ResolveInfo> apps = loadApps();
				List<IconEntity> labelIcons = initLableIcons(apps);
				return new Pair<List<ResolveInfo>, List<IconEntity>>(apps, labelIcons);
			}

			@Override
			protected void onProgressUpdate(Void... values) {
				super.onProgressUpdate(values);
				Log.d(TAG, "AsyncTask onProgressUpdate.");
			}
			
		}.execute();
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		progressbarComp = (RelativeLayout) getActivity().findViewById(R.id.progressbarComp);
		if(mApps == null) {
			progressbarComp.setVisibility(View.VISIBLE);
		}
		
		Log.d(TAG, "RetainedFragment onActivityCreated.");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
		progressbarComp = null;
		
		Log.d(TAG, "RetainedFragment onDetach.");
	}

	private List<ResolveInfo> loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		return getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
	}
	
	private List<IconEntity> initLableIcons(List<ResolveInfo> apps) {
		List<IconEntity> labelIcons = new ArrayList<IconEntity>(apps.size());
		PackageManager pm = getActivity().getPackageManager();
		for(ResolveInfo info : apps) {
			ActivityInfo i = info.activityInfo;
			labelIcons.add(new IconEntity(i.loadLabel(pm).toString(), i.loadIcon(pm), i.packageName));
		}
		return labelIcons;
	}
	
	private void createSearchView() {
		mSearchView = new SearchView(getActivity());
		mSearchView.setOnQueryTextListener(this);
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		if(TextUtils.isEmpty(newText)) {
			mAdapter.getFilter().filter("");
//			appGrid.clearTextFilter();
		} else {
			mAdapter.getFilter().filter(newText.toString());
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}
	
	
	public class AppsAdapter extends BaseAdapter implements Filterable {
		
		private List<IconEntity> origData;
		
		public AppsAdapter() {
			origData = new ArrayList<IconEntity>(mLabelIcons);
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
			
			CheckableView view;
			if(convertView == null) {
				view = new CheckableView(getActivity(), parent);
			} else {
				view = (CheckableView) convertView;
			}
			
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
						List<IconEntity> founded = new ArrayList<IconEntity>();
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
}