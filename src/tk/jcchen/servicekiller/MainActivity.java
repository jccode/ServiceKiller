package tk.jcchen.servicekiller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import tk.jcchen.servicekiller.ui.AppGridActivity;
import tk.jcchen.servicekiller.ui.IconEntity;
import tk.jcchen.servicekiller.ui.SettingsActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	public static final String TAG = "ServiceKiller";
	static final int REQUEST_APPS = 1;
	private final List<String> defaultApps = Arrays.asList(
			"com.tencent.mm", "com.eg.android.AlipayGphone", "com.tencent.mobileqq");
	private ListView mListView;
	private RetainedFragment mFragment;
	private AppsArrayAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById(R.id.main);
		
		FragmentManager fm = getFragmentManager();
		mFragment = (RetainedFragment) fm.findFragmentByTag("work");
		if(mFragment == null) {
			mFragment = new RetainedFragment();
			loadApps(defaultApps);
			fm.beginTransaction().add(mFragment, "work").commit();
		}
		mListView.setEmptyView(findViewById(android.R.id.empty));
		
		mAdapter = new AppsArrayAdapter(this, mFragment.apps);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		
		switch (item.getItemId()) {
		case R.id.action_add:
			intent = new Intent(this, AppGridActivity.class);
			startActivityForResult(intent, REQUEST_APPS);
			return true;
			
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
			
		case R.id.action_kill:
			break;
			
		case R.id.action_remove:
			removeAppFromList();
			break;
			
		case R.id.action_about:
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_APPS) {
			if(resultCode == RESULT_OK) {
				ArrayList<String> result = data.getExtras().getStringArrayList("result");
				loadApps(result);
			}
		}
	}
	
	
	void loadApps(List<String> packageNames) {
		HashSet<IconEntity> entities = new HashSet<IconEntity>(mFragment.apps);
		PackageManager pm = getPackageManager();
		for(String packageName : packageNames) {
			try {
				ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
				Drawable icon = info.loadIcon(pm);
				CharSequence name = info.loadLabel(pm);
				IconEntity entity = new IconEntity(name.toString(), icon, packageName);
				entities.add(entity);
			} catch (NameNotFoundException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		mFragment.apps = new ArrayList<IconEntity>(entities);
		if(mAdapter != null) 
			mAdapter.notifyDataSetChanged();
	}
	
	private void removeAppFromList() {
		for(IconEntity entity : mFragment.apps) {
			if(entity.isSelected()) {
				mFragment.apps.remove(entity);
			}
		}
		mAdapter.notifyDataSetChanged();
	}
	
	
	public static class RetainedFragment extends Fragment {
		
		List<IconEntity> apps = new ArrayList<IconEntity>();
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		@Override
		public void onDetach() {
			super.onDetach();
		}
	}
	
	static class ViewHolder {
		ImageView icon;
		CheckedTextView text;
	}
	
	class AppsArrayAdapter extends ArrayAdapter<IconEntity> {
		
		private final Activity context;
		private final List<IconEntity> list;

		public AppsArrayAdapter(Activity context, List<IconEntity> list) {
			super(context, R.layout.app_list_item, list);
			this.context = context;
			this.list = list;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			IconEntity entity = list.get(position);
			ViewHolder holder = null;
			if(convertView == null) {
				view = context.getLayoutInflater().inflate(R.layout.app_list_item, null, false);
				holder = new ViewHolder();
				holder.icon = (ImageView) view.findViewById(R.id.listitem_icon);
				holder.text = (CheckedTextView) view.findViewById(R.id.listitem_text);
				holder.text.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CheckedTextView text = ((CheckedTextView) v);
						IconEntity entity = (IconEntity) v.getTag();
						entity.setSelected(text.isSelected());
						text.toggle();
					}
				});
				
				view.setTag(holder);
			} else {
				view = convertView;
			}
			
			holder = (ViewHolder) view.getTag();
			holder.text.setText(entity.getName());
			holder.text.setTag(entity);
			holder.icon.setBackground(entity.getImage());
			
			return view;
		}
		
	}
}

