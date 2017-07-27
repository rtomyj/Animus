package com.EntryActivities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.MainActivities.BaseClasses.Activity_Base;
import com.SubActivities.Passcode;
import com.UtilityClasses.Files;
import com.UtilityClasses.LauncherMethods;
import com.UtilityClasses.MiscMethods;
import com.UtilityClasses.Permissions;
import com.UtilityClasses.Pictures;
import com.UtilityClasses.Tags;
import com.UtilityClasses.UI;
import com.UtilityClasses.Audio;
import com.UtilityClasses.XML;
import com.UtilityClasses.Entry;
import com.rtomyj.Diary.R;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewEntry extends Activity_Base implements LocationListener, Entry {
    // Activity Vars
    private SharedPreferences sp;
    private Context context;

    // saveEntryText info
    private boolean isSaved = false;
    private boolean onPauseNotTakingPic = false;

    // password info
    private boolean needPasswordCheck = true;

    // Entry info
    private volatile String filename = "", locationName = "";
    private String partnerString = "", jobString = "";
    private double latitude = 0,  longitude = 0;
    private boolean gpsPermission = false,  storedLocationBool = false, dataLoaded = false, entryDeleted = false,  statuses = false, isFave = false, hasAudio = false;
    private int currMood = 0, imageCount = 0;
    private long fileCreationMillis = 0;

    // Lists
    private ArrayList<String> tagSuggestionsArrList;
    private ArrayList<CharSequence> tagsArrList;

    // views for activity
    private EditText entryTextET;
    private EditText titleET;
    private LinearLayout enteredTagsLL;

    // other components
    private LocationManager locationManager;
    private Location location;
    private Uri picFromCameraURI;

    // recording ui and components
    private Audio audio;

    // UI variables
    private int primaryColor = 0, secondaryColor = 0, darkThemeTextColor = 0, lightTextColor = 0;
    private Drawable tagsBackgroundDrawable;
    private String fontStyle, theme;
    private float textSize = 0;
    private Typeface userSelectedFontTF;
    private LinearLayout.LayoutParams tagsTVParams;

    @Override
    protected void onCreate(Bundle previousInstance) {
        super.onCreate(previousInstance);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        context = this;
       UI.setTheme(context, sp.getString("Theme", "Default"));

        // very small screen sizes will not look good on a horizontal screen so I block it.
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (previousInstance != null) {     // gets info about_activity_layout last instance from bundle
            // file variables
            filename = previousInstance.getString("FILENAME");
            isSaved = previousInstance.getBoolean("IS_SAVED");
            fileCreationMillis = previousInstance.getLong("FILE_CREATED_MILLIS");

            // info variables
            statuses = previousInstance.getBoolean("STATUSES");
            if(statuses) {
                jobString = previousInstance.getString("JOB");
                partnerString = previousInstance.getString("PARTNER");
            }
            currMood = previousInstance.getInt("MOOD");
            isFave = previousInstance.getBoolean("IS_FAVE");
            hasAudio = previousInstance.getBoolean("HAS_AUDIO");

            // location variables
            gpsPermission = previousInstance.getBoolean("GPS_PERMISSION");
            storedLocationBool = previousInstance.getBoolean("STORED_LOCATION");
            if (storedLocationBool) {
                latitude = previousInstance.getDouble("LATITUDE");
                longitude = previousInstance.getDouble("LONGITUDE");
                locationName = previousInstance.getString("LOCATION");
            }

            // personalization variables
            primaryColor = previousInstance.getInt("PRIMARY_COLOR");
            secondaryColor = previousInstance.getInt("SECONDARY_COLOR");
            lightTextColor = previousInstance.getInt("LIGHT_TEXT_COLOR");
            textSize = previousInstance.getFloat("TEXT_SIZE");
            fontStyle = previousInstance.getString("FONT_STYLE");
            theme = previousInstance.getString("THEME");
            darkThemeTextColor = previousInstance.getInt("DARK_THEME_TEXT_COLOR");


            // tags arrays
            // checks the bundle to see if the arrays are empty, if arrays aren't empty then all of the elements are added to this instance arrays with the same name. Otherwise this instance Arrays are instantiated.
            if (previousInstance.getCharSequenceArrayList("ARR_TAGS") != null )
                tagsArrList = new ArrayList<>(previousInstance.getCharSequenceArrayList("ARR_TAGS"));
            else
                tagsArrList = new ArrayList<>();

            if (previousInstance.getStringArrayList("SUGGESTED_TAGS") != null)
                tagSuggestionsArrList = new ArrayList<>(previousInstance.getStringArrayList("SUGGESTED_TAGS"));
            else
                tagSuggestionsArrList = new ArrayList<>();

        }

        setContentView(R.layout.new_entry_layout);

        // animates the actionbar_layout on the bottom as well as the date_selection_layout layout.
        LinearLayout infoLL = (LinearLayout) findViewById(R.id.info_layout);
        LinearLayout  markup = (LinearLayout) findViewById(R.id.markup);

        Animation slideDownAnim = AnimationUtils.loadAnimation(context, R.anim.info_down_from_top);
        Animation slideUpAnim = AnimationUtils.loadAnimation(context, R.anim.toolbar_up_from_bottom);

        infoLL.startAnimation(slideDownAnim);
        markup.startAnimation(slideUpAnim);

    }

    public void setupEntryData(){       // if there is nothing being loaded from bundle then there needs to be data_activity_layout loaded from SharedPreference or created here.
        fileCreationMillis = System.currentTimeMillis();

        statuses = sp.getBoolean("Statuses", false);
        if (statuses) {
            partnerString = sp.getString("Relationship", null);
            jobString = sp.getString("Occupation", null);
        }

        gpsPermission = sp.getBoolean("GPS", false);
        theme = sp.getString("Theme", "Default");

        int [] colors = UI.getThemeElements(context, theme);           // used to set background color and text color of certain views later on.
        primaryColor = colors[0];
        secondaryColor = colors[1];
        darkThemeTextColor = colors[2];


        lightTextColor =  ContextCompat.getColor(context,R.color.UILightForeground);
        textSize = Float.parseFloat(sp.getString("TextSize", "14"));
        fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";

        // initializes null ArrayLists
        tagsArrList = new ArrayList<>();
        tagSuggestionsArrList = new ArrayList<>();

    }

    public void setupSuggestedTags(){
        //  gets suggested tags from previously used tags found in xml
        tagSuggestionsArrList.addAll(Tags.getUniqueTags(getFilesDir()));

        // gets contact names from the users contacts to suggest tags.
        if (sp.getBoolean("Contacts", false)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {     // only shows explanation if user hasn't seen it or doesn't clicked "never show again"
                    Permissions.requestDialog(Permissions.MY_PERMISSIONS_REQUEST_CONTACTS, context);
                }else{
                    Permissions.permissionNotGranted(context, Permissions.MY_PERMISSIONS_REQUEST_CONTACTS);
                }

            }else
                tagSuggestionsArrList.addAll(Files.getContactSuggestions(this));

        }

    }


    // places all the right information to the UI such as time and date_selection_layout, location and statuses (if appropriate) and mood from previous instance. Among others.
    public void placeInfoToViews(TextView monthTV, TextView dayTV, TextView timeTV, TextView yearTV, TextView currentLocationTV, TextView relationshipTV, TextView occupationTV){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fileCreationMillis);

        monthTV.setText(String.format(Locale.getDefault(), "%1$tB", calendar));
        yearTV.setText(String.format(Locale.getDefault(), "%1$tY", calendar));
        timeTV.setText(MiscMethods.getLocalizedTime(calendar));
        dayTV.setText(String.format(Locale.getDefault(), "%1$td", calendar) + ',');


            // changes the value of the Status' if the user has the preference enabled with their values
            if (statuses) {
                relationshipTV.setText(partnerString);
                occupationTV.setText(jobString);

            } else {        //hides views if preference is off.
                relationshipTV.setVisibility(View.INVISIBLE);
                occupationTV.setVisibility(View.INVISIBLE);
            }

            // if gps permission is given by user the preference pane and location was not recorded yet in then the app will ping for location
            if (gpsPermission && ! storedLocationBool) {
                if (locationManager == null) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation? Only needed when the user hasn't seen the dialog before
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            Permissions.requestDialog(Permissions.MY_PERMISSIONS_REQUEST_LOCATION, context);
                        }else{
                            Permissions.permissionNotGranted(context, Permissions.MY_PERMISSIONS_REQUEST_LOCATION);
                        }

                    }else
                            getLocation(true, currentLocationTV);      // changes the location text view to the value of the users current location.
                }
            }else if( gpsPermission && storedLocationBool){     // if  gps permission is set to true and the app already pinged for location and has that info, it sets the location name to the TextView
                currentLocationTV.setText(locationName);
            }
            else
                getLocation(false, currentLocationTV);     // hides location text view


        if (isFave){
            Button faveButton = (Button) findViewById(R.id.add_like_button);
            changeFave(faveButton);
        }


    }



    // puts tags into the UI
    public void placeTagsToEntry(){
        // adds the tags from bundle if there were any.
            for (CharSequence tag: tagsArrList) {
                addTag(tag.toString(), false);
            }

    }

    public void getLocation(boolean isGPSGrantedByUser, TextView currentLocationTV){
        // if user has granted location services for app then location is pinged.
        if(isGPSGrantedByUser) {
            try {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 1000, this);
            } catch (SecurityException securityException) {
                Log.e("Location Exception", securityException.toString());
            }
        }
            else{
            currentLocationTV.setVisibility(View.INVISIBLE);
            }

    }


    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);       // hides keyboard


        if (hasAudio) {
            ImageSwitcher audioSwitcher = (ImageSwitcher) findViewById(R.id.audio);
            if (audio == null)
                audio = new Audio(context, filename, audioSwitcher, hasAudio);

        }

        onPauseNotTakingPic = false;
        if (sp == null || context == null ){
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            context = this;
        }

        // if onStart hasn't run (dataLoaded == false), UI hasn't been updated.
        // changes the UI of the views to match that of what the user wants.
        // adds time and date_selection_layout to entry
        if(! dataLoaded) {
            TextView monthTV = (TextView) findViewById(R.id.month_for_new_entry);
            TextView dayTV = (TextView) findViewById(R.id.day_for_new_entry);
            TextView timeTV = (TextView) findViewById(R.id.time_for_new_entry);
            TextView yearTV = (TextView) findViewById(R.id.year_for_new_entry);
            TextView currentLocationTV = (TextView) findViewById(R.id.location);
            TextView relationshipTV = (TextView) findViewById(R.id.relationship);
            TextView occupationTV = (TextView) findViewById(R.id.occupation);

            enteredTagsLL = (LinearLayout) findViewById(R.id.tags_in_entry);

            if (fileCreationMillis == 0) {      // this data_activity_layout setupViews will only run if fileCreationMillis is 0 and the only time that is true is when it never gets altered before onStart()
                setupEntryData();       // gets date_selection_layout, time, statuses, location, suggested tags, etc
                setupSuggestedTags();       // sets up suggested tags array with contact names and previously used tags.
            }

            placeInfoToViews(monthTV, dayTV, timeTV, yearTV, currentLocationTV, relationshipTV, occupationTV);      // places all info gotten from previous method to the UI
            changeMoodText(currMood, null);     // changes mood button to current mood.
            loadPicsFromLastInstance();         // puts pics from last instance to views.
            customizeUI(monthTV, dayTV, timeTV, yearTV, currentLocationTV, relationshipTV, occupationTV);       // changes the UI according to user preferences
            placeTagsToEntry();     // puts the tags user has added to the entry in the UI
        }

        // inits entryText EditText as well as puts a watcher to count the words and characters.
        if (entryTextET == null) {
            titleET = (EditText) findViewById(R.id.title_of_entry);
            entryTextET = (EditText) findViewById(R.id.entry_text);
            entryTextET.addTextChangedListener(new TextWatcher() {
                TextView textCount = (TextView) findViewById(R.id.textCount);

                @Override
                public synchronized void afterTextChanged(Editable s) {
                    StringBuilder characterCount = new StringBuilder("");
                    characterCount.append("C: ");
                    characterCount.append(Integer.toString(s.length()));
                    characterCount.append( '\t' );

                    StringBuilder wordCount = new StringBuilder("");
                    wordCount.append(characterCount);
                    wordCount.append("W: " );
                    wordCount.append(String.valueOf(MiscMethods.countWords(entryTextET.getText().toString())));

                    textCount.setText(wordCount);
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

        }

        dataLoaded = true;      // will prevent frequent onStart() calls from redoing operations that have already been done before.
    }



    public void loadPicsFromLastInstance(){
        int loadCount = 1;      // loops through all possible names for image file. Each photo file has a num X were X is the order in which photo was recorded.
        File loadPics = new File(getFilesDir(), filename + "(" + loadCount + ").png");      // gets photo with num X = loadCount
        while (loadPics.exists()) {
            makeImageViews(imageCount, loadPics);

            loadCount++;
            loadPics = new File(getFilesDir(), filename + "(" + loadCount + ").png");
            imageCount ++;      // image count = number of pics loaded.
            }
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



    public void setListenersToImageViews(ImageView imageView, final int imageNum, final LinearLayout holder){

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).startActivityForResult(LauncherMethods.launchPhotoViewer(v, context, filename), 2);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imageCount = Pictures.deleteOnePicFromEntry(v, holder, context, filename, imageNum);
                return false;
            }
        });

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        onPauseNotTakingPic = false;

        boolean isPasswordOn = sp.getBoolean("Password", false);
        if (isPasswordOn) {
            if (needPasswordCheck) {
                Intent i = new Intent(context, Passcode.class);
                startActivityForResult(i, 1);
            }
            needPasswordCheck = true;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        // file variables
        savedInstance.putString("FILENAME", filename);
        savedInstance.putLong("FILE_CREATED_MILLIS", fileCreationMillis);
        savedInstance.putBoolean("IS_SAVED", isSaved);

        // info variables
        savedInstance.putString("PARTNER", partnerString);
        savedInstance.putString("JOB", jobString);
        savedInstance.putInt("MOOD", currMood);
        savedInstance.putBoolean("STATUSES", statuses);
        savedInstance.putBoolean("IS_FAVE", isFave);
        if (audio != null)
            savedInstance.putBoolean("HAS_AUDIO", audio.isRecordingSaved());
        else
            savedInstance.putBoolean("HAS_AUDIO", false);

        //location variables
        savedInstance.putString("LOCATION", locationName);
        savedInstance.putBoolean("STORED_LOCATION", storedLocationBool);
        savedInstance.putDouble("LATITUDE", longitude);
        savedInstance.putDouble("LONGITUDE", longitude);
        savedInstance.putBoolean("GPS_PERMISSION", gpsPermission);

        // tags
        savedInstance.putStringArrayList("SUGGESTED_TAGS", tagSuggestionsArrList);
        savedInstance.putCharSequenceArrayList("ARR_TAGS", tagsArrList);

        // customizable things
        savedInstance.putInt("PRIMARY_COLOR", primaryColor);
        savedInstance.putInt("SECONDARY_COLOR", secondaryColor);
        savedInstance.putInt("LIGHT_TEXT_COLOR", lightTextColor);
        savedInstance.putFloat("TEXT_SIZE", textSize);
        savedInstance.putString("FONT_STYLE", fontStyle);
        savedInstance.putString("THEME", theme);
        savedInstance.putInt("DARK_THEME_TEXT_COLOR",darkThemeTextColor);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onPauseNotTakingPic = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onPauseNotTakingPic = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ((imageCount > 0 || (entryTextET.getText().toString().trim().length() > 0)  || audio!= null  || ! filename.equals("") || isSaved ) && ! entryDeleted) {
            if (onPauseNotTakingPic) {
                saveData();
            }
        }

    }

    public void moodButtonClicked(final View mood) {
        AlertDialog.Builder selectMood = new AlertDialog.Builder(context);
        CharSequence itemsT[] = getResources().getStringArray(R.array.moods_arr);

        selectMood.setTitle("Select Mood");
        selectMood.setNeutralButton(R.string.DISMISS, null);
        selectMood.setSingleChoiceItems(itemsT, currMood,
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        currMood = item;
                        changeMoodText(currMood, (TextView) mood);
                        dialog.dismiss();
                    }
                });
        selectMood.show();

    }

    public void changeMoodText(int mood, TextView moodTV){
        if (moodTV == null)
            moodTV = (TextView) findViewById(R.id.mood);

        moodTV.setText(MiscMethods.getMood(mood, moodTV));
    }


    public void customizeUI(TextView monthTV, TextView dayTV, TextView timeTV, TextView yearTV, TextView currentLocationTV, TextView relationshipTV, TextView occupationTV){
        EditText title = (EditText) findViewById(R.id.title_of_entry);
        EditText text = (EditText) findViewById(R.id.entry_text);

        LinearLayout  markup = (LinearLayout) findViewById(R.id.markup);
        LinearLayout content = (LinearLayout) findViewById(R.id.content);
        Button faveTV = (Button) findViewById(R.id.add_like_button);


        // weak references to UI elements
        WeakReference<EditText> titleWeak = new WeakReference<>(title);
        WeakReference<EditText> textWeak = new WeakReference<>(text);

        WeakReference<LinearLayout> markupWeak = new WeakReference<>(markup);
        WeakReference<LinearLayout> contentWeak = new WeakReference<>(content);


        if (sp.getString("Theme", "Default").contains("Onyx")) {
            CoordinatorLayout parent = (CoordinatorLayout) findViewById(R.id.New_Entry_Layout);
            Button mood = (Button) findViewById(R.id.mood);

            markupWeak.get().setBackgroundColor(secondaryColor);

            mood.setTextColor(darkThemeTextColor);
            monthTV.setTextColor(darkThemeTextColor);
            timeTV.setTextColor(darkThemeTextColor);
            titleWeak.get().setTextColor(darkThemeTextColor);
            textWeak.get().setTextColor(darkThemeTextColor);

            textWeak.get().setHighlightColor(ContextCompat.getColor(context, R.color.UIDarkBlue_Green));
            contentWeak.get().setBackgroundColor(ContextCompat.getColor(context, R.color.UIDarkGray));
            parent.setBackgroundColor(ContextCompat.getColor(context, R.color.UIDarkForeground));
        }


        if (  !  fontStyle.contains("DEFAULT") ) {

            userSelectedFontTF = Typeface.createFromAsset(this.getAssets(), "fonts_adapter_layout/" + fontStyle);

            titleWeak.get().setTypeface(userSelectedFontTF, Typeface.BOLD);
            textWeak.get().setTypeface(userSelectedFontTF);

            // if false, location textview will be removed...
            if(gpsPermission)
                currentLocationTV.setTypeface(userSelectedFontTF);

            timeTV.setTypeface(userSelectedFontTF);
            monthTV.setTypeface(userSelectedFontTF);
            if (statuses) {
                relationshipTV.setTypeface(userSelectedFontTF);
                occupationTV.setTypeface(userSelectedFontTF);
            }

            if (gpsPermission)
            currentLocationTV.setTextSize(textSize);
        }

        markupWeak.get().setBackgroundColor(secondaryColor);
        titleWeak.get().setTextColor(secondaryColor);
        dayTV.setTextColor(secondaryColor);
        faveTV.setTextColor(primaryColor);

        titleWeak.get().setTextSize(textSize + 3);
        if(statuses) {
            relationshipTV.setTextSize(textSize);
            occupationTV.setTextSize(textSize);
        }
        if (gpsPermission)
            currentLocationTV.setTextSize(textSize);

        monthTV.setTextSize(textSize);
        timeTV.setTextSize(textSize);
        dayTV.setTextSize(textSize + 5);
        yearTV.setTextSize(textSize);
        textWeak.get().setTextSize(textSize);
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

    private void takePic(){
        onPauseNotTakingPic = false;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        picFromCameraURI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png"));     // this stores the image to device storage
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picFromCameraURI);
        startActivityForResult(intent, USE_CAMERA_CONST);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case USE_CAMERA_CONST:
                needPasswordCheck = false;
                switch (resultCode){
                    case RESULT_OK:
                    try {
                        ++ imageCount;
                        saveData();
                        makeImageViews(imageCount, Pictures.saveImage(picFromCameraURI, context,  filename, imageCount));

                    } catch (IOException e) {
                        Log.e("Taking pic Err", e.toString());
                    }
                    break;

                }
                break;
            case PICK_PHOTO_CONST:
                needPasswordCheck = false;
                switch (resultCode){
                    case RESULT_OK:
                    try {
                        Uri selectedImage = data.getData();
                        ++ imageCount;
                        saveData();
                        makeImageViews(imageCount, Pictures.saveImage(selectedImage, context, filename, imageCount));
                    } catch (IOException e) {
                        Log.e("Pic file not found", e.toString());
                    }
                    break;

                }
                break;
            case 1:
                switch (resultCode){
                        case RESULT_OK:
                        needPasswordCheck = false;
                        break;
                    case RESULT_CANCELED:
                        needPasswordCheck = true;
                        break;
                }
                break;
        }
    }


    /*
    this method is called whenever a user is requested to accept or deny a permission and they dismiss the dialog
    It checks what they chose, and does an action according to it.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Permissions.MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      // if user checked allow
                    takePic();
                }
                break;

            case Permissions.MY_PERMISSIONS_REQUEST_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      // if user checked allow
                    tagSuggestionsArrList.addAll(Files.getContactSuggestions(this));
                }
                break;

            case Permissions.MY_PERMISSIONS_REQUEST_LOCATION:
                TextView currentLocationTV = (TextView) findViewById(R.id.location);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      // if user checked allow
                    getLocation(true, currentLocationTV);

                } else {
                    getLocation(false, currentLocationTV);
                }
                break;
            case Permissions.MY_PERMISSIONS_REQUEST_MIC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      // if user checked allow
                        saveData();
                        ImageSwitcher audioSwitcher = (ImageSwitcher) findViewById(R.id.audio);
                        if (audio == null)
                            audio = new Audio(context, filename, audioSwitcher);

                }
                break;
        }
    }


    public void shareFromMenuItem(MenuItem m) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.share_alert, null);
        // Get the layout inflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v);
        builder.setTitle(getResources().getString(R.string.share_dialog_title));
        builder.setNeutralButton(
                getResources().getString(R.string.share_dialog_neutral_button),
                null);
        builder.create();
        builder.show();
    }



    public void deleteClicked(View v) {
        // otherwise data_activity_layout has to be erased.
            AlertDialog.Builder t = new AlertDialog.Builder(this);
            t.setMessage(R.string.delete_entry_dialog_message_);
            t.setIcon(ContextCompat.getDrawable(this, R.drawable.white_discard));
            t.setPositiveButton(R.string.DELETE,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            entryDeleted = true;
                            if (isSaved) {
                                Files.deleteEntry(context.getFilesDir(), filename);
                                Files.removeNewEntryFromPreference(sp);
                            }
                            finish();
                        }

                    });
                     t.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });

            t.setCancelable(false);
            t.show();
        }



    public void addTag(String newTag, boolean addToList) {
        enteredTagsLL.setClickable(true);
        if (tagsBackgroundDrawable == null)
           tagsBackgroundDrawable = UI.getTagsBackgroundDrawable(context, theme);

        if (tagsTVParams == null){
            tagsTVParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            tagsTVParams.setMargins(0, 0, 45, 0);
        }

        if (addToList)
        tagsArrList.add(newTag);

        TextView tagTV = Tags.newTagView(context, textSize, lightTextColor, tagsTVParams, tagsBackgroundDrawable, fontStyle, userSelectedFontTF);
        tagTV.setText(newTag);
        enteredTagsLL.addView(tagTV);

        StringBuilder tagString = new StringBuilder("");
        tagString.append("\"");
        tagString.append(newTag);
        tagString.append("\"");
        tagString.append(getResources().getString(R.string.added));

        Toast toast = Toast.makeText(this, tagString.toString(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);

        toast.show();

    }

    public void emailButtonClicked(View mi) {
        EditText title = (EditText) findViewById(R.id.title_of_entry);
        onPauseNotTakingPic = false;

        MiscMethods.email(title.toString(), entryTextET.toString(), "", context);
    }

    public void EditTags(View v) {
        AlertDialog.Builder tagsList = new AlertDialog.Builder(this);
        CharSequence[] itemsT = new CharSequence[tagsArrList.size()];
        for (int i = 0; i < tagsArrList.size(); i++) {
            itemsT[i] = tagsArrList.get(i);
        }
        tagsList.setIcon(ContextCompat.getDrawable(context, R.drawable.tags));
        tagsList.setTitle(R.string.edit_tags_dialog_title);
        tagsList.setNegativeButton(R.string.CANCEL, null);
        tagsList.setSingleChoiceItems(itemsT, -1,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        tagsArrList.remove(item);
                        enteredTagsLL.removeViewAt(item);
                        if (tagsArrList.size() == 0)
                            enteredTagsLL.setClickable(false);

                        dialog.dismiss();

                    }
                });
        tagsList.show();



    }

    public void changeFave(View view){
        if (((Button) view).getText().equals("☆")){
            ((Button) view).setText("★");
            isFave = true;
        }
        else {
            ((Button) view).setText("☆");
            isFave = false;
        }
    }

    public void newTag(View v) {
        AlertDialog.Builder tempDialog = new AlertDialog.Builder(context);
        final AutoCompleteTextView tagTV = new AutoCompleteTextView(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, tagSuggestionsArrList);

        AlertDialog.Builder newTagAlert = Tags.getAddTagDialog(context.getResources(), tempDialog, tagTV, adapter, tagSuggestionsArrList);

        newTagAlert.setIcon(ContextCompat.getDrawable(context, R.drawable.tags));

        newTagAlert.setPositiveButton(getResources().getString(R.string.add),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        extractTagFromDialog(tagTV.getText().toString().trim());

                    }
                });

        tagTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    extractTagFromDialog(tagTV.getText().toString().trim());
                    tagTV.setText("");
                    return false;
                }
                return false;
            }
        });

        newTagAlert.create();
        newTagAlert.show();
    }


    public void extractTagFromDialog(String newTag){
        newTag = newTag.replaceAll("[^a-zA-Z \\s]", "");

        if (! newTag.equals("") ) {
            if (! tagsArrList.contains(newTag)) {
                addTag(newTag, true);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if ( locationName.equals("")) {
            this.location = location;
            new RetrieveLocation().execute("");
        }

    }

    private class RetrieveLocation extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            Geocoder geo = new Geocoder(NewEntry.this);
            List<Address> address;
            try {
                address = geo.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);

                locationName = address.get(0).getAddressLine(0) + " " + address.get(0).getAddressLine(1).replaceAll(address.get(0).getPostalCode(), "").trim();
                if (locationName.length() > 20)
                    locationName = address.get(0).getAddressLine(1).replaceAll(address.get(0).getPostalCode(), "").replaceAll(address.get(0).getCountryCode(), "").trim();


                storedLocationBool = true;
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                try {
                    locationManager.removeUpdates(NewEntry.this);
                }
                catch (SecurityException locErr){
                    Log.e("Loc Err", locErr.toString());
                }
                return locationName;
            } catch (Exception e1) {
                if (locationManager != null) {
                    try {
                        locationManager.removeUpdates(NewEntry.this);
                    }catch (SecurityException e){

                    }
                }
                e1.printStackTrace();
                return getResources().getString(R.string.network_err);
            }
        }

        protected void onPostExecute(String feed) {
            TextView currentLocationTV = (TextView) findViewById(R.id.location);
                currentLocationTV.setText(feed);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }


    public void boldButtonClicked(View bold) {
       MiscMethods.bold(entryTextET);
    }
    public void italicButtonClicked(View italic) {
        MiscMethods.italic(entryTextET);
    }
    public void underlineButtonClicked(View underline) {
        MiscMethods.underline(entryTextET);
    }


    public void saveButtonClicked(View v) {
        onPauseNotTakingPic = true;
        this.finish();
    }



    public void recordAudio(View v)  {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Permissions.requestDialog(Permissions.MY_PERMISSIONS_REQUEST_MIC, context);
            }else{
                Permissions.permissionNotGranted(context, Permissions.MY_PERMISSIONS_REQUEST_MIC);
            }
        } else {
                saveData();
                ImageSwitcher audioSwitcher = (ImageSwitcher) findViewById(R.id.audio);
                if (audio == null) {
                    Log.e("audio recording", "fiilename is " + filename);
                    audio = new Audio(context, filename, audioSwitcher);
                }
        }
    }

    public void messWithAudio(View v){
        if (audio != null)
            audio.messWithAudio();

    }

    public void playAudio(View view) {
        if (audio != null)
            audio.playAudio();
    }

    public void pauseAudio(View view) {
        if (audio != null)
            audio.pauseAudio();
    }

    public void replay(View v) {
        if (audio != null) {
            try {
                audio.replay();
            }catch(IOException io){
                Log.e("Audio file not found", io.toString());

            }
        }

    }

    synchronized public void saveData(){
        String currentFileName = titleET.getText().toString().trim().replaceAll("[^\\p{L} \\s 0-9]", "").replaceAll(" ", "_");
        String oldFileName = null;

        if (! filename.equals(currentFileName) ){         // when the title EditText and filename variable aren't equal all of the files have to be renamed.
            oldFileName = filename;
            filename = Files.renameFiles(context.getFilesDir(), filename, currentFileName);
            if ( ! isSaved)
                XML.recordNewEntryToXML(context.getResources(), context.getFilesDir(), filename, imageCount, partnerString, jobString, tagsArrList, locationName, latitude, longitude, isFave, storedLocationBool, currMood);
        }
        else if (filename.equals(currentFileName) && ! isSaved){
            try {
                filename = Files.createNewEntryFile(currentFileName, context);
                XML.recordNewEntryToXML(context.getResources(), context.getFilesDir(), filename, imageCount, partnerString, jobString, tagsArrList, locationName, latitude, longitude, isFave, storedLocationBool, currMood);

                Log.e("new file created", "name is " + filename);
            }catch (IOException io){
                Log.e("Err creating entry file", io.toString());
            }
        }

    try {
        if (isSaved)
            XML.updateEntryInXML(context.getResources(), context.getFilesDir(), filename, oldFileName, imageCount, partnerString, jobString, tagsArrList, locationName, latitude, longitude, isFave, storedLocationBool, currMood);
        isSaved = true;
        Files.saveEntryText(filename, context, entryTextET);
        Files.saveNewEntryInfoToPreferences(sp, filename, tagsArrList, isFave);
    } catch (IOException io) {
        Log.e("Err saving txt", io.toString());
    }

    }

}
