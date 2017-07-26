package com.MainActivities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import com.Adapters.MainAdapters.TagsAdapter;
import com.MainActivities.BaseClasses.MainActivity;
import com.rtomyj.Diary.R;

import java.util.ArrayList;
import java.util.Arrays;

public class Tags extends MainActivity<TagsAdapter, LinearLayoutManager> {
	// other
	private boolean isAlphaSort = true;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentActivityIdentifier = TAGS;
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			isAlphaSort = savedInstanceState.getBoolean("IS_ALPHA_SORT");

			short initSize = savedInstanceState.getShort("ARR_SIZE"), index = 0;
			Byte[] tagAmountArr = new Byte[initSize];
			for (int element : savedInstanceState.getIntArray("TAG_AMOUNT_ARR")){
				tagAmountArr[index] = (byte) element;
				index++;
			}

			activityAdapter = new TagsAdapter(this, userUIPreferences, savedInstanceState.getStringArrayList("TAGS_ARR_LIST"),
					new ArrayList<>(Arrays.asList(tagAmountArr)), savedInstanceState.getStringArrayList("FILE_NAMES_ARR_LIST"));


		}else{
			activityAdapter = new TagsAdapter(this, userUIPreferences);
		}
		setupViews();
	}


	public synchronized void sort(MenuItem m) {
		if (isAlphaSort == true) {
			isAlphaSort = false;
			m.setTitle(getResources().getString(R.string.SORT_ALPHABETICALLY));
			m.setIcon(getResources().getDrawable(R.drawable.white_sort_alph));
			//tagsAdapter.sortNum(numSortedTagsArrList, numSortedTagNumArrList, numSortedFilenamesArrList);
			//Log.e("11111111", Integer.toString(alphaSortedFilenamesArrList.size()));
		} else {
			isAlphaSort = true;

			m.setTitle(getResources().getString(R.string.SORT_NUMERICALLY));
			m.setIcon(getResources().getDrawable(R.drawable.white_sort));
			//tagsAdapter.sortAlph(alphaSortedTagsArrList, alphSortedTagNumArrList, alphaSortedFilenamesArrList);
			//Log.e("eee", Integer.toString(alphaSortedFilenamesArrList.size()));
		}

	}


	/*
	public class CustomComparator implements Comparator<String> {
		private final Pattern pattern = Pattern.compile("(\\d+)\\s+(.*)");

		@Override
		public int compare(String s1, String s2) {
			Matcher m1 = pattern.matcher(s1);
			if (!m1.matches()) {
				throw new IllegalArgumentException("s1 doesn't match: " + s1);
			}
			Matcher m2 = pattern.matcher(s2);
			if (!m2.matches()) {
				throw new IllegalArgumentException("s2 doesn't match: " + s2);
			}
			int i1 = Integer.parseInt(m1.group(1));
			int i2 = Integer.parseInt(m2.group(1));
			if (i1 < i2) {
				return 1;
			} else if (i1 > i2) {
				return -1;
			}
			return m1.group(2).compareTo(m2.group(2));
		}
	}
	*/

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("IS_ALPHA_SORT", isAlphaSort);
		outState.putStringArrayList("TAGS_ARR_LIST", activityAdapter.getTagsArrList());
		outState.putStringArrayList("FILE_NAMES_ARR_LIST", activityAdapter.getFileNamesArrList());

		ArrayList<Byte> tagAmountByteArrList = new ArrayList<>(activityAdapter.getTagAmountArrList());
		int [] tagAmountIntArr = new int[tagAmountByteArrList.size()];
		short index =0;
		for (Byte tagAmountByte: tagAmountByteArrList){
			tagAmountIntArr[index] = (int) tagAmountByte;
			index++;
		}
		outState.putShort("ARR_SIZE", index);
		outState.putIntArray("TAG_AMOUNT_ARR", tagAmountIntArr);
	}

	/*
	private class LoadTags extends AsyncTask<String, Integer, String> {
		private Context context;
		private ArrayList<String> fileNames = new ArrayList<>();

		private ArrayList<String> unsortedTagsArrList = new ArrayList<>();
		private ArrayList<Integer> unsortedTagNumArrList = new ArrayList();

		public LoadTags(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... params) {




				boolean addedAlph, addedNumerically;


				for (int i = 0; i < unsortedTagsArrList.size(); i ++){

					addedAlph = false;
					addedNumerically = false;
					if (i == 0) {
						alphaSortedTagsArrList.add(unsortedTagsArrList.get(i));
						alphSortedTagNumArrList.add(unsortedTagNumArrList.get(i));
						alphaSortedFilenamesArrList.add(fileNames.get(i));

						numSortedTagsArrList.add(unsortedTagsArrList.get(i));
						numSortedTagNumArrList.add(unsortedTagNumArrList.get(i));
						numSortedFilenamesArrList.add(fileNames.get(i));
					}

					else {


						sorting: for (int j = 0; j < alphaSortedTagsArrList.size(); j++) {

							if (alphaSortedTagsArrList.get(j).compareTo(unsortedTagsArrList.get(i)) > 0) {
								alphaSortedTagsArrList.add(j, unsortedTagsArrList.get(i));
								alphSortedTagNumArrList.add(j, unsortedTagNumArrList.get(i));
								alphaSortedFilenamesArrList.add(j, fileNames.get(i));
								addedAlph = true;
								break sorting;
							}
							if (!addedAlph && j == (alphaSortedTagsArrList.size() -1)){

								alphaSortedTagsArrList.add(unsortedTagsArrList.get(i));
								alphSortedTagNumArrList.add(unsortedTagNumArrList.get(i));
								alphaSortedFilenamesArrList.add(fileNames.get(i));
								break sorting;
							}




						}

						sortingNumerically: for (int j = 0; j < numSortedTagNumArrList.size(); j++) {

							if (numSortedTagNumArrList.get(j) <= unsortedTagNumArrList.get(i)) {
								numSortedTagsArrList.add(j, unsortedTagsArrList.get(i));
								numSortedTagNumArrList.add(j, unsortedTagNumArrList.get(i));

								numSortedFilenamesArrList.add(j, fileNames.get(i));
								addedNumerically = true;
								break sortingNumerically;
							}
							if (!addedNumerically && j == (numSortedTagNumArrList.size() -1)){

								numSortedTagsArrList.add(unsortedTagsArrList.get(i));
								numSortedTagNumArrList.add(unsortedTagNumArrList.get(i));
								numSortedFilenamesArrList.add(fileNames.get(i));
								break sortingNumerically;
							}

						}
					}

				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			/*
			if (isAlphaSort == true)
				tagsAdapter = new TagsAdapter(context, alphaSortedTagsArrList, alphSortedTagNumArrList);
			else
				tagsAdapter = new TagsAdapter(context, numSortedTagsArrList, numSortedTagNumArrList);



			tagsAdapter = new TagsAdapter(context, alphaSortedTagsArrList, alphSortedTagNumArrList, alphaSortedFilenamesArrList);
			//recyclerView.setAdapter(tagsAdapter);

		}

	}
	*/



	@Override
	public boolean onNavigationItemSelected(final MenuItem item) {
		switch(item.getItemId()) {
			case R.id.tags:
				closeNavDrawer();
				break;
			default:
				super.onNavigationItemSelected(item);
		}
		return true;
	}
}
