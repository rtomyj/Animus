package com.AnimusSubActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.PlusOneButton;
import com.rtomyj.Diary.R;

import java.lang.ref.WeakReference;

public class AboutMeActivity extends AppCompatActivity {

	private PlusOneButton mPlusOneButton;
	private String theme;

	private AlertDialog.Builder alert;
	private View alertView;
	private TextView title;
	private TextView tv;

	private Intent intent;

	protected void onResume() {
		super.onResume();
		// Refresh the state of the +1 button each time the activity receives
		// focus.
		          mPlusOneButton.initialize("https://play.google.com/store/apps/details?id=com.rtomyj.Diary",  0);


	}


	// Custom methods

	private void setTheme(){
		theme = PreferenceManager
				.getDefaultSharedPreferences(this).getString("Theme", "Default");

		if (theme.equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}
		else if (theme.equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		}
		else if (theme.equals("Material")){
			super.setTheme(R.style.Material);
		}
		else if (theme.equals("Material 2")){
			super.setTheme(R.style.Material2);
		}
		else if(theme.equals("Material 3")){
			super.setTheme(R.style.Material3);
		}else if(theme.equals("Material 4")){
			super.setTheme(R.style.Material4);
		}
	}

	// End custom methods...
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme();

		setContentView(R.layout.about_me);


		Toolbar actionbar = (Toolbar) findViewById(R.id.toolbar);
		WeakReference<Toolbar> actionbarWeak = new WeakReference<>(actionbar);
		setSupportActionBar(actionbarWeak.get());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (theme.contains("Onyx")){
			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
			TextView myInfo = (TextView) findViewById(R.id.my_info);
			ScrollView scrollview = (ScrollView) findViewById(R.id.scrollView1);


			parent.setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));
			myInfo.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));
			scrollview.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));
			myInfo.setTextColor(getResources().getColor(R.color.UIDarkText));


			Button facebook = (Button) findViewById(R.id.facebook);
			Button twitter = (Button) findViewById(R.id.twitter);
			Button bugsImprovements = (Button) findViewById(R.id.bugs_improvements);
			Button credits = (Button) findViewById(R.id.credits);
			Button me = (Button) findViewById(R.id.me);
			Button history = (Button) findViewById(R.id.history);

			facebook.setTextColor(getResources().getColor(R.color.UIDarkText));
			twitter.setTextColor(getResources().getColor(R.color.UIDarkText));
			bugsImprovements.setTextColor(getResources().getColor(R.color.UIDarkText));
			credits.setTextColor(getResources().getColor(R.color.UIDarkText));
			me.setTextColor(getResources().getColor(R.color.UIDarkText));
			history.setTextColor(getResources().getColor(R.color.UIDarkText));
		}

		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		// mPlusClient = PlusClient.Builder$build;
		mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);

	}

	 public void bugsClicked(View v) {
		intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
		 intent.setType("text/plain");
		 intent.putExtra(Intent.EXTRA_SUBJECT, "");
		 intent.putExtra(Intent.EXTRA_TEXT, "");
		 intent.setData(Uri.parse("mailto:corporationawesome@gmail.com")); // or
																		// just
																		// "mailto:"
																		// for
																		// blank
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such
														// that when user
														// returns to your app,
														// your app is
														// displayed, instead of
														// the emailButtonClicked app.
		try {
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.feedback_error),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void facebook(View v) {
		try {
			this.getPackageManager().getPackageInfo("com.facebook.katana", 0); // Checks
																				// if
																				// FB
																				// is
																				// even
																				// installed.
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("fb://profile/191771744345413")); // Trys to make
																// intent with
																// FB's URI
		} catch (Exception e) {
			intent = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/pages/Corporation-Awesome/191771744345413"));
			// catches and  opens a  url to the desired page
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}

	public void twitter(View v) {
		try {
			// get the Twitter app if possible

			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://twitter.com/corporation_awe"));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		} catch (Exception e) {
			// no Twitter app, revert to browser
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://twitter.com/corporation_awe"));
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);

	}

	public void credits(View v) {
		alert = new AlertDialog.Builder(this);
		alertView = View.inflate(this, R.layout.credits, null);

		alert.setNeutralButton(R.string.dismiss, null);

		alert.setView(alertView);
		title = (TextView) alertView.findViewById(R.id.title);
		tv = (TextView) alertView.findViewById(R.id.textView1);

		if (theme.contains("Onyx")) {
			tv.setTextColor(getResources().getColor(R.color.UIDarkText));
			title.setTextColor(getResources().getColor(R.color.UIDarkText));
		}
								alert.create();
										alert.show();

	}

	public void me(View v) {
		alert = new AlertDialog.Builder(this);


		alertView = View.inflate(this, R.layout.me, null);
		alert.setView(alertView);
		tv = (TextView) alertView.findViewById(R.id.textView1);
		title = (TextView) alertView.findViewById(R.id.title);
		tv.setText(Html.fromHtml(tv.getText().toString()));

		alert.setNeutralButton(R.string.dismiss, null);
		if (theme.contains("Onyx")) {
			tv.setTextColor(getResources().getColor(R.color.UIDarkText));
			title.setTextColor(getResources().getColor(R.color.UIDarkText));
		}




		alert.create();
		alert.show();


	}
	public void history(View v) {
		alert = new AlertDialog.Builder(this);
		alertView = View.inflate(this, R.layout.history, null);
		alert.setView(alertView);


		alert.setNeutralButton(R.string.dismiss, null);

		title = (TextView) alertView.findViewById(R.id.title);
		tv = (TextView) alertView.findViewById(R.id.textView1);

		if (theme.contains("Onyx")) {
			tv.setTextColor(getResources().getColor(R.color.UIDarkText));
			title.setTextColor(getResources().getColor(R.color.UIDarkText));
		}
		alert.create();
		alert.show();
		
		
		
		
	}

}
