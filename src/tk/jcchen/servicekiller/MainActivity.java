package tk.jcchen.servicekiller;

import java.util.ArrayList;

import tk.jcchen.servicekiller.ui.AppGridActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static final String LOG_TAG = "ServiceKiller";
	static final int REQUEST_APPS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pkg = "com.tencent.mm";
				PackageManager pm = getPackageManager();
				try {
					ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
					Drawable icon = ai.loadIcon(pm);
					CharSequence label = ai.loadLabel(pm);
					
					RelativeLayout layout = (RelativeLayout) MainActivity.this.findViewById(R.id.main);
					ImageView imageView = new ImageView(MainActivity.this);
					imageView.setBackground(icon);
					imageView.setId(1);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT, 
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.BELOW, R.id.textView1);
					layout.addView(imageView, params);
					
					TextView textView = new TextView(MainActivity.this);
					textView.setText(label);
					RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT, 
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					params2.addRule(RelativeLayout.RIGHT_OF, 1);
					layout.addView(textView, params2);
					
					
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String intentClass = null;
		
		switch (item.getItemId()) {
		case R.id.action_add:
//			intentClass = "tk.jcchen.servicekiller.ui.AppGridActivity";
//			break;
			Intent intent = new Intent(this, AppGridActivity.class);
			startActivityForResult(intent, REQUEST_APPS);
			return true;
			
		case R.id.action_settings:
			intentClass = "tk.jcchen.servicekiller.ui.SettingsActivity";
			break;
			

		default:
			return super.onOptionsItemSelected(item);
		}
		
		if(intentClass != null) {
			try {
//				Log.i(LOG_TAG, intentClass);
				Intent intent = new Intent(this, Class.forName(intentClass));
				startActivity(intent);
//				Log.i(LOG_TAG, "start activity " + intentClass);
			} catch (ClassNotFoundException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
		}
		
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_APPS) {
			if(resultCode == RESULT_OK) {
				ArrayList<String> result = data.getExtras().getStringArrayList("result");
				((TextView)this.findViewById(R.id.textView1)).setText(result.toString());
			}
		}
	}
}
