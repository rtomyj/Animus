package com.EntryActivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.UtilityClasses.LauncherMethods;
import com.UtilityClasses.Permissions;
import com.UtilityClasses.Pictures;
import com.rtomyj.Diary.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by MacMini on 8/25/17.
 */

public class Entry_Pictures extends Entry_Audio {

    final int USE_CAMERA_CONST = 100;
    final int PICK_PHOTO_CONST = 101;

    Uri picFromCameraURI;


    public void loadPicsFromLastInstance(){
        int loadCount = 1;      // loops through all possible names for image file. Each photo file has a num X were X is the order in which photo was recorded.
        File loadPics = new File(getFilesDir(), entryMeta.filename + "(" + loadCount + ").png");      // gets photo with num X = loadCount
        while (loadPics.exists()) {
            makeImageViews(entryMeta.numOfPhotos, loadPics);

            loadCount++;
            loadPics = new File(getFilesDir(), entryMeta.filename + "(" + loadCount + ").png");
            entryMeta.numOfPhotos ++;      // image count = number of pics loaded.
        }
    }


    public void takePic(){
        onPauseNotTakingPic = false;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        picFromCameraURI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png"));     // this stores the image to device storage
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picFromCameraURI);
        startActivityForResult(intent, USE_CAMERA_CONST);
    }


    public void makeImageViews(int imageNum, File loadPics){
        ImageView imageView;
        if(imageNum == 1) {
            imageView = (ImageView) findViewById(R.id.main_pic);
            Pictures.setImageToView(loadPics, imageNum, imageView, null);
        }else {
            LinearLayout layoutHoldingPics = (LinearLayout) findViewById(R.id.layoutWithP);
            imageView = Pictures.getImageView(context, imageNum);

            Pictures.setImageToView(loadPics, imageNum, imageView, layoutHoldingPics);
            setListenersToImageViews(imageView, imageNum, layoutHoldingPics);
        }

    }


    private void setListenersToImageViews(ImageView imageView, final int imageNum, final LinearLayout holder){

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).startActivityForResult(LauncherMethods.launchPhotoViewer(v, context, entryMeta.filename), 2);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                entryMeta.numOfPhotos = Pictures.deleteOnePicFromEntry(v, holder, context, entryMeta.filename, imageNum);
                return false;
            }
        });

    }

    // when the user clicks on the button to add an image this method runs and shows a menu for the user to select either camera or photo picker.
    public void addPic(View m) {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        PopupMenu popup = new PopupMenu(this, m);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.pic_button_popup, popup.getMenu());
        popup.show();
    }



    public void useCamera(MenuItem m) throws IOException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Permissions.requestDialog(Permissions.MY_PERMISSIONS_REQUEST_CAMERA, context);
            }
            else{
                Permissions.permissionNotGranted(context, Permissions.MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {     // if the user has granted permission to access camera app seeks permission to write data_activity_layout to disk
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Permissions. requestDialog(Permissions.MY_PERMISSIONS_REQUEST_WRITE_TO_DISK, context);
                }else{
                    Permissions.permissionNotGranted(context, Permissions.MY_PERMISSIONS_REQUEST_WRITE_TO_DISK);
                }
            }
            else {          // if both the camera and the write disk permissions are set to ok, then the system goes to the camera UI
                takePic();
            }
        }

    }


    public void choosePhoto(MenuItem m) {
        onPauseNotTakingPic = false;
        Intent choosePic = new Intent(Intent.ACTION_PICK);
        choosePic.setType("image/*");
        startActivityForResult(choosePic, PICK_PHOTO_CONST);

    }
}
