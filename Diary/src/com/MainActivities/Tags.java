package com.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.Adapters.TagsAdapter;
import com.rtomyj.Diary.R;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Tags extends MainActivity {
	//lists
	private ArrayList<Integer> alphSortedTagNumArrList = new ArrayList<>();
	private ArrayList<String> alphaSortedTagsArrList = new ArrayList();
	private ArrayList<String> alphaSortedFilenamesArrList = new ArrayList();

	private ArrayList<Integer> numSortedTagNumArrList = new ArrayList<>();
	private ArrayList<String> numSortedTagsArrList = new ArrayList();
	private ArrayList<String> numSortedFilenamesArrList = new ArrayList();

	// views
	private TagsAdapter tagsAdapter;

	// other
	private boolean isAlphaSort = true;


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	protected void onStart() {
		super.onStart();

		if (tagsAdapter == null)
			new LoadTags(this).execute("null");
		else
			tagsAdapter.notifyDataSetChanged();


		/* on click for items in tags
				Intent i = new Intent(c, ChosenTag.class);
				if (isAlphaSort == true)
					i.putExtra("TAG", alphaSortedTagsArrList.get(position));
				else
					i.putExtra("TAG", numSortedTagsArrList.get(position));
				startActivityForResult(i, 2);
		 */


	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			isAlphaSort = savedInstanceState.getBoolean("IS_ALPHA_SORT");
		}

		adapterSize = activityAdapter.getItemCount();
		setupActionBar();
		setInfoToActionBar(TAGS);
		customizeUI();  // changes UI elements
	}



	/*
         * public boolean onPrepareOptionsMenu(Menu dropDownMenuForSelectedFile) { if (isAlphaSort == true)
         * dropDownMenuForSelectedFile.removeItem(R.id.alph_sort); else dropDownMenuForSelectedFile.removeItem(R.id.value_sort);
         * return super.onPrepareOptionsMenu(dropDownMenuForSelectedFile); }
         */

	public synchronized void sort(MenuItem m) {
		if (isAlphaSort == true) {
			isAlphaSort = false;
			m.setTitle(getResources().getString(R.string.alph_sort));
			m.setIcon(getResources().getDrawable(R.drawable.white_sort_alph));
			tagsAdapter.sortNum(numSortedTagsArrList, numSortedTagNumArrList, numSortedFilenamesArrList);
			Log.e("11111111", Integer.toString(alphaSortedFilenamesArrList.size()));
		} else {
			isAlphaSort = true;

			m.setTitle(getResources().getString(R.string.value_sort));
			m.setIcon(getResources().getDrawable(R.drawable.white_sort));
			tagsAdapter.sortAlph(alphaSortedTagsArrList, alphSortedTagNumArrList, alphaSortedFilenamesArrList);
			Log.e("eee", Integer.toString(alphaSortedFilenamesArrList.size()));
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
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


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


			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			File xml = new File(getFilesDir(), "Files.xml");

			if (numSortedTagsArrList.size() == 0) {
				alphaSortedTagsArrList.clear();
				alphSortedTagNumArrList.clear();
				numSortedTagsArrList.clear();
				numSortedTagNumArrList.clear();

				try {
					Document doc;
					Element element;
					Node node;
					NodeList nodeList;
					DocumentBuilder builder = factory.newDocumentBuilder();
					doc = builder.parse(xml);

					nodeList = doc.getElementsByTagName("Files");
					node = nodeList.item(0);
					element = (Element) node;

					nodeList = element.getChildNodes();

				for (int z = 0; z < nodeList.getLength(); z++) {
					node = nodeList.item(z);

					if (node.getNodeName() != "#text") {

						element = (Element) node;

						// if current node isnt for the file in question, it searches through all its tags and adds those that are unique to suggestionsArrList

							String tagsInCurrentNode = ((Element) node).getAttribute("tags");

							for (int j = 0; j < Integer.parseInt(tagsInCurrentNode); j ++)
								if (!unsortedTagsArrList.contains(((Element) node).getAttribute("tag" + Integer.toString(j + 1)))) {
									unsortedTagsArrList.add(((Element) node).getAttribute("tag" + Integer.toString(j + 1)).replaceAll("_", " "));
									unsortedTagNumArrList.add(1);

									fileNames.add(((Element) node).getAttribute("name"));
									Log.e("tag", ((Element) node).getAttribute("tag" + Integer.toString(j + 1)));

								}
						else{
									for (int x = 0; x < unsortedTagsArrList.size(); x++){
										if (unsortedTagsArrList.get(x).equals(((Element) node).getAttribute("tag" + Integer.toString(j + 1)))){

											unsortedTagNumArrList.set(x, unsortedTagNumArrList.get(x) + 1);
										}
									}
								}

					}
				}
			} catch (Exception ex) {

			}

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

				*/

			tagsAdapter = new TagsAdapter(context, alphaSortedTagsArrList, alphSortedTagNumArrList, alphaSortedFilenamesArrList);
			//recyclerView.setAdapter(tagsAdapter);

		}

	}


	@Override
	public boolean onNavigationItemSelected(final MenuItem item) {
		switch(item.getItemId()) {
			case R.id.tags:
				break;
			default:
				super.onNavigationItemSelected(item);
		}
		return true;
	}
}
