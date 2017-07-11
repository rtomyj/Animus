package com.MainActivities;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.Adapters.EntriesAdapter;
import com.UtilityClasses.AnimusLauncherMethods;
import com.UtilityClasses.AnimusFiles;
import com.UtilityClasses.AnimusXML;
import com.rtomyj.Diary.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public class Entries extends MainActivity<EntriesAdapter, LinearLayoutManager> {
	// Objects for list
	private ArrayList<String> faveChangedFileNameArrList = new ArrayList<>(10);
	private ArrayList<String> deletedFileNameArrList = new ArrayList<>(10);

	// Misc. Objects
	private Snackbar undoDeleteSnackBar;

	// others...
	private int selectedFile;
	boolean loadJustFaves = false;


	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		// if bundle isn't empty  it means the activity was recreated, it restores all the previous sessions variables from the bundle
		if (bundle != null) {
			// loads data from bundle
			boolean faves[] = bundle.getBooleanArray("FAVES");
			assert faves != null;
			ArrayList<Boolean> favArrayList = new ArrayList<>(Collections.nCopies(faves.length, false));
			WeakReference<ArrayList<Boolean>> favArrayListWeak = new WeakReference<>(favArrayList);

			for (int i = 0; i < faves.length; i++)
				favArrayListWeak.get().set(i, faves[i]);

				/*
				 since all the arrays im getting are from a bundle i figure that using a weak reference here will saveEntryText some memory since entriesAdapter object has a very long life cycle
				 and the bundle arrays will then have a reference to them for a long time
				  */
			WeakReference<ArrayList<String>> sortedFilesArrWeak = new WeakReference<>(bundle.getStringArrayList("SORTEDFILES"));
			WeakReference<ArrayList<String>> tag1ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG1"));
			WeakReference<ArrayList<String>> tag2ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG2"));
			WeakReference<ArrayList<String>> tag3ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG3"));


			// new files adapter object
			activityAdapter = new EntriesAdapter(this, sortedFilesArrWeak.get(), tag1ArrWeak.get(), tag2ArrWeak.get(), tag3ArrWeak.get(), favArrayListWeak.get(),
					userUIPreferences);
		} else {  // if there is nothing to copy from bundle, call method
			activityAdapter = new EntriesAdapter(this, userUIPreferences, loadJustFaves);

		}
		adapterSize = activityAdapter.getItemCount();
		setupActionBar();
		setInfoToActionBar(DOMUS);
		customizeUI();  // changes UI elements
	}

	// Sets the UI elements to specified colors depending on the theme used.
	@Override
	void customizeUI() {
		super.customizeUI();
	}



	private void makeSnackBar(){

		if (undoDeleteSnackBar == null) {
			LinearLayout home = (LinearLayout) findViewById(R.id.parent);
			undoDeleteSnackBar = Snackbar.make(home, getText(R.string.entry_deleted_snackbar_dialog), Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.undo_text), new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					deletedFileNameArrList.remove(deletedFileNameArrList.size() - 1);  // removes last inserted element from array list
					activityAdapter.restorePreviousDeletion(); // calls the restore method from entries adapter to get the info of the delete file.

					adapterSize++;
					actionBar.setSubtitle("Total Entries: " + Integer.toString(adapterSize));

					if (adapterSize == 0) {
						showWelcome();
					} else
						showList();
				}
			});
			((TextView) (undoDeleteSnackBar.getView().findViewById(android.support.design.R.id.snackbar_text))).setTextColor(ContextCompat.getColor(this, R.color.Peach));
		}

	}


	@Override
	protected void onStart() {
		super.onStart();
		makeSnackBar();
		// Creates xml files for tags, faves, etc...
		AnimusXML.checkForAppFiles(getFilesDir(), getAssets(), getBaseContext().getContentResolver());

	}

	/*
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, 1111);
			} catch (IntentSender.SendIntentException e) {
				// Unable to resolve, message user appropriately
			}
		} else {
			GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
		}
		
	}
	*/


	// There is a hamburger style icon on every view in the List Widget. This handles on click calls for such instances.
	public void dropDownMenuForSelectedFile(View threeVerticleDotMenu) {
		Context context = this;
		WeakReference<Context> contextWeak = new WeakReference<>(context);

		PopupMenu popup = new PopupMenu(contextWeak.get(), threeVerticleDotMenu);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.long_click_entries, popup.getMenu());
		popup.show();

		selectedFile = threeVerticleDotMenu.getId();
	}


	// User can favorite an entry right from the List widget.
	/*
		whenever there is a change to the view faveView a new index is added to the array faveChangedFileNameArrList. This index is the corresponding index of the file in the adapter.
		If however, the index is already in the array, then it gets removed. The purpose is that all the changes will be handled at the same time. If there are no changes then that saves on time.
		 */

	public void favoriteSelectedFile(View faveView) {
		String filename = activityAdapter.getFilename(faveView.getId());
		int index = faveChangedFileNameArrList.indexOf(filename), faveViewIndex = faveView.getId();
		if (index != -1)
			faveChangedFileNameArrList.remove(index);
		else
			faveChangedFileNameArrList.add(filename);

		for (String ind : faveChangedFileNameArrList) {
			Log.e("Favorited", ind);
		}


		if (((TextView) faveView).getText().toString().equals("☆")) {
			((TextView) faveView).setText(Html.fromHtml("★"));
			activityAdapter.setFaveStatus(faveViewIndex, true);

		} else {
			((TextView) faveView).setText(Html.fromHtml("☆"));
			activityAdapter.setFaveStatus(faveViewIndex, false);
		}

	}

	public void delete(MenuItem m) {
		// dismisses SnackBar if it is shown to prevent a situation where user can't undo a deletion
		if (undoDeleteSnackBar.isShown())
			undoDeleteSnackBar.dismiss();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		WeakReference<AlertDialog.Builder> builderWeak = new WeakReference<>(builder);

		builderWeak.get().setTitle(R.string.delete_entry_dialog);
		builderWeak.get().setMessage(R.string.delelte_dialog_message);
		builderWeak.get().setIcon(ContextCompat.getDrawable(this, R.drawable.white_discard));
		builderWeak.get().setNegativeButton(R.string.no, null);
		builderWeak.get().setPositiveButton(R.string.delete_confirmation,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						/*
						AnimusFiles.deleteSingleEntry(contextSoft.get(), entriesAdapter.getFilename(selectedFile));
						*/

						undoDeleteSnackBar.show();
						deletedFileNameArrList.add(activityAdapter.getFilename(selectedFile));
						activityAdapter.childRemoved(selectedFile);
						adapterSize--;
						actionBar.setSubtitle("Total Entries: " + Integer.toString(adapterSize));

						if (activityAdapter.getItemCount() == 0) {
							showWelcome();
						} else
							showList();
					}
				});
		builderWeak.get().create();
		builderWeak.get().show();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if ( !faveChangedFileNameArrList.isEmpty()) {
			/*
			this removes the intersection of deletedFileNameArrList and faveChangedFileNameArrList since it is redundant to set
			// fave for a file that iis going to be deleted.
			 */
			faveChangedFileNameArrList.removeAll(deletedFileNameArrList);
			AnimusXML.updateFavesToXML(this.getFilesDir(), faveChangedFileNameArrList); // commit the changes to favorite setting to the xml.
		}
		if ( !deletedFileNameArrList.isEmpty()) {
			for (String index : deletedFileNameArrList) {
				Log.e("Deleted File", index);
			}
			new AnimusFiles.DeleteMultipleEntries(this, deletedFileNameArrList).execute("");
		}
	}


	// when the activity is being terminated it gets the chance to saveEntryText its state. Here it saves the files, the tags for the files, their status (whether they're favorites) etc.
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		boolean faves[] = new boolean[activityAdapter.getItemCount()];

		for (int i = 0; i < activityAdapter.getItemCount(); i++)
			faves[i] = activityAdapter.getFaveAtPosition(i);

		bundle.putStringArrayList("SORTEDFILES", activityAdapter.getSortedFiles());
		bundle.putStringArrayList("TAG1", activityAdapter.getFirstTags());
		bundle.putStringArrayList("TAG2", activityAdapter.getSecondTags());
		bundle.putStringArrayList("TAG3", activityAdapter.getThirdTags());
		bundle.putBooleanArray("FAVES", faves);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);  // needed in order for password screen not be invoked every time user goes back from any other activity

		switch (requestCode) {
			case NEW_ENTRY:
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
				String newFileName = sp.getString("NEWFILENAME", null);

        	/*
        		 potentialNewEntry is a boolean  that only gets set to true when the user clicks on the NewEntry button and  connects to that activity.
         		This if statement will only run if there is a new entry and its purpose is to refresh the Adapter with the new entry.
          	*/
				if (potentialNewEntry && newFileName != null) {
					// adds the new file to the adapter and refreshes it so the contents of the RecyclerView make chronological sense.
					activityAdapter.newEntry(newFileName, sp.getString("NEWFILETAG1", ""), sp.getString("NEWFILETAG2", ""), sp.getString("NEWFILETAG3", ""), sp.getBoolean("NEWFILEFAVE", false));

					adapterSize++;
					actionBar.setSubtitle("Total Entries: " + Integer.toString(adapterSize));

					potentialNewEntry = false;

					recyclerView.scrollToPosition(0);

			/*
				I use a preference to store new file name and their tags on the NewEntry activity if, for some reason, they don't go back to this activity then the onCreate method will pull in new data from
				their directory (new file is in there) and also attempt to read these preferences later in the onStart method. Removing them preemptive wll remove redundancies.
		 	*/
					sp.edit().remove("NEWFILENAME").apply();
					sp.edit().remove("NEWFILETAG1").apply();
					sp.edit().remove("NEWFILETAG2").apply();
					sp.edit().remove("NEWFILETAG3").apply();
				}
				break;
		}
	}


	@Override
	protected void onRestart() {
		super.onRestart();
	}

	public void selection(MenuItem z) {
		AnimusLauncherMethods.chosenFile(this, activityAdapter.getFilename(selectedFile), selectedFile, activityAdapter.getSortedFiles());
	}


	@Override
	public boolean onNavigationItemSelected(final MenuItem item) {
		super.onNavigationItemSelected(item);
		return true;
	}

}
