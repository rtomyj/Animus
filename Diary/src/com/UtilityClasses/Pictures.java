package com.UtilityClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rtomyj.Diary.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CaptainSaveAHoe on 6/8/17.
 */

public class Pictures {

    public static ImageView getImageView(Context context, int imageCount) {
        ImageView imageView = new ImageView(context);

        imageView.setAdjustViewBounds(true);
        imageView.setPadding(0, 0, 5, 0);
        imageView.setId(imageCount);
        return imageView;
    }




    // sets image specified by loadPics variable to an ImageView
    public static void setImageToView(File loadPics,  final int imageNum, ImageView imageView, final LinearLayout holder){

        if (imageNum == 1) {       // if the image being put to UI is the first one to be processed, it gets put in the main ImageView
            new LoadPic(loadPics, imageView).execute("null");
            imageView.setClickable(true);
        }
        else{       // otherwise it gets added to the linearlayout holding pics
            new LoadPic(loadPics, imageView).execute("null");
            holder.addView(imageView);
        }
    }



    static private class LoadPic extends AsyncTask<String, Integer, Bitmap> {
        File picFile;
        ImageView imageView;

        LoadPic(File picFile, ImageView imageView) {
            this.picFile = picFile;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... file) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;

            if (picFile.length() > 2300000 * 1.3){
                BitmapFactory.decodeFile(picFile.getAbsolutePath(), opt);

                opt.inPreferQualityOverSpeed = false;
                int size = 0;
                int height = opt.outHeight, width = opt.outWidth;

                while (height > 1200 && width > 800){
                    height = height /2;
                    width = width / 2;
                    size +=2;
                }

                opt.inSampleSize = size;
            }
            opt.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(picFile.getAbsolutePath(), opt);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        @Override
        protected void onPreExecute() {
        }

    }


    public static int deleteOnePicFromEntry(View v, LinearLayout layoutHoldingPics, Context context, String filename, int imageCount){
        // deletes currently selected picture, reduces the imageCount and renames all other picture files to coincide to new order

        if (layoutHoldingPics != null)
             layoutHoldingPics.removeView(v);

        File picDeletion = new File(context.getFilesDir(), filename + "(" + v.getId() + ").png");
        File renamePic;
        int temp = v.getId();
        picDeletion.delete();
        if (temp < imageCount) {
            for (int i = temp; i < imageCount; i++) {
                renamePic = new File(context.getFilesDir(), filename + "(" + (i + 1) + ").png");
                renamePic.renameTo(new File(context.getFilesDir(), filename + "(" + (i) + ").png"));
            }
        }
       return --imageCount;

    }



    public static File saveImage(Uri pictureURI, Context context, String filename, int imageNum) throws IOException {
        InputStream imageStream = context.getContentResolver().openInputStream(pictureURI);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

        File picFile = new File(context.getFilesDir(), filename + "(" + (imageNum) + ")" + ".png");
        picFile.createNewFile();

        //Bitmap bitmap = Bitmap.createBitmap(android.provider.MediaStore.Images.Media.getBitmap(contentResolver, pictureURI));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        FileOutputStream imageOutput = new FileOutputStream(picFile);
        imageOutput.write(bytes.toByteArray());
        imageOutput.close();

        bitmap.recycle();

        return picFile;
    }


    public static class LRUBitmapCache extends LruCache<String, Bitmap> {

        public LRUBitmapCache(int maxSize) {
            super(maxSize);
        }

        public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null) {
                this.put(key, bitmap);
            }
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();

        }

        public Bitmap getBitmapFromMemCache(String key) {
            return this.get(key);
        }


    }



}
