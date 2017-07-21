package com.UtilityClasses;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rtomyj.Diary.R;

import java.lang.ref.WeakReference;

/**
 * Created by CaptainSaveAHoe on 6/7/17.
 */

public class Permissions {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 900;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 901;
    public static final int MY_PERMISSIONS_REQUEST_CONTACTS = 902;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_TO_DISK = 903;
    public static final int MY_PERMISSIONS_REQUEST_MIC = 904;



    public static void requestDialog(final int request, final Context context){
        AlertDialog.Builder permissionDialog = new AlertDialog.Builder(context);
        WeakReference<AlertDialog.Builder> permissionDialogWeak = new WeakReference<>(permissionDialog);

        View requestView = View.inflate(context, R.layout.request_sheet, null);
        TextView requestTitleTV = (TextView) requestView.findViewById(R.id.request_title);
        TextView requestExplanationTV = (TextView) requestView.findViewById(R.id.request_explanation);

        WeakReference<TextView> requestTitleTVWeak = new WeakReference<>(requestTitleTV);
        WeakReference<TextView> requestExplanationTVWeak = new WeakReference<>(requestExplanationTV);

        switch (request){
            case MY_PERMISSIONS_REQUEST_CAMERA:
                requestTitleTVWeak.get().setText("Camera Requested");
                requestExplanationTVWeak.get().setText("In order for application to use camera, permission must be granted. Camera is used to take pictures on user approval.");
                break;
            case MY_PERMISSIONS_REQUEST_CONTACTS:
                requestTitleTVWeak.get().setText("Contacts Requested");
                requestExplanationTVWeak.get().setText("In order for application to access contacts, permission must be granted. Contacts are used to suggest tags while typing.");
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION:
                requestTitleTVWeak.get().setText("Location Requested");
                requestExplanationTVWeak.get().setText("In order for application ping location, permission must be granted. Location is used when new entry is made.");
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_TO_DISK:
                requestTitleTVWeak.get().setText("Write To Disk Requested");
                requestExplanationTVWeak.get().setText("In order for application to take pictures writing to disk is needed. This will saveEntryText the photos you take here in your phone.");
                break;
            case MY_PERMISSIONS_REQUEST_MIC:
                requestTitleTVWeak.get().setText("Recording Audio Requested");
                requestExplanationTVWeak.get().setText("In order for application to take record audio permission is required..");
                break;


        }

        permissionDialogWeak.get().setView(requestView);
        permissionDialogWeak.get().setNegativeButton(R.string.CANCEL, null);
        permissionDialogWeak.get().setNeutralButton(context.getString(R.string.dismiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestResult(request, (Activity) context);
            }
        });
        permissionDialogWeak.get().setCancelable(false);
        permissionDialogWeak.get().show();
    }



    private static void requestResult(int request, Activity context){
        switch (request) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                break;
            case MY_PERMISSIONS_REQUEST_CONTACTS:
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_CONTACTS);
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION:
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_TO_DISK:
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_TO_DISK);
                break;
            case MY_PERMISSIONS_REQUEST_MIC:
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_MIC);
                break;

        }
    }


    public static void permissionNotGranted(Context context, int PERMISSION_CODE){
        Toast toast = null;

        switch (PERMISSION_CODE){
            case MY_PERMISSIONS_REQUEST_CAMERA:
                toast = Toast.makeText(context, "Permission needed to use camera.", Toast.LENGTH_SHORT);
                break;
            case MY_PERMISSIONS_REQUEST_CONTACTS:
                toast = Toast.makeText(context, "Permission needed to access contacts. ", Toast.LENGTH_SHORT);
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION:
                toast = Toast.makeText(context, "Permission needed to ping location.", Toast.LENGTH_SHORT);
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_TO_DISK:
                toast = Toast.makeText(context, "Permission needed to save photos.", Toast.LENGTH_SHORT);
                break;
            case MY_PERMISSIONS_REQUEST_MIC:
                toast = Toast.makeText(context, "Permission needed to access mic.", Toast.LENGTH_SHORT);
                break;
        }
        toast.setGravity(Gravity.TOP, 0, 0);

        toast.show();
    }



}
