package Settings;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class NotificationSettingActivity extends PreferenceActivity{
Preference p;
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) 	
		{     
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);   
		}
		getFragmentManager().beginTransaction().replace(android.R.id.content, new NotifSettingsFrag()).commit();
	}
}
