package com.AnimusAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.UtilityClasses.AnimusFiles;
import com.UtilityClasses.AnimusLauncherMethods;
import com.UtilityClasses.AnimusMiscMethods;
import com.UtilityClasses.AnimusTags;
import com.UtilityClasses.AnimusUI;
import com.UtilityClasses.AnimusXML;
import com.rtomyj.Diary.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.ViewHolder> {

    // cache for deleted content
    private String cacheFileName, cacheTag1, cacheTag2, cachetTag3;
    private boolean cacheFileIsFavorite;
    private int cachePosition;

    // lists holding contents for the recycler view
    private volatile ArrayList<String> sortedFilesArrList ;
    private volatile ArrayList<String> tag1ArrList;
    private volatile ArrayList<String> tag2ArrList;
    private volatile ArrayList<String> tag3ArrList;
    private volatile ArrayList<Boolean> favArrList ;

    // UI Customization
    private Context context;
    private Typeface userSelectedFontTF;
    private float textSize = 0;
    private String fontStyle, theme;
    private volatile Animation animation;
    private volatile LinearLayout.LayoutParams tagsTVParams;
    private volatile Drawable tagsBackgroundDrawable, darkThemeSelectorShader;
    private int primaryColor, secondaryColor, tagsTextColor, numLines, textColorForDarkThemes;

    // controls which views get animated
    private short maxPosition;

    // other
    private Locale locale;

    /*
    holds a limited amount of UI objects. When the recycler view needs to get new objects, old ones are recycled from here.
    Otherwise the views are used again by the LayoutManager
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        private View parent;
        private TextView summaryTV;
        private TextView titleTV;
        private TextView timeTV;
        private TextView dayTV;
        private TextView monthTV;
        private LinearLayout tagsLL;
        private TextView menuTV;
        private TextView favTV;
        private CardView cardView;


        ViewHolder(View parent) {
            super(parent);
            summaryTV = (TextView) parent.findViewById(R.id.summary_of_entry);
            titleTV = (TextView) parent.findViewById((R.id.title));
            timeTV = (TextView) parent.findViewById(R.id.file_year);
            dayTV = (TextView) parent.findViewById(R.id.file_day);
            monthTV = (TextView) parent.findViewById(R.id.file_month);
            tagsLL = (LinearLayout) parent.findViewById(R.id.tags_file_adapter);
            menuTV = (TextView) parent.findViewById(R.id.menu);
            favTV = (TextView) parent.findViewById(R.id.fav);
            cardView = (CardView) parent.findViewById(R.id.file_frag);
        }

    }

    // there are two constructors and they both require the same data setup, this takes care of it
    private void setAdapterBaseData(Context context){
        if (this.context ==null) {
            locale = Locale.getDefault();
            this.context = context;

            if (!fontStyle.contains("DEFAULT")) {  // if font style is anything but the value default, it creates a typeface with the specified name.
                userSelectedFontTF = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontStyle);
            }

            animation = AnimationUtils.loadAnimation(context, R.anim.fadein);

            tagsTVParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            tagsTVParams.setMargins(0, 0, 45, 0);

            tagsBackgroundDrawable = AnimusUI.getTagsBackgroundDrawable(context, theme);
            darkThemeSelectorShader = AnimusUI.getDarkSelectorDrawable(context, theme);

        }
    }

    // Constructor with the necessary data set being passed to it.
    public EntriesAdapter(Context context, ArrayList<String> temp, ArrayList<String> tag1ArrList, ArrayList<String> tag2ArrList, ArrayList<String> tag3ArrList, ArrayList<Boolean> favArrList,
                          int primaryColor, int secondaryColor, int textColorForDarkThemes, int tagsTextColor, String theme, String fontStyle, int numLines, float textSize ) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.textColorForDarkThemes = textColorForDarkThemes;
        this.tagsTextColor = tagsTextColor;
        this.theme = theme;
        this.fontStyle = fontStyle;
        this.numLines = numLines;
        this.textSize= textSize;

        // transfers all the info from the calling activity to this adapter.
        sortedFilesArrList = new ArrayList<>(temp);
        this.tag1ArrList = new ArrayList<>(tag1ArrList);
        this.tag2ArrList = new ArrayList<>(tag2ArrList);
        this.tag3ArrList = new ArrayList<>(tag3ArrList);
        this.favArrList= new ArrayList<>(favArrList);

        setAdapterBaseData(context);
    }

    // constructor with only the months array and the calling activities Context
    public EntriesAdapter(Context context, int primaryColor, int secondaryColor, int textColorForDarkThemes, int tagsTextColor, String theme, String fontStyle,
                          int numLines, float textSize){
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.textColorForDarkThemes = textColorForDarkThemes;
        this.tagsTextColor = tagsTextColor;
        this.theme = theme;
        this.fontStyle = fontStyle;
        this.numLines = numLines;
        this.textSize= textSize;

        ArrayList<File> filesArrayList = new ArrayList<>();
        filesArrayList.addAll(AnimusFiles.getFilesWithExtension(context.getFilesDir(),".txt"));

        Collections.sort(filesArrayList, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.valueOf(f2.lastModified()).compareTo(
                        f1.lastModified());
            }
        });

        final int sortedFilesArrListSize = filesArrayList.size();
        sortedFilesArrList = new ArrayList<>(sortedFilesArrListSize);
        tag1ArrList = new ArrayList<>(Collections.nCopies(sortedFilesArrListSize, ""));
        tag2ArrList = new ArrayList<>(Collections.nCopies(sortedFilesArrListSize, ""));
        tag3ArrList = new ArrayList<>(Collections.nCopies(sortedFilesArrListSize, ""));
        favArrList = new ArrayList<>(Collections.nCopies(sortedFilesArrListSize, false));

        for (File list : filesArrayList)
            sortedFilesArrList.add(list.getName());

        filesArrayList.clear();
        AnimusXML.getEntriesAdapterInfo(sortedFilesArrList, tag1ArrList, tag2ArrList, tag3ArrList, favArrList, context.getFilesDir());

        setAdapterBaseData(context);
    }

    // Create new views (invoked by the layout manager). Should do any parent specific customizations after the LayoutInflater method is invoked.
    @Override
    public EntriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_of_entries, parent, false);
        return  new ViewHolder(parentView);
    }


    // Changes the color/text size/etc of views. All values have been stored in variables in the setAdapterBaseData() method
    private void customizeUI(ViewHolder holder ) {
        // changes colors of UI elements according to theme.
        holder.favTV.setTextColor(primaryColor);
        holder.menuTV.setTextColor(primaryColor);
        holder.dayTV.setTextColor(secondaryColor);
        holder.titleTV.setTextColor(secondaryColor);

        switch (theme) {
            case "Onyx P":
            case "Onyx B":
                holder.cardView.setBackground(darkThemeSelectorShader);
                holder.monthTV.setTextColor(textColorForDarkThemes);
                holder.summaryTV.setTextColor(textColorForDarkThemes);
                holder.menuTV.setTextColor(textColorForDarkThemes);
                holder.timeTV.setTextColor(textColorForDarkThemes);
                holder.favTV.setTextColor(textColorForDarkThemes);
                break;
        }

        // changes text size and number of lines according to user preference
        holder. summaryTV.setMaxLines(numLines);
        holder.summaryTV.setMinLines(numLines);

        holder.summaryTV.setTextSize(textSize);
        holder.dayTV.setTextSize(textSize + (float) 5);
        holder.monthTV.setTextSize(textSize);
        holder.titleTV.setTextSize(textSize+ (float) 2);
        holder.timeTV.setTextSize(textSize);


        // changes font if applicable
        if (!fontStyle.contains("DEFAULT")) {
            holder.summaryTV.setTypeface(userSelectedFontTF);
            holder.monthTV.setTypeface(userSelectedFontTF);
            holder.titleTV.setTypeface(userSelectedFontTF);
            holder. dayTV.setTypeface(userSelectedFontTF);
            holder.timeTV.setTypeface(userSelectedFontTF);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public synchronized void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.parent.setClickable(true);
        holder.parent.setId(position);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {
                int position = parent.getId();
                AnimusLauncherMethods.chosenFile(context, sortedFilesArrList.get(position), position, sortedFilesArrList);

            }
        });

        customizeUI(holder);
        setInfo(holder, position);
        tags(holder, position);

        // the id for the two views is the position they are in the RecyclerView so whenever they are clicked I can just check the id of the view and pull the info at that position from adapter
        holder.menuTV.setId(position);
        holder.favTV.setId(position);

        if (position > maxPosition && position > 2) {
            holder.cardView.startAnimation(animation);
            maxPosition = (short) position;
        }

    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sortedFilesArrList.size();
    }


    // sets text to the views from the entry file/files.xml
    private void setInfo(ViewHolder holder, int position){
        char[] summaryBytes;
        int length;
        Calendar calendar = Calendar.getInstance();

        if (holder.summaryTV.getMaxLines() < 4 ) {
            length = 250;
            summaryBytes = new char[length];
        }
        else  {
            length = 400;
            summaryBytes = new char[length];
        }

        File file = new File(context.getFilesDir(), sortedFilesArrList.get(position));
        // sets filename to selected view.
        String filename = file.getName(); // gets the file name with the extension and the underscores instead of spaces.
        String formattedFilename = filename.replaceAll(".txt", "").replaceAll("_", " ");
        if (formattedFilename.length() > 3) { // user can name a file with a name shorter than 4 letters. This catches that exception
            switch (formattedFilename.substring(0, 4)) {
                case "Temp":
                    holder.titleTV.setText("");
                    break;
                default:
                    holder.titleTV.setText(formattedFilename);
                    break;
            }
        }else
            holder.titleTV.setText(formattedFilename);

        // gets a calendar instance and changes views text to corresponding info.
        calendar.setTimeInMillis(file.lastModified());

        holder.timeTV.setText(AnimusMiscMethods.getLocalizedTime(calendar));

        // uses string builder to append info about the day of month.
        StringBuilder dayBuilder = new StringBuilder(String.format(locale, "%1$td", calendar));
        dayBuilder.append(",");
        holder.dayTV.setText(dayBuilder.toString());
        holder.monthTV.setText(String.format(locale, "%1$tB", calendar));


        // sets the faves view to the corresponding emoticon
        try {
            if (favArrList.get(position))
                holder.favTV.setText(Html.fromHtml("★"));
            else
                holder.favTV.setText(Html.fromHtml("☆"));
        }
        catch(Exception e){
            holder.favTV.setText(Html.fromHtml("☆"));
            Log.e("Fave views state", e.toString());

        }

        try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader( context.openFileInput(filename)), length);
                bufferedReader.read(summaryBytes,0, length);
                String summaryString = new String(summaryBytes);
                summaryString = summaryString.substring(summaryString.indexOf("\n") + 3);   // the +3 skips some weird annotation that is added when using writeUTF()

            if (summaryString.isEmpty()) { // if the entry was empty, places a text informing the user and changes the gravity to center.
                holder.summaryTV.setGravity(Gravity.CENTER |Gravity.CENTER_VERTICAL);
                holder.summaryTV.setText(context.getResources().getString(R.string.text));


            } else { // else sets the first couple of bytes of the entry to the summary view and change the font style if applicable.
                    if (!fontStyle.equals("Default"))
                    holder.summaryTV.setTypeface(holder.summaryTV.getTypeface(), Typeface.NORMAL);
                    holder.summaryTV.setText(Html.fromHtml(summaryString));
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // iterates through the tag arrays and creates a new TextView for the tag if it is necessary.
    synchronized public void tags(ViewHolder holder, int position){
        holder.tagsLL.removeAllViews();

        int count = 0;
        while (count < 3) {
            String tagName = "";
            switch (count) {
                    case 0:
                        tagName = tag1ArrList.get(position);
                        break;
                    case 1:
                        tagName =  tag2ArrList.get(position);
                        break;
                    case 2:
                        tagName = tag3ArrList.get(position);
                        break;
                }
            if (! tagName.equals("")) {
                    final TextView tagTV = AnimusTags.newTagView(context, textSize, tagsTextColor, tagsTVParams, tagsBackgroundDrawable, fontStyle, userSelectedFontTF);
                    tagTV.setText(tagName.replaceAll("_", " "));
                    holder.tagsLL.addView(tagTV);
                }
                else{
                    break;
                }
            count ++;
        }
    }


    public void newFavSet(ArrayList<Boolean> favArrList2) {
        this.favArrList.clear();
        this.favArrList.addAll(favArrList2);
        this.notifyDataSetChanged();
    }


    public void fileChanged(ArrayList<String> sortedFiles,
                            ArrayList<String> tag1ArrList2, ArrayList<String> tag2ArrList2,
                            ArrayList<String> tag3ArrList2, ArrayList<Boolean> favArrList2) {
        this.sortedFilesArrList.clear();
        this.tag1ArrList.clear();
        this.tag2ArrList.clear();
        this.tag3ArrList.clear();
        this.favArrList.clear();

        this.tag1ArrList.addAll(tag1ArrList2);
        this.tag2ArrList.addAll(tag2ArrList2);
        this.tag3ArrList.addAll(tag3ArrList2);
        this.favArrList.addAll(favArrList2);
        this.sortedFilesArrList.addAll(sortedFiles);

        notifyDataSetChanged();
        notifyItemRangeChanged(0, getItemCount());
    }


    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }


    public ArrayList<Boolean> getFaves(){
    return favArrList;
}
    public ArrayList<String> getSortedFiles(){
        return sortedFilesArrList;
    }
    public String getFilename(int position){
        return sortedFilesArrList.get(position);
    }
    public String getFirstTag(int position){
        return tag1ArrList.get(position);
    }
    public String getSecondTag(int position){
        return tag2ArrList.get(position);
    }
    public String getThirdTag(int position){
        return tag3ArrList.get(position);
    }
    public Boolean getFaveAtPosition(int position){
        return favArrList.get(position);
    }

    public ArrayList<String> getFirstTags(){
        return tag1ArrList;
    }
    public ArrayList<String> getSecondTags(){
        return tag2ArrList;
    }
    public ArrayList<String> getThirdTags(){
        return tag2ArrList;
    }
    public void childRemoved(int target){
        cachePosition = target;
        cacheFileName = sortedFilesArrList.get(target);
        cacheTag1 = tag1ArrList.get(target);
        cacheTag2 = tag2ArrList.get(target);
        cachetTag3 = tag3ArrList.get(target);
        cacheFileIsFavorite = favArrList.get(target);

        sortedFilesArrList.remove(target);
        tag1ArrList.remove(target);
        tag3ArrList.remove(target);
        tag2ArrList.remove(target);
        favArrList.remove(target);
        this.notifyItemRemoved(target);
        this.notifyItemRangeChanged(target, this.getItemCount());

    }
    public void restorePreviousDeletion(){
        sortedFilesArrList.add(cachePosition, cacheFileName);
        tag1ArrList.add(cachePosition, cacheTag1);
        tag2ArrList.add(cachePosition, cacheTag2);
        tag3ArrList.add(cachePosition, cachetTag3);
        favArrList.add(cachePosition, cacheFileIsFavorite);
        this.notifyItemInserted(cachePosition);
        notifyItemRangeChanged(cachePosition, this.getItemCount());
    }


     public void setFaveStatus (int target, boolean status) {

        favArrList.set(target, status);
        notifyItemChanged(target);
    }

    public void newEntry(String name, String tag1, String tag2, String tag3, boolean fave){
        sortedFilesArrList.add(0, name);
        tag1ArrList.add(0, tag1);
        tag2ArrList.add(0, tag2);
        tag3ArrList.add(0, tag3);
        favArrList.add(0, fave);

        notifyItemInserted(0);
        notifyItemRangeChanged(0, getItemCount());
    }
    }