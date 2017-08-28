package com.EntryActivities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@SuppressLint("CutPasteId")
public class ChosenEntry extends Entry  {
	private File chosenFile;
	private int currentPositionFromDomus;


	private ArrayList<String> sortedFilesArrList = new ArrayList<>();

	private LinearLayout.LayoutParams paramsForTagsOval;

	// other
	private String string_constant;

	@Override
	protected void onStart() {

		super.onStart();


		if (! dataLoaded ) {


			Intent previousActivityIntent = getIntent();
			previousActivityIntent.getExtras().get("FILENAME");
			sortedFilesArrList.addAll(previousActivityIntent.getExtras().getStringArrayList("FILESARRAY"));
			currentPositionFromDomus = previousActivityIntent.getExtras().getInt("POSITION");
			entryMeta.filename = previousActivityIntent.getExtras().getString("FILENAME");

			chosenFile = new File(getFilesDir(), entryMeta.filename);
			entryMeta.filename = entryMeta.filename.replace(".txt", "").trim();

			entryMeta.dateMillis = chosenFile.lastModified();


			try {
				DataInputStream in = new DataInputStream(openFileInput(entryMeta.filename + ".txt"));
				try {
					if (entryMeta.filename.replaceAll(".txt", "").substring(0, 4)
							.equals("Temp") == true) {
						this.setTitle(getResources().getString(R.string.untitled));
					} else
						this.setTitle(entryMeta.filename.replaceAll(".txt", "").replaceAll(
								"_", " "));

				} catch (Exception e) {
					this.setTitle(entryMeta.filename.replaceAll(".txt", ""));
				}
				in = new DataInputStream(openFileInput(entryMeta.filename));


				in.readUTF();
				// text.setText(Html.fromHtml(text.getText().toString() +
				// in.readUTF()));
				string_constant = in.readUTF();

				in.close();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (entryMeta.changedFileName.equals("") != true)
			this.setTitle(entryMeta.changedFileName.replaceAll("_", " "));



	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		setContentView(R.layout.chosen_entry_activity_layout);


		// changes views color to match theme


		//setViewsToTheme();

		paramsForTagsOval = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		paramsForTagsOval.setMargins(0, 0, 30, 0);

		TextView d = (TextView) findViewById(R.id.date);

		LinearLayout tags = (LinearLayout) findViewById(R.id.entryTags);



		if (bundle != null) {

			entryMeta.changedFileName = bundle.getString("NEWNAME");

			string_constant = bundle.getString("STRINGCONSTANT");
			entryMeta.filename = bundle.getString("FILENAME");
			chosenFile = new File(getFilesDir(), entryMeta.filename);
		}
		else  {

		}

/*
		if (Locale.getDefault().getCountry().equals("US"))
			d.setText(Html.fromHtml("<b>" + weekday[lastModified.get(Calendar.DAY_OF_WEEK)]
					+ " " + month[lastModified.get(Calendar.MONTH)] + " "
					+ lastModified.get(Calendar.DAY_OF_MONTH) + ", " + lastModified.get(Calendar.YEAR)
					+ "\t\t" + "</b>"
					+ MiscMethods.setHour(lastModified.get(Calendar.HOUR)) + ":"
					+ MiscMethods.setMinute(lastModified.get(Calendar.MINUTE))
					+ MiscMethods.ampm(lastModified.get(Calendar.HOUR))));
		else
			d.setText(Html.fromHtml("<b>" + weekday[lastModified.get(Calendar.DAY_OF_WEEK)]
					+ " " + lastModified.get(Calendar.DAY_OF_MONTH) + " "
					+ month[lastModified.get(Calendar.MONTH)] + ". " + lastModified.get(Calendar.YEAR)
					+ "\t\t" + "</b>" + lastModified.get(Calendar.HOUR) + ":"
					+ MiscMethods.setMinute(lastModified.get(Calendar.MINUTE))));


*/

	}


	@Override
	protected void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putString("STRINGCONSTANT", string_constant);
	}

/*
	public void swapViews(MenuItem m) throws IOException {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		ViewSwitcher menuVS = (ViewSwitcher) findViewById(R.id.ChangeButtons);

		final ViewSwitcher vs = (ViewSwitcher) findViewById(R.id.ChangeText);

		try {
			menuVS.showNext();
		} catch (Exception e) {

		}
		vs.showNext();
		if (vs.getCurrentView().equals(entryTextTV)) {


			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)

				menuVS.setVisibility(View.INVISIBLE);
			try {
				DataOutputStream dos = new DataOutputStream(openFileOutput(
						filename, Context.MODE_PRIVATE));

				m.setTitle(getResources().getString(R.string.EDIT));
				m.setIcon(R.drawable.white_edit);
				string_constant = entryTextET.getText().toString().trim();
				entryTextTV.setText(Html.fromHtml(string_constant.replace("\n",
						"<br />")));
				entryTextET.setText(null);
				
				
				dos.writeUTF(name + "\n");
				dos.writeUTF(string_constant);

				dos.flush();
				dos.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			chosenFile.setLastModified(lastModified.getTimeInMillis());

			File changeDate;
			int t = 1;
			changeDate = new File(getFilesDir(),
					filename.replaceAll(".txt", "") + "(" + t + ").png");
			while (changeDate.exists() == true) {
				changeDate.setLastModified(lastModified.getTimeInMillis());
				t++;
				changeDate = new File(getFilesDir(), filename.replaceAll(
						".txt", "") + "(" + t + ").png");
			}
			changeDate = new File(getFilesDir(),
					filename.replaceAll(".txt", "") + ".mpeg4");
			if (changeDate.exists() == true) {
				changeDate.setLastModified(lastModified.getTimeInMillis());
			}

			entryTextET.setClickable(false);
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			// nameOfFile.setTextColor(getResources().getColor(
			// R.color.Legit_Purple));
		} else {

			getResources().getConfiguration();
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
menuVS.setVisibility(View.VISIBLE);
			m.setTitle(getResources().getString(R.string.DONE));
			m.setIcon(R.drawable.white_accept);
			entryTextET.setText(null);
			entryTextET.append(string_constant + " ");
			entryTextET.setClickable(true);
			entryTextET.setFocusableInTouchMode(true);
			//entryText.requestFocus();
			final InputMethodManager inputMethodManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.showSoftInput(entryTextET,
					InputMethodManager.SHOW_IMPLICIT);
			// nameOfFile.setTextColor(getResources().getColor(R.color.Black));
		}
	}
	*/
	
	private void deleteEverything() {
		DocumentBuilderFactory factory;
		TransformerFactory tFactory;
		Transformer transformer = null;
		DocumentBuilder builder = null;


		Document doc = null;
		DOMSource source = null;
		StreamResult result = null;



		File f = new File(getFilesDir(), entryMeta.filename);
		f.delete();
		entryMeta.filename = entryMeta.filename.replace(".txt", "");
		f = new File(getFilesDir(), entryMeta.filename + ".mpeg4");
		f.delete();

		int t = 1;
		f = new File(getFilesDir(), entryMeta.filename + "(" + t
				+ ").png");
		while (f.exists() == true) {
			f.delete();
			t++;
			f = new File(getFilesDir(), entryMeta.filename + "(" + t
					+ ").png");
		}


		factory = DocumentBuilderFactory.newInstance();
		File filesXML = new File(getFilesDir(), "Files.xml");
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(filesXML);

			tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer();
			source = new DOMSource(doc);
			result = new StreamResult(filesXML);
		}catch(Exception x){

		}





		Element element;
		Node node;
		NodeList nodeList;

		Node parent = doc.getElementsByTagName("Files").item(0);
		nodeList = parent.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);

			if (node.getNodeName() != "#text") {

				element = (Element) node;
				if (element.getAttribute("name").equals(
						entryMeta.filename.replaceAll(" ", "_") + ".txt")) {
					parent.removeChild(node);
					node = nodeList.item(i - 1);
					if (node.getNodeName() == "#text")
					parent.removeChild(node);

					doc.normalize();
					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}


		finish();

	}

	/*
	@Override
	protected void onPause() {
		super.onPause();


		final LinearLayout tags = (LinearLayout) findViewById(R.id.entryTags);
		final ViewSwitcher vs = (ViewSwitcher) findViewById(R.id.ChangeText);

		if (vs.getCurrentView().equals(entryTextET)) {
			string_constant = entryTextET.getText().toString().trim();
			entryTextTV.setText(Html.fromHtml(string_constant.replace("\n", "<br />")));
		}
		if (shouldISave == true) {
			try {
				DataOutputStream dos = new DataOutputStream(openFileOutput(
						filename, Context.MODE_PRIVATE));
				dos.writeUTF(name + "\n");
				try{
				dos.writeUTF(string_constant.trim());
				}
				catch(NullPointerException e){
					deleteEverything();
				}

				dos.flush();
				dos.close();

				chosenFile.setLastModified(lastModified.getTimeInMillis());
				File changeDate;
				int t = 1;
				changeDate = new File(getFilesDir(), filename.replaceAll(
						".txt", "") + "(" + t + ").png");
				while (changeDate.exists() == true) {
					changeDate.setLastModified(lastModified.getTimeInMillis());
					t++;
					changeDate = new File(getFilesDir(), filename.replaceAll(
							".txt", "") + "(" + t + ").png");
				}
				changeDate = new File(getFilesDir(), filename.replaceAll(
						".txt", "") + ".mpeg4");
				if (changeDate.exists() == true) {
					changeDate.setLastModified(lastModified.getTimeInMillis());
				}

				tags.setClickable(false);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (newFilename.equals("") != true) {
				chosenFile = new File(getFilesDir(), filename);
				if (chosenFile.exists())
					chosenFile.renameTo(new File(getFilesDir(), newFilename + ".txt"));
				chosenFile = new File(getFilesDir(), filename.replaceAll(".txt", "")
						+ ".mpeg4");
				if (chosenFile.exists())
					chosenFile.renameTo(new File(getFilesDir(), newFilename + ".mpeg4"));

				int picNum = 1;
				chosenFile = new File(getFilesDir(), filename.replaceAll(".txt", "")
						+ "(" + picNum + ").png");

				while (chosenFile.exists()) {
					chosenFile.renameTo(new File(getFilesDir(), newFilename + "("
							+ picNum + ").png"));
					picNum++;
					chosenFile = new File(getFilesDir(), filename.replaceAll(".txt", "")
							+ "(" + picNum + ").png");
				}

			}
			tags();

			Intent returnIntent = new Intent();
			this.setResult(RESULT_OK, returnIntent);
			finish();
		}
	}
	*/
/*
	public void tags() {
		TransformerFactory tFactory;

		DocumentBuilderFactory factory;
		DocumentBuilder builder = null;
		Document doc = null;
		DOMSource source = null;
		StreamResult result = null;

		Transformer transformer = null;


		factory = DocumentBuilderFactory.newInstance();
		File filesXML = new File(getFilesDir(), "Files.xml");
			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(filesXML);

				tFactory = TransformerFactory.newInstance();

				transformer = tFactory.newTransformer();
				source = new DOMSource(doc);
				result = new StreamResult(filesXML);




			builder = factory.newDocumentBuilder();
			doc = builder.parse(filesXML);


		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
			catch(TransformerException d){

			}






		Element element;
		Node node;
		NodeList nodeList = null;
		try {
			node = doc.getElementsByTagName("Files").item(0);
			nodeList = node.getChildNodes();
		} catch (Exception e) {

		}

		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);

			if (node.getNodeName() != "#text") {

				element = (Element) node;
				if (element.getAttribute("name").equals(
						filename.replaceAll(" ", "_"))) {

					if (newFilename.equals("") != true) {
						element.setAttribute("name", newFilename + ".txt");
					}

					for (int y = 0; y < allTagsArrList.size(); y++)
						element.setAttribute("tag" + (y + 1), allTagsArrList.get(y)
								.replaceAll(" ", "_"));

					if (allTagsArrList.size() == 0) {
						for (int j = 0; j < tagsLoadedOnStartArrList.size(); j++)
							element.removeAttribute("tag" + (j + 1));
					}
					if (allTagsArrList.size() < tagsLoadedOnStartArrList.size()) {
						for (int j = allTagsArrList.size(); j < tagsLoadedOnStartArrList.size(); j++)
							element.removeAttribute("tag" + (j + 1));
					}

					element.setAttribute("tags",
							Integer.toString((allTagsArrList.size())));
					element.setAttribute("pics",
							Integer.toString(imageCount - 1));
					if (fav.equals("true"))
						element.setAttribute("favoriteSelectedFile", "true");
					else
						element.setAttribute("favoriteSelectedFile", "false");


					CharSequence moods[] = getResources().getStringArray(R.array.moods_arr);
					element.setAttribute("mood", moods[currMood].toString());

					doc.normalize();
					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}






	}



	public void renameFiles(MenuItem v) {
		final AlertDialog.Builder newName = new AlertDialog.Builder(this);
		final EditText renamed = new EditText(this);
		renamed.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_CAP_WORDS);

		renamed.requestFocus();
		newName.setTitle(getResources().getString(R.string.rename_file_dialog));
		newName.setView(renamed);
		if (filename.substring(0, 4).equals("Temp") != true) {
			renamed.setText(filename.replaceAll(".txt", "")
					.replaceAll("_", " "));

			renamed.selectAll();
		}

		newName.setPositiveButton(getResources().getString(R.string.OK),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (renamed
								.getText()
								.toString()
								.trim()
								.equals(filename.replaceAll(".txt", "")
										.replaceAll("_", " ")) != true
								&& renamed.getText().toString().trim()
										.equals("") != true) {
							String ts = renamed.getText().toString().trim()
									.replaceAll("[^\\p{L} \\s 0-9]", "")
									.replaceAll(" ", "_");
							File ft = new File(context.getFilesDir(), ts + ".txt");
							if (ft.exists() == true) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.file_exists_err),
										Toast.LENGTH_LONG).show();
							} else {
								rename(ts.replaceAll("_", " "));
								newFilename = ts.replaceAll(" ", "_");

							}
						}

					}
				});
		newName.setNegativeButton(getResources().getString(R.string.DISMISS),
				null);
		newName.create();
		final AlertDialog alert = newName.create();
		alert.show();
		final String title = this.getTitle().toString();

		renamed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (renamed.getText().toString().trim()
							.replaceAll("[^\\p{L} \\s 0-9]", "").equals(title) != true
							&& renamed.getText().toString().trim().equals("") != true) {

						File ft = new File(context.getFilesDir(), renamed.getText()
								.toString().trim()
								.replaceAll("[^\\p{L} \\s 0-9]", "")
								.replaceAll(" ", "_")
								+ ".txt");
						if (ft.exists() == true) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.file_exists_err)
											+ filename, Toast.LENGTH_LONG)
									.show();
						} else {
							rename(renamed.getText().toString().trim());
							newFilename = renamed.getText().toString().trim()
									.replaceAll("[^\\p{L} \\s 0-9]", "")
									.replaceAll(" ", "_");
							alert.dismiss();
						}
					}
					return true;
				}
				return false;
			}
		});
	}


	private class LoadData extends AsyncTask<String, Integer, String> {
		LinearLayout tags;
		ArrayList<String> temp = new ArrayList<>();
		String latitude = "";
		String longitude = "";


		TransformerFactory tFactory;

		DocumentBuilderFactory factory;
		DocumentBuilder builder = null;
		Document doc = null;


		public LoadData(LinearLayout tags) {
			this.tags = tags;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (fav.equals("true"))
				favTV.setText(Html.fromHtml("★"));



			if (relationship.equals("") && job.equals("")){

				partnerTV.setText(null);
				occupationTV.setText(null);

			}else {
				partnerTV.setText(relationship);
				occupationTV.setText(job);
			}
			for (int y = 0; y < allTagsArrList.size(); y++) {
				TextView tv = new TextView(context);
				tv.setId(amountOfCurrentTags);
				tv.setLayoutParams(paramsForTagsOval);
				tv.setSingleLine(true);
				tv.setTextSize(textSize);
				tv.setTextColor(getResources().getColor(R.color.UILightForeground));

				if (Build.VERSION.SDK_INT > 15)
					tv.setBackground(getResources().getDrawable(
							R.drawable.drop_shadow_purple));
				else
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.drop_shadow_purple));
				tv.setPadding(24, 12, 24, 12);

				if (textSize > 12)
					tv.setTextSize(textSize - 4);
				else

					tv.setTextSize(textSize - 2);

				tv.setText(allTagsArrList.get(y).replaceAll("_", " "));
				tags.addView(tv);

			}
		}

		@Override
		protected String doInBackground(String... params) {

			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			File filesXML = new File(getFilesDir(), "Files.xml");
			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(filesXML);

				tFactory = TransformerFactory.newInstance();

			} catch (SAXException e) {
			} catch (ParserConfigurationException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {

				Element element;
				Node node;
				NodeList nodeList;

				nodeList = doc.getElementsByTagName("Files");
				node = nodeList.item(0);
				nodeList = node.getChildNodes();


					for (int z = 0; z < nodeList.getLength(); z++) {
						node = nodeList.item(z);

						if (node.getNodeName() != "#text") {

							element = (Element) node;

							if (element.getAttribute("name").equals(
									filename.replaceAll(" ", "_"))) {

								relationship = element.getAttribute("partner");
								job = element.getAttribute("occupation");
								fav = element.getAttribute("favoriteSelectedFile");

								String moods[] =  getResources().getStringArray(R.array.moods_arr);



								String mood = element.getAttribute("mood");
								for (int i = 0; i < moods.length; i++){
									if (mood.equals(moods[i])){
										currMood = i;
									}
								}

								amountOfCurrentTags = Integer.parseInt(element
										.getAttribute("tags"));


								for (int y = 0; y < amountOfCurrentTags; y++) {
									tagsLoadedOnStartArrList.add(element.getAttribute("tag"
											+ (y + 1)));
									allTagsArrList.add(element.getAttribute("tag"
											+ (y + 1)));


								}


								try {
									if (element.getAttribute("latitude")
											.equals("") == true) {

										locationName = "NULL";
									} else {
										locationName = element
												.getAttribute("locationName");
										latitude = element.getAttribute(
												"latitude").trim();
										longitude = element.getAttribute(
												"longitude").trim();

									}
								} catch (Exception e4) {

								}

							}

							// if current node isnt for the file in question, it searches through all its tags and adds those that are unique to suggestionsArrList
							else{
								String tagsInCurrentNode = ((Element) node).getAttribute("tags");

								for (int j = 0; j < Integer.parseInt(tagsInCurrentNode); j ++)
									if (!suggestionsArrList.contains(((Element) node).getAttribute("tag" + Integer.toString(j + 1)))) {
										suggestionsArrList.add(((Element) node).getAttribute("tag" + Integer.toString(j + 1)).replaceAll("_", " "));
									}
							}
						}
					}
			} catch (Exception e) {

			}


			return null;
		}

	}
	*/



}