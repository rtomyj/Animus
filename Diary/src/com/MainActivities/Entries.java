package com.MainActivities;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.Adapters.MainAdapters.DomusAdapter;
import com.Adapters.Parents.EntriesBaseAdapter;
import com.Adapters.MainAdapters.ChosenTagAdapter;
import com.Adapters.MainAdapters.FaveAdapter;
import com.MainActivities.BaseClasses.MainActivity;
import com.UtilityClasses.LauncherMethods;
import com.UtilityClasses.Files;
import com.UtilityClasses.XML;
import com.rtomyj.Diary.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public class Entries extends MainActivity<EntriesBaseAdapter, LinearLayoutManager> {
	// Objects for list
	private ArrayList<String> faveChangedFileNameArrList = new ArrayList<>(5);
	private ArrayList<String> deletedFileNameArrList = new ArrayList<>(5);

	// Misc. Objects
	private Snackbar undoDeleteSnackBar;

	// others...
	private int indexOfFocusedFile;
	protected byte adapterInitSize = 0;
	protected String chosenTagName;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		// if bundle isn't empty  it means the activity was recreated, it restores all the previous sessions variables from the bundle
		if (bundle != null) {
			// loads data_activity_layout from bundle
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
			WeakReference<ArrayList<String>> sortedFilesArrWeak = new WeakReference<>(bundle.getStringArrayList("SORTED_FILES"));
			WeakReference<ArrayList<String>> tag1ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG1"));
			WeakReference<ArrayList<String>> tag2ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG2"));
			WeakReference<ArrayList<String>> tag3ArrWeak = new WeakReference<>(bundle.getStringArrayList("TAG3"));


			// uses the previous states info to reinitialize the adapter.
			switch (currentActivityIdentifier){
				case DOMUS:
					activityAdapter = new DomusAdapter(this, sortedFilesArrWeak.get(), tag1ArrWeak.get(), tag2ArrWeak.get(), tag3ArrWeak.get(), favArrayListWeak.get(),
							userUIPreferences);
					break;
				case FAVES:
					activityAdapter = new FaveAdapter(this, sortedFilesArrWeak.get(), tag1ArrWeak.get(), tag2ArrWeak.get(), tag3ArrWeak.get(), favArrayListWeak.get(),
							userUIPreferences);
					break;
				case CHOSEN_TAG:
					activityAdapter = new ChosenTagAdapter(this, sortedFilesArrWeak.get(), tag1ArrWeak.get(), tag2ArrWeak.get(), tag3ArrWeak.get(), favArrayListWeak.get(),
							userUIPreferences);
					break;
			}

		} else { 		 // if there is nothing in the bundle then the class creates a new Adapter object. The Adapter will then load the files necessary from scratch
			switch (currentActivityIdentifier) {
				case DOMUS:
					activityAdapter = new DomusAdapter(this, userUIPreferences);
					break;
				case FAVES:
					activityAdapter = new FaveAdapter(this, userUIPreferences);
					break;
				case CHOSEN_TAG:
					activityAdapter = new ChosenTagAdapter(this, userUIPreferences, chosenTagName, adapterInitSize);
					break;
			}

		}
		super.setupViews();
	}

	private void makeSnackBar(){
		if (undoDeleteSnackBar == null) {
			LinearLayout home = (LinearLayout) findViewById(R.id.parent);
			undoDeleteSnackBar = Snackbar.make(home,
					getText(R.string.ENTRY_DELETED), Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.UNDO),
					new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					deletedFileNameArrList.remove(deletedFileNameArrList.size() - 1);  // removes last inserted element from array list
					activityAdapter.restorePreviousDeletion(); // calls the restore method from entries adapter to get the info of the delete file.

					adapterSize++;
					setActionBarSubTitle("Total Entries: " + Integer.toString(adapterSize));

					switch (adapterSize){
						case 0:
							showWelcome();
							break;
						default:
							showList();
					}
				}
			});
			((TextView) (undoDeleteSnackBar.getView().findViewById(android.support.design.R.id.snackbar_text))).setTextColor(ContextCompat.getColor(this, R.color.UILightForeground));
		}

	}


	@Override
	protected void onStart() {
		super.onStart();
		makeSnackBar();

	}



	// There is a hamburger style icon on every view in the List Widget. This handles on click calls for such instances.
	public void dropDownMenuForSelectedFile(View threeVerticalDotMenu) {
		PopupMenu popup = new PopupMenu(this, threeVerticalDotMenu);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.long_click_entries_list_element, popup.getMenu());
		popup.show();

		indexOfFocusedFile = threeVerticalDotMenu.getId();
	}


	// User can favorite an entry right from the List widget.
	/*
		whenever there is a change to the view faveView a new index is added to the array faveChangedFileNameArrList. This index is the corresponding index of the file in the adapter.
		If however, the index is already in the array, then it gets removed. The purpose is that all the changes will be handled at the same time. If there are no changes then that saves on time.
		 */

	public void favoriteSelectedFile(View faveView) {
		int favePosition = faveView.getId();
		String filename = activityAdapter.getFilename(favePosition);
		boolean isFave = activityAdapter.getFaveAtPosition(favePosition);

		if (faveChangedFileNameArrList.contains(filename)) {
			faveChangedFileNameArrList.remove(filename);
		}
		else
			faveChangedFileNameArrList.add(filename);

		if ( ! isFave)
			activityAdapter.setFaveStatus(favePosition, true);
		else
			activityAdapter.setFaveStatus(favePosition, false);


	}

	public void delete(MenuItem m) {
		// dismisses SnackBar if it is shown to prevent a situation where user can't undo a deletion
		if (undoDeleteSnackBar.isShown())
			undoDeleteSnackBar.dismiss();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.delete_entry_dialog_title);
		builder.setMessage(R.string.delete_entry_dialog_message_);
		builder.setIcon(ContextCompat.getDrawable(this, R.drawable.white_discard));
		builder.setNegativeButton(R.string.NO, null);
		builder.setPositiveButton(R.string.DELETE,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						undoDeleteSnackBar.show();
						deletedFileNameArrList.add(activityAdapter.getFilename(indexOfFocusedFile));
						activityAdapter.childRemoved(indexOfFocusedFile);
						adapterSize--;
						setActionBarSubTitle("Total Entries: " + Integer.toString(adapterSize));

						if (activityAdapter.getItemCount() == 0) {
							showWelcome();
						} else
							showList();
					}
				});
		builder.create();
		builder.show();

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
			XML.updateArrayOfFavesToXML(this.getFilesDir(), faveChangedFileNameArrList); // commit the changes to favorite setting to the xml.
		}
		if ( !deletedFileNameArrList.isEmpty()) {
			for (String index : deletedFileNameArrList) {
				Log.e("Deleted File", index);
			}
			Files.deleteMultipleEntries(this, deletedFileNameArrList);
		}
	}


	// when the activity is being terminated it gets the chance to saveEntryText its state. Here it saves the files, the tags for the files, their status (whether they're favorites) etc.
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		boolean faves[] = new boolean[activityAdapter.getItemCount()];

		for (int i = 0; i < activityAdapter.getItemCount(); i++)
			faves[i] = activityAdapter.getFaveAtPosition(i);

		bundle.putStringArrayList("SORTED_FILES", activityAdapter.getSortedFiles());
		bundle.putStringArrayList("TAG1", activityAdapter.getFirstTags());
		bundle.putStringArrayList("TAG2", activityAdapter.getSecondTags());
		bundle.putStringArrayList("TAG3", activityAdapter.getThirdTags());
		bundle.putBooleanArray("FAVES", faves);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
			case NEW_ENTRY:
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
				boolean isNewEntryFave = sp.getBoolean("NEWFILEFAVE", false);

				if (currentActivityIdentifier == FAVES &&  ! isNewEntryFave)
					break;

				String newFileName = sp.getString("NEWFILENAME", null);

				if (newFileName != null) {
					// adds the new file to the adapter and refreshes it so the contents of the RecyclerView make chronological sense.
					activityAdapter.newEntry(newFileName, sp.getString("NEWFILETAG1", ""), sp.getString("NEWFILETAG2", ""), sp.getString("NEWFILETAG3", ""), isNewEntryFave);

					adapterSize++;
					setActionBarSubTitle("Total Entries: " + Integer.toString(adapterSize));
					scrollToX(0);

			/*
				I use a preference to store new file name and their tags on the NewEntry activity if, for some reason, they don't go back to this activity then the onCreate method will pull in new data_activity_layout from
				their directory (new file is in there) and also attempt to read these preferences later in the onStart method. Removing them preemptive wll remove redundancies.
		 	*/
					sp.edit().remove("NEWFILENAME").apply();
					sp.edit().remove("NEWFILETAG1").apply();
					sp.edit().remove("NEWFILETAG2").apply();
					sp.edit().remove("NEWFILETAG3").apply();
					sp.edit().remove("NEWFILEFAVE").apply();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);  // needed in order for password screen not be invoked every time user goes back from any other activity
				break;
		}
	}

	public void selection(MenuItem z) {
		LauncherMethods.chosenFile(this, activityAdapter.getFilename(indexOfFocusedFile), indexOfFocusedFile, activityAdapter.getSortedFiles());
	}


}
