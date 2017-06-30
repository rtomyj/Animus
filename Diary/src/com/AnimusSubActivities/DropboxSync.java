package com.AnimusSubActivities;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by CaptainSaveAHoe on 8/1/16.
 */
public class DropboxSync {



    private void dropboxPreLolli(final SwipeRefreshLayout mSwipeRefreshLayout){
		/*
		mSwipeRefreshLayout
		.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(true);

				(new Handler()).postDelayed(new Runnable() {
					@Override
					public void run() {
						if (dropboxSync.hasLinkedAccount() == false)
							dropboxSync.startLink((Activity) c, 0);
						else {
							DbxFile file = null;
							try {
								DbxFileSystem fileSystem = DbxFileSystem
										.forAccount(dropboxSync
												.getLinkedAccount());
								DbxPath check = new DbxPath("/Memories");

								String temp = null;
								String xml = "";
								BufferedReader br;

								if (fileSystem.exists(check) == false)
									fileSystem
											.createFolder(new DbxPath(
													"Memories"));

								check = new DbxPath(
										"/Memories/Tags.txt");


								if (fileSystem.exists(check) == false) {

									file = fileSystem
											.create(new DbxPath(
													"/Memories/Tags.txt"));

									br = new BufferedReader(
											new FileReader(new File(
													getFilesDir(),
													"Tags.xml")));
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

								check = new DbxPath(
										"/Memories/Files.txt");

								if (fileSystem.exists(check) == false) {

									file = fileSystem
											.create(new DbxPath(
													"/Memories/Files.txt"));

									br = new BufferedReader(
											new FileReader(new File(
													getFilesDir(),
													"Files.xml")));
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

								DataInputStream in;

								for (int i = 0; i < sortedFiles.size(); i++) {


									check = new DbxPath("/Memories/"
											+ sortedFiles.get(i));

									if (fileSystem.exists(check) == false) {
										in = new DataInputStream(
												openFileInput(sortedFiles
														.get(i)));

										string_constant = in.readUTF()
												+ in.readUTF();


										file = fileSystem
												.create(new DbxPath(
														"/Memories/"
																+ sortedFiles
																		.get(i)));
										file.writeString(string_constant);
										file.close();


									}
/*
									File pic;
									int num = 1;
									do{
										pic = new File(getFilesDir(), sortedFiles.get(i).replaceAll(".txt", "("+  num + ").png"));
										check = new DbxPath("/Memories/"
												+  sortedFiles.get(i).replaceAll(".txt", "("+  num + ").png"));

										if (fileSystem.exists(check) == false){
											file = fileSystem
													.create(new DbxPath("/Memories/"
															+
															 sortedFiles.get(i).replaceAll(".txt", "("+  num + ").png")));
											file.writeFromExistingFile(pic, false);
											file.close();
											num ++;

											pic = new File(getFilesDir(), sortedFiles.get(i).replaceAll(".txt", "("+  num + ").png"));
										}

									}while(pic.exists());

									pic = null;
									*/
		/*


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

						mSwipeRefreshLayout.setRefreshing(false);

					}
				}, 1000);
			}
		});
						*/
    }
}
