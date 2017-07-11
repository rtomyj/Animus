package com.MainActivities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.Adapters.EntriesAdapter;
import com.SubActivities.ChosenFile;
import com.Settings.MainSettingsFrag;
import com.SubActivities.NewNote;
import com.SubActivities.Passcode;
import com.android.vending.billing.IInAppBillingService;
import com.rtomyj.Diary.R;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// Reusing the entries layout and EntriesAdapter.
public class ChosenTag extends AppCompatActivity {
	private IInAppBillingService mservice = null;
	private ServiceConnection connection;

	private int fileToDelete;

	private DocumentBuilderFactory factory;
	private TransformerFactory tFactory;
	private Transformer transformer;
	private DocumentBuilder builder;
	private Document doc;
	private DOMSource source;
	private StreamResult result;
	private Element element;
	private Node node;
	private NodeList nodeList;
	private File xml;

	private EntriesAdapter adapter;

	// private ArrayList<String> partner = new ArrayList<String>();
	// private ArrayList<String> occupation = new ArrayList<String>();

	private ArrayList<String> tag1 = new ArrayList<>();
	private ArrayList<String> tag2 = new ArrayList<>();
	private ArrayList<String> tag3 = new ArrayList<>();
	private ArrayList<Boolean> fav = new ArrayList<>();

	private ArrayList<String> files = new ArrayList<>();

	private boolean passcodeOn = false;
	private boolean passcodeCheck = true;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				passcodeCheck = false;
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
				passcodeCheck = true;
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		passcodeOn = sp.getBoolean("Password", false);
		if (passcodeOn == true) {
			if (passcodeCheck == true) {
				Intent i = new Intent(this, Passcode.class);
				startActivityForResult(i, 1);
			}
			passcodeCheck = true;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sp.getString("Theme", "Default").equals("Onyx P")) {
			super.setTheme(R.style.OnyxP);
		}
		else if (sp.getString("Theme", "Default").equals("Onyx B")) {
			super.setTheme(R.style.OnyxB);
		}
		else if (sp.getString("Theme", "Default").equals("Material")) {
			super.setTheme(R.style.Material);
		}
		else if (sp.getString("Theme", "Default").equals("Material 2")) {
			super.setTheme(R.style.Material2);
		}
		else if (sp.getString("Theme", "Default").equals("Material 3")) {
			super.setTheme(R.style.Material3);
		}
		else if (sp.getString("Theme", "Default").equals("Material 4")) {
			super.setTheme(R.style.Material4);
		}
		setContentView(R.layout.selected_tag);

		

		final TextView month_and_year = (TextView) findViewById(R.id.month_and_year);
		if (sp.getString("Theme", "Default").contains("Onyx")) {
			LinearLayout l = (LinearLayout) findViewById(R.id.drawer_layout);

			l.setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));
			month_and_year.setBackgroundColor(getResources().getColor(R.color.DarkWhite_ish));
			month_and_year.setTextColor(getResources().getColor(R.color.UIDarkText));
		}
		else if(sp.getString("Theme", "Default").equals("Material")) {
			month_and_year.setBackgroundColor(getResources().getColor(R.color.UIMaterialPink));
		}
		else if(sp.getString("Theme", "Default").equals("Material 2")) {
			month_and_year.setBackgroundColor(getResources().getColor(R.color.UIMaterialBlue));
		}
		else if(sp.getString("Theme", "Default").equals("Material 3")) {
			month_and_year.setBackgroundColor(getResources().getColor(R.color.UIMaterialPurple));
		}
		else if(sp.getString("Theme", "Default").equals("Material 4")) {
			month_and_year.setBackgroundColor(getResources().getColor(R.color.UIMaterialOrange));
		}
		float textSize = Float.parseFloat(sp.getString("TextSize", "14"));

		month_and_year.setTextSize(textSize);

		String fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

		if (fontStyle.contains("DEFAULT") != true) {

			Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/"
					+ fontStyle);

			month_and_year.setTypeface(tf);
		}

		String tag = ((String) getIntent().getExtras().get("TAG"));

		this.setTitle(tag.replaceAll("_", " "));

		final ListView listview = (ListView) findViewById(R.id.selected_tag_list);

		try {
			factory = DocumentBuilderFactory.newInstance();
			xml = new File(getFilesDir(), "Tags.xml");

			builder = factory.newDocumentBuilder();
			doc = builder.parse(xml);

			tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer();
			source = new DOMSource(doc);
			result = new StreamResult(xml);

			nodeList = doc.getElementsByTagName("Tags");
			node = nodeList.item(0);
			nodeList = node.getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);
				if (node.getNodeName() != "#text") {

					if (node.getNodeName().trim().equals(tag.trim()) == true) {
						NodeList newList = node.getChildNodes();
						for (int j = 0; j < newList.getLength(); j++) {
							node = newList.item(j);
							if (node.getNodeName().equals("File")) {
								element = (Element) node;
								files.add(element.getAttribute("name"));

							}
						}
						i = nodeList.getLength();
					}
				}
			}

		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}

		Collections.reverse(files);

		File filesXML = new File(getFilesDir(), "Files.xml");

		factory = DocumentBuilderFactory.newInstance();
		for (int i = 0; i < files.size(); i++) {
			try {
				boolean enteredINXML = false;

				builder = factory.newDocumentBuilder();
				doc = builder.parse(filesXML);

				nodeList = doc.getElementsByTagName("Files");
				node = nodeList.item(0);
				nodeList = node.getChildNodes();

				for (int z = 1; z < nodeList.getLength(); z++) {
					node = nodeList.item(z);
					z++;
					if (node.getNodeName() != "#text") {

						element = (Element) node;
						if (element.getAttribute("name").equals(
								files.get(i).replaceAll(" ", "_"))) {

							// partner.add(element.getAttribute("partner"));
							// occupation.add(element
							// .getAttribute("occupation"));
							z = 100000;
							tag1.add(element.getAttribute("tag1"));
							tag2.add(element.getAttribute("tag2"));
							tag3.add(element.getAttribute("tag3"));
							if (element.getAttribute("favoriteSelectedFile").equals("true"))
								fav.add(true);
							else
								fav.add(false);
							enteredINXML = true;
						}
					}
				}

				// after going through the files.xml file and not seeing the file, checks to see if file exists, if it does then it will creat a node for the file
				if (enteredINXML == false) {
					File check = new File(getFilesDir(), files.get(i).trim()
							.replaceAll(" ", "_"));

					if (check.isFile()) {
						Transformer transformer;
						transformer = TransformerFactory.newInstance()
								.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult(new File(
								getFilesDir(), "Files.xml"));

						Element thisFile = doc.createElement("File");
						thisFile.setAttribute("name", files.get(i).trim()
								.replaceAll(" ", "_"));
						thisFile.setAttribute("pics", Integer.toString(0));
						thisFile.setAttribute("tags", Integer.toString(0));

						thisFile.setAttribute("partner", "");
						thisFile.setAttribute("occupation", "");
						thisFile.setAttribute("favoriteSelectedFile", "false");

						doc.normalizeDocument();
						transformer.transform(source, result);
					}
					//ereases reference in Tags.xml and from files array
					else{
						files.remove(i);


					}

				}
			} catch (Exception e) {
			}
		}
		final String[] weekdays = getResources().getStringArray(
			R.array.abrev_weekdays);
		//adapter = new EntriesAdapter(this, files, tag1, tag2, tag3, fav, weekdays);
		/*
		listview.setAdapter(adapter);
		final Context c = this;
		final String[] m = c.getResources().getStringArray(R.array.months);
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (files.size() > 0) {

					File f = new File(c.getFilesDir(), files
							.get(firstVisibleItem));
					Time t = new Time();
					t.set(f.lastModified());
					month_and_year.setText(m[t.month] + " "
							+ Integer.toString(t.year));
					f = null;
					t = null;
				}
			}

		});
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent("com.diary.chosen_file");
				i.putExtra("FILENAME", files.get(position));
				i.putExtra("FILESARRAY", files);
				i.putExtra("POSITION", position);
				passcodeCheck = false;
				startActivity(i);
			}
		});
		*/
	}
	public void selection(MenuItem z) {
		Intent i = new Intent(this, ChosenFile.class);
		i.putExtra("FILENAME", files.get(fileToDelete));
		i.putExtra("FILESARRAY", files);
		i.putExtra("POSITION", files.indexOf(fileToDelete));
		startActivityForResult(i, 2);
	}

	public void fav(View v) {
		if (((TextView) v).getText().toString().equals("☆")) {
			((TextView) v).setText(Html.fromHtml("★"));

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			File filesXML = new File(getFilesDir(), "Files.xml");

			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(filesXML);

				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(filesXML);

				Node node = doc.getElementsByTagName("Files").item(0);
				NodeList nodeList = node.getChildNodes();

				Element element;
				String filename = files.get(v.getId()).replaceAll(" ", "_");

				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);

					if (node.getNodeName() != "#text") {

						element = (Element) node;
						if (element.getAttribute("name").equals(filename)) {
							element.setAttribute("favoriteSelectedFile", "true");
							fav.set(v.getId(), true);
							adapter.newFavSet(fav);

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
			} catch (Exception x) {

			}

		} else {
			((TextView) v).setText(Html.fromHtml("☆"));

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			File filesXML = new File(getFilesDir(), "Files.xml");

			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(filesXML);

				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(filesXML);

				Node node = doc.getElementsByTagName("Files").item(0);
				NodeList nodeList = node.getChildNodes();

				Element element;
				String filename = files.get(v.getId()).replaceAll(" ", "_");

				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);

					if (node.getNodeName() != "#text") {

						element = (Element) node;
						if (element.getAttribute("name").equals(filename)) {
							element.setAttribute("favoriteSelectedFile", "false");
							fav.set(v.getId(), false);
							adapter.newFavSet(fav);
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
			} catch (Exception x) {

			}

		}
	}

	public void menu(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.long_click_entries, popup.getMenu());

		popup.show();
		String temp = "";
		for (int i = 0; i < files.size(); i++) {
			temp = temp + " " + files.get(i);
		}
		// Toast.makeText(getApplicationContext(), temp,
		// Toast.LENGTH_LONG).show();

		fileToDelete = v.getId();
		// Toast.makeText(getApplicationContext(),
		// sortedFiles.get(fileToDelete),
		// Toast.LENGTH_LONG).show();
	}

	public void delete(MenuItem m) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delelte_dialog_message);
		builder.setIcon(getResources().getDrawable(R.drawable.white_discard));
		builder.setNegativeButton(R.string.no, null);
		builder.setPositiveButton(R.string.delete_confirmation,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						new DeleteEntry().execute("null");
					}
				});
		builder.create();
		builder.show();

	}

	public void newEntry(MenuItem view) {
		Intent i = new Intent(this, NewEntry.class);
		startActivityForResult(i, 2);


		overridePendingTransition(R.anim.scale, R.anim.scale);
	}

	public void newNote(MenuItem view) {
		Intent i = new Intent(this, NewNote.class);
		startActivityForResult(i, 2);
	}

	public void settingsClicked(MenuItem view) {
		Intent i = new Intent(this, MainSettingsFrag.class);
		startActivityForResult(i, 2);
	}

	public void feedback(MenuItem view) {

		Intent mail = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
		mail.setType("text/plain");
		mail.putExtra(Intent.EXTRA_SUBJECT, "");
		mail.putExtra(Intent.EXTRA_TEXT, "");
		mail.setData(Uri.parse("mailto:corporationawesome@gmail.com")); // or
																		// just
																		// "mailto:"
																		// for
																		// blank
		mail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such
														// that when user
														// returns to your app,
														// your app is
														// displayed, instead of
														// the emailButtonClicked app.
		try {
			startActivity(mail);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.feedback_error),
					Toast.LENGTH_LONG).show();
		}
	}

	public void donation(MenuItem view) {
		/*
		
		*/
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = View.inflate(this, R.layout.donation, null);


		TextView title = (TextView) v.findViewById(R.id.title);
		TextView donation = (TextView) v.findViewById(R.id.donation_info);
		TextView inapp = (TextView) v.findViewById(R.id.inapp_info);


		title.setTextColor(getResources().getColor(R.color.UIDarkText));
		donation.setTextColor(getResources().getColor(R.color.UIDarkText));
		inapp.setTextColor(getResources().getColor(R.color.UIDarkText));


		builder.setView(v);
		builder.setNeutralButton(getResources().getString(R.string.dismiss),
				null);
		builder.create();
		builder.show();

	}

	public void baseDonation(View v) {
		donate("base_donation");
	}

	public void secondTier(View v) {
		donate("second_tier");
	}

	public void thirdTier(View v) {
		donate("third_tier");
	}

	public void godTier(View v) {
		donate("god_tier");
	}
	public void removeAds(View v) {
		donate("ad_removal");
	}

	public void donate(String donation) {
		if (mservice != null) {
			ArrayList<String> skuList = new ArrayList<String>();
			skuList.add(donation);
			Bundle querySkus = new Bundle();
			querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
			Bundle skuDetails;
			try {
				skuDetails = mservice.getSkuDetails(3, getPackageName(),
						"inapp", querySkus);

				int response = skuDetails.getInt("RESPONSE_CODE");
				if (response == 0) {

					ArrayList<String> responseList = skuDetails
							.getStringArrayList("DETAILS_LIST");

					for (String thisResponse : responseList) {
						JSONObject object = new JSONObject(thisResponse);
						String sku = object.getString("productId");
						String price = object.getString("price");
						if (sku.equals(donation)) {
							System.out.println("price " + price);
							Bundle buyIntentBundle = mservice.getBuyIntent(3,
									getPackageName(), sku, "inapp",
									"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
							PendingIntent pendingIntent = buyIntentBundle
									.getParcelable("BUY_INTENT");
							startIntentSenderForResult(
									pendingIntent.getIntentSender(), 1001,
									new Intent(), Integer.valueOf(0),
									Integer.valueOf(0), Integer.valueOf(0));
						}
					}
				}
			} catch (Exception e) {

			}
		} else {

			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.donation_err),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		//getMenuInflater().inflate(R.menu.main_ui_menu, menu);

		return true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		connection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				mservice = null;

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mservice = IInAppBillingService.Stub.asInterface(service);
			}
		};

		/*
		bindService(new Intent(
				"com.android.vending.billing.InAppBillingService.BIND"),
				connection, FontSelectionActivity.BIND_AUTO_CREATE);
				
				*/
	}

	private class DeleteEntry extends AsyncTask<String, Integer, String> {

		Element element;
		Node node;
		NodeList nodeList;
		File filesXML = new File(getFilesDir(), "Files.xml");

		DOMSource source;
		StreamResult result;
		Document doc;
		Node parent;

		DocumentBuilder builder;
		Transformer transformer;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			File f = new File(getFilesDir(), files.get(fileToDelete));
			ArrayList<String> tags = new ArrayList<String>();

			f.delete();
			String filename;
			filename = files.get(fileToDelete).replace(".txt", "");
			f = new File(getFilesDir(), filename + ".mpeg4");
			f.delete();

			int t = 1;
			f = new File(getFilesDir(), filename + "(" + t + ").png");
			while (f.exists() == true) {
				f.delete();
				t++;
				f = new File(getFilesDir(), filename + "(" + t + ").png");
			}

			DocumentBuilder builder;
			try {
				builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();

				doc = builder.parse(filesXML);

				TransformerFactory tFactory = TransformerFactory.newInstance();
				transformer = tFactory.newTransformer();
				source = new DOMSource(doc);
				result = new StreamResult(filesXML);

				parent = doc.getElementsByTagName("Files").item(0);
				nodeList = parent.getChildNodes();

				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);

					if (node.getNodeName() != "#text") {

						element = (Element) node;
						if (element.getAttribute("name").equals(
								filename.replaceAll(" ", "_") + ".txt")) {
							for (int y = 0; y < Integer.parseInt(element
									.getAttribute("tags")); y++) {
								tags.add(element.getAttribute("tag" + (y + 1)));

							}
							parent.removeChild(node);
							node = nodeList.item(i - 1);
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

				doc = builder.parse(new File(getFilesDir(), "Tags.xml"));
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParserConfigurationException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (TransformerConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			source = new DOMSource(doc);
			result = new StreamResult(new File(getFilesDir(), "Tags.xml"));

			int loop = 0;
			int tempInt = 0;
			parent = null;
			for (int i = 0; i < tags.size(); i++) {
				NodeList tagList = doc.getElementsByTagName("Tags");
				Node tagNode = tagList.item(0);
				element = (Element) tagNode;

				String temp = tags.get(i);
				temp.toString().replaceAll("\\s", "");

				NodeList searchList = element.getElementsByTagName(temp
						.toString().trim().replaceAll(" ", "_"));

				Node searchNode = searchList.item(0);
				if (searchNode != null) {
					element = (Element) searchNode;
					tempInt = Integer.parseInt(element.getAttribute("amount")
							.trim());
					tempInt--;

					element.setAttribute("amount", Integer.toString(tempInt));

					doc.normalize();

					parent = doc.getElementsByTagName(
							tags.get(i).trim().replaceAll(" ", "_")).item(0);
					nodeList = parent.getChildNodes();

					for (int j = 0; j < nodeList.getLength(); j++) {
						node = nodeList.item(j);

						if (node.getNodeName() != "#text") {

							element = (Element) node;
							if (element.getAttribute("name").equals(
									filename.replaceAll(" ", "_") + ".txt")) {
								parent.removeChild(node);
								node = nodeList.item(j - 1);
								parent.removeChild(node);
								doc.normalize();
								loop++;
								if (loop == tags.size()) {
									j = 1000000;
									i = 1000000;
								}

								try {
									transformer.transform(source, result);
								} catch (TransformerException e) {
									// TODO Auto-generated catch
									// block
									e.printStackTrace();
								}
							}
						}
					}

				}
				doc.normalizeDocument();
				if (tempInt <= 0) {
					element = (Element) doc.getElementsByTagName("Tags")
							.item(0);
					element.removeChild(parent.getPreviousSibling());
					element.removeChild(parent);
					element.setAttribute("amount", Integer.toString(Integer
							.parseInt(element.getAttribute("amount")) - 1));
					for (int a = 0; a < tagList.getLength(); a++) {
						node = tagList.item(a);
						// this is always "Tags"
						// Toast.makeText(getApplicationContext(),
						// node.getNodeName(), Toast.LENGTH_LONG)
						// .show();
						if (node.getNodeName() == "#text") {

							Node anotherNode = tagList.item(a + 1);
							if (anotherNode.getNodeName() == "#text") {
								Element _ele = (Element) anotherNode;
								element.removeChild(_ele);

								doc.normalizeDocument();

								// Toast.makeText(getApplicationContext(),
								// "Jello",
								// Toast.LENGTH_LONG).show();
							}
						}

					}

					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			files.remove(fileToDelete);
			tag1.remove(fileToDelete);
			tag2.remove(fileToDelete);
			tag3.remove(fileToDelete);
			fav.remove(fileToDelete);
			return null;
		}
	}
}
