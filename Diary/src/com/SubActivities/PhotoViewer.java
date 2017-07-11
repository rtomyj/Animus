package com.SubActivities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.rtomyj.Diary.R;

import java.io.File;

public class PhotoViewer extends AppCompatActivity{



	private String photo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.photo_viewer);
		photo = this.getIntent().getStringExtra("PHOTO");


		File file = new File(getFilesDir(), photo);
		ImageView photoView = (ImageView) findViewById(R.id.picture);
		new LoadPic(photoView, file).execute();


}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

	}


	private class LoadPic extends AsyncTask<String, Integer, Bitmap> {
		private ImageView image;
		private File picFile;

		public LoadPic(ImageView p, File picFile) {
			image = p;
			this.picFile = picFile;
		}

		@Override
		protected Bitmap doInBackground(String... file) {
//

			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;

			if (picFile.length() > 2300000 * 1.3){
				BitmapFactory.decodeFile(picFile.getAbsolutePath(), opt);

				opt.inPreferQualityOverSpeed = false;
				int size = 0;
				int height = opt.outHeight, width = opt.outWidth;


				while (height > 1800 && width > 1500){
					height = height /2;
					width = width / 2;

					size +=2;
				}

				opt.inSampleSize = size;
			}
			opt.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeFile(picFile.getAbsolutePath(),
					opt);
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			image.setAdjustViewBounds(true);
			image.setImageBitmap(result);
		}

		@Override
		protected void onPreExecute() {
		}

	}
}