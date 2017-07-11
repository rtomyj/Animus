package Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.UtilityClasses.AnimusFiles;
import com.UtilityClasses.AnimusUI;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.rtomyj.Diary.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Data extends AppCompatActivity {

	final static private String APP_KEY = "aex1czbns7sva3p";
	final static private String APP_SECRET = "yah1emwm7tbs29g";


	private DropboxAPI<AndroidAuthSession> mDBApi;

	private Switch sdSwitch;
	private Switch dropboxSwitch;
	private ProgressBar syncPB;
	private Context context;
	private ArrayList<String> filesArrList = new ArrayList<String>();


	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

		context = this;
		String theme = sp.getString("Theme", "Default");
		AnimusUI.setTheme(context, theme);
			
		setContentView(R.layout.data);

		Toolbar actionbar = (Toolbar) findViewById(R.id.toolbar);
		WeakReference<Toolbar> actionbarWeak = new WeakReference<>(actionbar);
		setSupportActionBar(actionbarWeak.get());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		TextView backupRestoreTV = (TextView) findViewById(R.id.textView2);

		if(sp.getString("Theme", "Default").equals("Material 2")){
			backupRestoreTV.setBackgroundColor(this.getResources().getColor(R.color.UIMaterialBlue));
		}
		
		else if(sp.getString("Theme", "Default").equals("Material 3")){
			backupRestoreTV.setBackgroundColor(this.getResources().getColor(R.color.UIMaterialPurple));
		}
		else if(sp.getString("Theme", "Default").equals("Material")){
			backupRestoreTV.setBackgroundColor(this.getResources().getColor(R.color.UIMaterialPink));
		}
		
		else if(sp.getString("Theme", "Default").equals("Material 4")){
			backupRestoreTV.setBackgroundColor(this.getResources().getColor(R.color.UIMaterialOrange));
		}
		else if(sp.getString("Theme", "Default").contains("Onyx")){
			TextView backupRestoreInfo = (TextView) findViewById(R.id.textView1);
			backupRestoreTV.setBackgroundColor(this.getResources().getColor(R.color.DarkWhite_ish));
			backupRestoreTV.setTextColor(this.getResources().getColor(R.color.UIDarkText));
			backupRestoreInfo.setTextColor(getResources().getColor(R.color.UIDarkText));
			
			LinearLayout parent = (LinearLayout) findViewById(R.id.parent);

			CardView subParentCV = (CardView) findViewById(R.id.subParent);
			CardView fileConsolidationCV = (CardView) findViewById(R.id.subParent);

			subParentCV.setRadius(2);
			fileConsolidationCV.setRadius(2);
			
			//parentSV.setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));
			

			TextView child2 = (TextView) findViewById(R.id.child2);
			TextView consolidateExplanationTV = (TextView) findViewById(R.id.consolidate_explanation);

			parent.setBackgroundColor(getResources().getColor(R.color.UIDarkOnyx));

			subParentCV.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));
			fileConsolidationCV.setBackgroundColor(getResources().getColor(R.color.UIDarkGray));

			child2.setTextColor(getResources().getColor(R.color.UIDarkText));
			consolidateExplanationTV.setTextColor(getResources().getColor(R.color.UIDarkText));
		}


	}



	@Override
	protected void onStart() {
		super.onStart();


		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

// And later in some initialization function:
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		//sdSwitch = (Switch) findViewById(R.id.sd_backup);
		dropboxSwitch = (Switch) findViewById(R.id.dropbox_backup);
		syncPB = (ProgressBar) findViewById(R.id.progressBar1);
		if (sp.getBoolean("DROPBOXBACKUP", false) == true) {
			dropboxSwitch.setChecked(true);
		}
		if (sp.getBoolean("SDBACKUP", false) == true) {
			sdSwitch.setChecked(true);
		}
	}

	public void sdBackup(View v) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sdSwitch.isChecked() == true) {

			if (getExternalSdCardPath().equals("null")) {
				sdSwitch.setChecked(false);
				Toast.makeText(getApplicationContext(),
						this.getResources().getString(R.string.no_sd),
						Toast.LENGTH_LONG).show();

				if (sdSwitch.isChecked() == true) {
					sp.edit().putBoolean("SDBACKUP", true).commit();
				}
			}
		}

	}

	public void dropboxBackup(View v) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (dropboxSwitch.isChecked() == true) {
			sp.edit().putBoolean("DROPBOXBACKUP", true).commit();
			
		}
		else{
			Toast.makeText(getApplicationContext(),
					this.getResources().getString(R.string.dbx_disconnect), Toast.LENGTH_LONG).show();
			sp.edit().putBoolean("DROPBOXBACKUP", false).commit();
			//dbxAccountManager.unlink();
		}
	}

	public void backup(View v) {
		if (dropboxSwitch.isChecked() == true) {
			syncPB.setVisibility(View.VISIBLE);
			syncPB.setProgress(1);

			new LoadList(filesArrList, context).execute("null");

		}
	}

	public static String getExternalSdCardPath() {
		String path = null;

		File sdCardFile = null;
		List<String> sdCardPossiblePath = Arrays.asList("external_sd",
				"ext_sd", "external", "extSdCard");

		for (String sdPath : sdCardPossiblePath) {
			File file = new File("/mnt/", sdPath);

			if (file.isDirectory() && file.canWrite()) {
				path = file.getAbsolutePath();
			}
		}

		if (path != null) {
			sdCardFile = new File(path);

			return sdCardFile.getAbsolutePath();
		} else {
			sdCardFile = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			return "null";
		}

	}

	private class LoadList extends AsyncTask<String, Integer, String> {

		private File[] temp;
		private Data data;

		public LoadList(ArrayList<String> files, Context c) {
			this.data = (Data) c;
		}

		@Override
		protected String doInBackground(String... params) {
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".txt")) {
						return true;
					}
					File f = new File(dir.getAbsolutePath() + "/" + filename);
					return f.isDirectory();
				}

			};
			temp = data.getFilesDir().listFiles(filter);
			for (int i = 0; i < temp.length; i++) {
				filesArrList.add(temp[i].getName());
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			syncPB.setProgress(45);


			/*
			(new Handler()).postDelayed(new Runnable() {
				@Override
				public void run() {
					if (dbxAccountManager.hasLinkedAccount() == false){
						dbxAccountManager.startLink((Activity) data, 0);
						syncPB.setProgress(0);
					}
					else {
						DbxFile file = null;
						try {
							DbxFileSystem fileSystem = DbxFileSystem
									.forAccount(dbxAccountManager.getLinkedAccount());
							DbxPath check = new DbxPath("/Memories");

							String temp = null;
							String xml = "";
							BufferedReader br;

							if (fileSystem.exists(check) == false)
								fileSystem
										.createFolder(new DbxPath("Memories"));

							check = new DbxPath("/Memories/Tags.txt");

							if (fileSystem.exists(check) == false) {

								file = fileSystem.create(new DbxPath(
										"/Memories/Tags.txt"));

								br = new BufferedReader(new FileReader(
										new File(getFilesDir(), "Tags.xml")));
								temp = "temp";
								while (temp != null) {

									temp = br.readLine();
									if (temp != null)
										xml = xml + temp + "\n";
								}

								file.writeString(xml);
								file.close();
							}

							temp = null;
							xml = "";

							check = new DbxPath("/Memories/Files.txt");

							if (fileSystem.exists(check) == false) {

								file = fileSystem.create(new DbxPath(
										"/Memories/Files.txt"));

								br = new BufferedReader(new FileReader(
										new File(getFilesDir(), "Files.xml")));
								temp = "temp";
								while (temp != null) {

									temp = br.readLine();
									if (temp != null)
										xml = xml + temp + "\n";
								}

								file.writeString(xml);
								file.close();
							}

							String string_constant;

							for (int i = 0; i < filesArrList.size(); i++) {
								DataInputStream in = new DataInputStream(
										openFileInput(filesArrList.get(i)));

								// text.setText(Html.fromHtml(text.getText().toString()
								// +
								// in.readUTF()));
								string_constant = in.readUTF() + in.readUTF();

								check = new DbxPath("/Memories/" + filesArrList.get(i));

								if (fileSystem.exists(check) == false) {
									file = fileSystem.create(new DbxPath(
											"/Memories/" + filesArrList.get(i)));
									file.writeString(string_constant);
									file.close();
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								file.close();
							} catch (Exception x) {

							}
					}

				}
				}
			}, 1000);
			syncPB.setProgress(100);



		*/
		}

	}

    public void consolidate(View v){
		// deletes files with "(X).txt" file name (no filename just an appended digit)
		ArrayList<File> filesArrayList = new ArrayList<>(AnimusFiles.getFilesWithExtension(context.getFilesDir(), ".txt"));
		ArrayList<File> picArrayList = new ArrayList<>(AnimusFiles.getFilesWithExtension(context.getFilesDir(), ".png"));
		ArrayList<File> audioArrayList = new ArrayList<>(AnimusFiles.getFilesWithExtension(context.getFilesDir(), ".mpeg"));

		AnimusFiles.deleteFilesWithNoName(filesArrayList, ".txt");
		AnimusFiles.deleteFilesWithNoName(picArrayList, ".png");
		AnimusFiles.deleteFilesWithNoName(audioArrayList, ".mpeg");


        DocumentBuilderFactory factory;
        TransformerFactory tFactory;
        DocumentBuilder builder;
        Document doc;

        factory = DocumentBuilderFactory.newInstance();
        File filesXML = new File(getFilesDir(), "Files.xml");



        DOMSource source;
        StreamResult result;


        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(filesXML);



            source = new DOMSource(doc);
            result = new StreamResult(filesXML);

            tFactory = TransformerFactory.newInstance();


            Transformer transformer = tFactory.newTransformer();

            Element element;
            Node node;
            NodeList nodeList;

            Node parent = doc.getElementsByTagName("Files").item(0);
            nodeList = parent.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                node = nodeList.item(i);

                if ( !node.getNodeName().equals("#text")) {
					element = (Element) node;

					// checks the attribute for name. If there isnt a filename with "name".txt then the node gets removed
					String filename = element.getAttribute("name");
					File file = new File(context.getFilesDir(), filename);
					if (!file.exists()) {
						Log.e("Does not exist", filename);
						parent.removeChild(node);
						node = nodeList.item(i - 1);
						if (node.getNodeName() == "#text")
							parent.removeChild(node);
						node = null;
					}


					if (node != null) {     //  if node was removed from xml in previous lines then node is null and we can't work on node since it was removed
						// counts the number of tags that an entry has and writes that number in the xml attribute, "tags"

						int x = 0;
						boolean exit = false;

						while (!exit) {
							if (element.getAttribute("tag" + Integer.toString(x + 1)).equals(""))
								exit = true;
							else
								x++;
						}
						element.setAttribute("tags", Integer.toString(x));

					}
				}
            }
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}










        } catch (SAXException e) {
        } catch (ParserConfigurationException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (TransformerException trans){

        }

    }
}
