package tk.jcchen.servicekiller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	public static final String LOG_TAG = "ServiceKiller";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
			intentClass = "tk.jcchen.servicekiller.ui.AppGridActivity";
			break;
			
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

}
