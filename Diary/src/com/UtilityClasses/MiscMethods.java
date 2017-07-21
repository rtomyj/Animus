package com.UtilityClasses;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rtomyj.Diary.R;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class MiscMethods {
	public MiscMethods(){

	}
	public static String setMinute(int minute){
		if (minute == 0)
			return "00";
		else if (minute == 1)
			return "01";
		else if (minute == 2)
			return "02";
		else if (minute == 3)
			return "03";
		else if (minute == 4)
			return "04";
		else if (minute == 5)
			return "05";
		else if (minute == 6)
			return "06";
		else if (minute == 7)
			return "07";
		else if (minute == 8)
			return "08";
		else if (minute == 9)
			return "09";
		else
			return Integer.toString(minute);
	}
	public static String setHour(int hour){
		if (hour == 0)
			hour = 12;
		else if (hour == 13)
			hour = 1;
		else if (hour == 14)
			hour = 2;
		else if (hour == 15)
			hour = 3;
		else if (hour == 16)
			hour = 4;
		else if (hour == 17)
			hour = 5;
		else if (hour == 18)
			hour = 6;
		else if (hour == 19)
			hour = 7;
		else if (hour == 20)
			hour = 8;
		else if (hour == 21)
			hour =9;
		else if (hour == 22)
			hour = 10;
		else if (hour == 23)
			hour = 11;
		return Integer.toString(hour);
	}
	public static String ampm(int hour) {
		if (hour == 0){
			return " AM";
		}
		else
		return " PM";
	}
	public static String randomizedName() {

		Random random = new Random();
		ArrayList<String> letters  = new ArrayList<>();


		WeakReference<Random> rWeak = new WeakReference<>(random);
		WeakReference<ArrayList<String>> lettersWeak = new WeakReference<>(letters);

		lettersWeak.get().add("A");
		lettersWeak.get().add("B");
		lettersWeak.get().add("C");
		lettersWeak.get().add("D");
		lettersWeak.get().add("E");
		lettersWeak.get().add("F");
		lettersWeak.get().add("G");
		lettersWeak.get().add("H");
		lettersWeak.get().add("I");
		lettersWeak.get().add("J");
		lettersWeak.get().add("K");
		lettersWeak.get().add("L");
		lettersWeak.get().add("M");
		lettersWeak.get().add("N");
		String temp = Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
				lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size())) + lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size())) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
				lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size())) + lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size()))
		+ lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size()));
		return temp;
	}
	public static String randomizedExtension() {
		Random random = new Random();
		ArrayList<String>   letters= new ArrayList<>();


		WeakReference<Random> rWeak = new WeakReference<>(random);
		WeakReference<ArrayList<String>> lettersWeak = new WeakReference<>(letters);

		lettersWeak.get().add("A");
		lettersWeak.get().add("B");
		lettersWeak.get().add("C");
		lettersWeak.get().add("D");
		lettersWeak.get().add("E");
		lettersWeak.get().add("F");
		lettersWeak.get().add("G");
		lettersWeak.get().add("H");
		lettersWeak.get().add("I");
		lettersWeak.get().add("J");
		lettersWeak.get().add("K");
		lettersWeak.get().add("L");
		lettersWeak.get().add("M");
		lettersWeak.get().add("N");
		String temp = Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
				lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size())) + lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size())) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
		Integer.toString(rWeak.get().nextInt(10)) + Integer.toString(rWeak.get().nextInt(10)) +
				lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size())) + lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size()))
		+ lettersWeak.get().get(rWeak.get().nextInt(lettersWeak.get().size()));

		return temp;
	}

	public static boolean isNetworkAvailable(Context context ) {

		WeakReference<Context> contextWeak = new WeakReference<>(context);
		boolean outcome = false;

		if (contextWeak.get() != null) {
			ConnectivityManager  cm= (ConnectivityManager) contextWeak.get()
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			WeakReference<ConnectivityManager> cmWeak = new WeakReference<>(cm);

			NetworkInfo[] networkInfos = cmWeak.get().getAllNetworkInfo();
			WeakReference<NetworkInfo[]> networkInfosWeak = new WeakReference<>(networkInfos);

			for (NetworkInfo tempNetworkInfo : networkInfosWeak.get()) {
				if (tempNetworkInfo.isConnected()) {
					outcome = true;
					break;
				}
			}
		}

		return outcome;
	}

	public static String getNumberOfTextFiles(Context c){
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		};

		return Integer
				.toString(c.getFilesDir().listFiles(filter).length);
	}

	public static String getNumberOfNoteFiles(Context c){

		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".note");
			}
		};

		return Integer
				.toString(c.getFilesDir().listFiles(filter).length);
	}


	public static String getNumberOfPicFiles(Context c){
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".png");
			}
		};

		return Integer
				.toString(c.getFilesDir().listFiles(filter).length);
	}
	public static String getMood(int mood, TextView moodTV){
		switch (mood){
			case 0:
				moodTV.setTextSize(14);
				break;
			default:
			moodTV.setTextSize(22);
				break;
		}

		switch (mood){
			case 1:
				return "\uD83D\uDE04";
			case 2:
				return "\uD83D\uDE07";
			case 3:
				return "\uD83D\uDE08";
			case 4:
				return "\uD83D\uDE05";
			case 5:
				return "\uD83D\uDE15";
			case 6:
				return "\uD83D\uDE1F";
			case 7:
				return "\uD83D\uDE34";
			case 8:
				return "\uD83D\uDE31";
			case 9:
				return "\uD83D\uDE35";
			case 10:
				return "\uD83D\uDE0D";
			case 11:
				return "\uD83D\uDE18";
			case 12:
				return "\uD83D\uDE0E";
			case 13:
				return "\uD83D\uDE2D";
			case 14:
				return "\uD83D\uDCA9";
			default:
				return "Mood";
		}

	}


	public static SpannableString getEditedText(String oldString){
		SpannableString newString = new SpannableString(null);



        char htmlCode[] = {'b', 'i', 'u'};
        /*

        for (int i = 0; i < summarySB.length() - 3; i++){
            if (summarySB.charAt(i) == '<'){
                if ((summarySB.charAt(i + 1) == htmlCode[0] || summarySB.charAt(i + 1) == htmlCode[1] || summarySB.charAt(i + 1) == htmlCode[2] ) && summarySB.charAt(i+2) == '>'){
                    summarySB.delete(i, i+3);

                }
                else if (summarySB.charAt(i + 1) == '/' &&
                        (summarySB.charAt(i + 2) == htmlCode[0] || summarySB.charAt(i + 2) == htmlCode[1] || summarySB.charAt(i + 2) == htmlCode[2] )
                        && summarySB.charAt(i + 3) == '>'){
                    summarySB.delete(i, i+4);

                }

            }


        }
        */
		return  newString;

	}


	public static String getLocalizedTime(Calendar calendar){
		// sets time for entry using a string builder as concatenating a string calls the string builders append methods and this implementation should have less calls at bit level.
		StringBuilder timeBuilder = new StringBuilder("");
		switch (Locale.getDefault().getCountry()){
			case "US":  // format is HH:MM AM/PM with HH being 1-12
				timeBuilder.append(MiscMethods.setHour(calendar.get(Calendar.HOUR)));
				timeBuilder.append(":");
				timeBuilder.append(MiscMethods.setMinute(calendar.get(Calendar.MINUTE)) );
				timeBuilder.append(" ");
				timeBuilder.append(MiscMethods.ampm(calendar.get(Calendar.HOUR)));
				return timeBuilder.toString();
			default:     // format is HH:MM with HH being 00-23
				timeBuilder.append(calendar.get(Calendar.HOUR) );
				timeBuilder.append(MiscMethods.setMinute(calendar.get(Calendar.MINUTE)));
				return timeBuilder.toString();
		}
	}


	public static synchronized int countWords(String s){
		int wordCount = 0;

		boolean word = false;
		int endOfLine = s.length() - 1;

		for (int i = 0; i < s.length(); i++) {
			// if the char is a letter, word = true.
			if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
				word = true;
				// if char isn't a letter and there have been letters before, counter goes up.
			} else if (!Character.isLetter(s.charAt(i)) && word) {
				wordCount++;
				word = false;
				// last word of String; if it doesn't end with a non letter, it wouldn't count without this.
			} else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
				wordCount++;
			}
		}
		return wordCount;
	}




	/*
  START MARKUP METHODS
   */
	public static void bold(EditText entryTextTV) {
		if (entryTextTV.hasFocus()) {
			entryTextTV.getText().insert(entryTextTV.getSelectionStart(), "<b>");
			entryTextTV.getText().insert(entryTextTV.getSelectionEnd(), "</b>");
			entryTextTV.setSelection(entryTextTV.getSelectionStart() - 4);
		}

	}

	public static void italic(EditText entryTextTV) {
		if (entryTextTV.hasFocus()) {
			entryTextTV.getText().insert(entryTextTV.getSelectionStart(), "<i>");
			entryTextTV.getText().insert(entryTextTV.getSelectionEnd(), "</i>");
			entryTextTV.setSelection(entryTextTV.getSelectionStart() - 4);
		}

	}

	public static void underline(EditText entryTextTV) {

		if (entryTextTV.hasFocus()) {
			entryTextTV.getText().insert(entryTextTV.getSelectionStart(), "<u>");
			entryTextTV.getText().insert(entryTextTV.getSelectionEnd(), "</u>");
			entryTextTV.setSelection(entryTextTV.getSelectionStart() - 4);
		}

	}

	public static void tab(EditText entryTextTV) {
		if (entryTextTV.hasFocus()) {
			entryTextTV.getText().insert(entryTextTV.getSelectionStart(), "        ");
		}
	}



	public static void email(String subject, String body, String recipient, Context context) {

		Intent mail = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
		mail.setType("text/plain");
		mail.putExtra(Intent.EXTRA_SUBJECT, subject);
		mail.putExtra(Intent.EXTRA_TEXT, body);
		mail.setData(Uri.parse("mailto:" + recipient)); // or just "mailto:" for blank
		mail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // user returns to app when they press back instead of going back in the mail client
		try {
			context.startActivity(mail);
		} catch (Exception e) {
			Toast.makeText(context.getApplicationContext(),
					context.getResources().getString(R.string.feedback_error),
					Toast.LENGTH_LONG).show();
		}
	}


	private class LoadDate extends AsyncTask<String, Integer, String> {
		private TextView month_and_year;
		private Context context;
		private String[] m;

		private File f;
		private java.util.Calendar calendar;

		private String firstVisibleItem;

		public LoadDate(Context c, TextView month_and_year, String[] m,
						String firstVisibleItem) {
			this.month_and_year = month_and_year;
			this.context= c;
			this.m = m;
			this.firstVisibleItem = firstVisibleItem;
		}

		@Override
		protected String doInBackground(String... params) {
			if (isCancelled() == false) {
				f = new File(context.getFilesDir(), firstVisibleItem);
				calendar = java.util.Calendar.getInstance();
				calendar.setTimeInMillis(f.lastModified());
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if ( ! (m[calendar.get(java.util.Calendar.MONTH)] + " " + Integer.toString(calendar.get(java.util.Calendar.YEAR))).equals(month_and_year.getText())){
				month_and_year.setText(m[calendar.get(java.util.Calendar.MONTH)] + " " + Integer.toString(calendar.get(java.util.Calendar.YEAR)));
			}
		}

	}

}
