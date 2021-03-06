package com.Adapters.Parents;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.UtilityClasses.DeletedEntry;
import com.UtilityClasses.LauncherMethods;
import com.UtilityClasses.MiscMethods;
import com.UtilityClasses.Tags;
import com.UtilityClasses.CustomAttributes;
import com.rtomyj.Diary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/*
    Parent class for adapters that show a summary of the file .txt file, its last modified date, 3 of its tags and whether it was a favorite entry.
 */
public class EntriesBaseAdapter extends AdapterSummaryCache<EntriesBaseAdapter.ViewHolder> {
    private DeletedEntry deletedEntry;

    // lists holding contents for the recycler view
    protected ArrayList<String> sortedFilesArrList;
    protected ArrayList<String> tag1ArrList;
    protected ArrayList<String> tag2ArrList;
    protected ArrayList<String> tag3ArrList;
    protected ArrayList<Boolean> favArrList;

    // controls which views get animated
    private short maxPosition;

    // other
    protected int initSize = 1;

    /*
    holds a limited amount of UI objects. When the recycler view needs to get new objects, old ones are recycled from here.
    Otherwise the views are used again by the LayoutManager
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private boolean alreadyCustomized = false;
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
            this.parent = parent;
            summaryTV = parent.findViewById(R.id.summary_of_entry);
            titleTV = parent.findViewById((R.id.title));
            timeTV = parent.findViewById(R.id.file_year);
            dayTV = parent.findViewById(R.id.file_day);
            monthTV = parent.findViewById(R.id.file_month);
            tagsLL = parent.findViewById(R.id.tags_file_adapter);
            menuTV = parent.findViewById(R.id.menu);
            favTV = parent.findViewById(R.id.fav);
            cardView = parent.findViewById(R.id.file_frag);
        }

    }


    // there are two constructors and they both require the same data setupViews, this takes care of it
    private void setupData() {
        locale = Locale.getDefault();
        userUIPreferences.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fadein));

    }

    // Constructor with the necessary data set being passed to it.
    protected EntriesBaseAdapter(Context context, ArrayList<String> sortedFilesArrList, ArrayList<String> tag1ArrList, ArrayList<String> tag2ArrList, ArrayList<String> tag3ArrList,
                                 ArrayList<Boolean> favArrList, CustomAttributes userUIPreferences) {
        super(context, userUIPreferences);
        // transfers all the info from the calling activity to this adapter.
        this.sortedFilesArrList = new ArrayList<>(sortedFilesArrList);
        this.tag1ArrList = new ArrayList<>(tag1ArrList);
        this.tag2ArrList = new ArrayList<>(tag2ArrList);
        this.tag3ArrList = new ArrayList<>(tag3ArrList);
        this.favArrList = new ArrayList<>(favArrList);

        setupData();
    }


    // constructor with only the months array and the calling activities Context
    protected EntriesBaseAdapter(Context context, CustomAttributes userUIPreferences) {
        super(context, userUIPreferences);
        setupData();
    }


    // Create new views (invoked by the layout manager). Should do any parent specific customizations after the LayoutInflater method is invoked.
    @Override
    public EntriesBaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_of_entries, parent, false);
        return new ViewHolder(parentView);
    }


    // Changes the color/text size/etc of views. All values have been stored in variables in the setupData() method
    private void customizeUI(ViewHolder holder) {
        if (!holder.alreadyCustomized) {
            holder.alreadyCustomized = true;

            // changes TextColor
            holder.dayTV.setTextColor(userUIPreferences.secondaryColor);
            holder.titleTV.setTextColor(userUIPreferences.secondaryColor);

            // changes background and text color depending whether theme is dark
            switch (userUIPreferences.theme) {
                case "Onyx P":
                case "Onyx B":
                    holder.cardView.setBackground(userUIPreferences.darkThemeSelectorShader);
                    holder.monthTV.setTextColor(userUIPreferences.textColorForDarkThemes);
                    holder.summaryTV.setTextColor(userUIPreferences.textColorForDarkThemes);
                    holder.menuTV.setTextColor(userUIPreferences.textColorForDarkThemes);
                    holder.timeTV.setTextColor(userUIPreferences.textColorForDarkThemes);
                    holder.favTV.setTextColor(userUIPreferences.textColorForDarkThemes);
                    break;
                default:
                    holder.menuTV.setTextColor(userUIPreferences.primaryColor);
                    holder.favTV.setTextColor(userUIPreferences.primaryColor);
                    break;
            }

            // changes text size and number of lines according to user preference
            holder.summaryTV.setMaxLines(userUIPreferences.numLines);
            holder.summaryTV.setMinLines(userUIPreferences.numLines);

            // changes text size
            holder.summaryTV.setTextSize(userUIPreferences.textSize);
            holder.dayTV.setTextSize(userUIPreferences.largeTextSize);
            holder.monthTV.setTextSize(userUIPreferences.textSize);
            holder.titleTV.setTextSize(userUIPreferences.mediumTextSize);
            holder.timeTV.setTextSize(userUIPreferences.textSize);

            // changes font if applicable
            if (userUIPreferences.userSelectedFontTF != null) {
                holder.summaryTV.setTypeface(userUIPreferences.userSelectedFontTF);
                holder.monthTV.setTypeface(userUIPreferences.userSelectedFontTF);
                holder.titleTV.setTypeface(userUIPreferences.userSelectedFontTF);
                holder.dayTV.setTypeface(userUIPreferences.userSelectedFontTF);
                holder.timeTV.setTypeface(userUIPreferences.userSelectedFontTF);
            }


        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public synchronized void onBindViewHolder(ViewHolder holder, final int position) {
        setOnclick(holder.cardView, position);
        customizeUI(holder);
        setInfo(holder, position);
        tags(holder, position);

        if (position > maxPosition && position > 2) {
            holder.parent.startAnimation(userUIPreferences.animation);
            maxPosition = (short) position;
        }

    }
    private void setOnclick(CardView parent, int position){
        parent.setClickable(true);
        parent.setId(position);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {
                int position = parent.getId();
                LauncherMethods.chosenFile(context, sortedFilesArrList.get(position), position, sortedFilesArrList);

            }
        });

    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sortedFilesArrList.size();
    }


    // sets text to the views from the entry file/files.xml
    private void setInfo(ViewHolder holder, int position) {
        // the id for the two views is the position they are in the RecyclerView so whenever they are clicked I can just check the id of the view and pull the info at that position from adapter
        holder.menuTV.setId(position);
        holder.favTV.setId(position);

        // sets filename to selected view.
        String currentFile = sortedFilesArrList.get(position);
        StringBuilder filenameBuilder = new StringBuilder(currentFile);
        int indexOfExtension = filenameBuilder.indexOf(".txt");     // sometimes the adapter will not have the filenames with the .txt extension
        if (indexOfExtension > -1)
         filenameBuilder.delete(indexOfExtension, filenameBuilder.length());

        String formattedFilename = filenameBuilder.toString().replaceAll("_", " ");

        if (formattedFilename.length() > 3) { // user can name a file with a name shorter than 4 letters. This catches that exception
            switch (formattedFilename.substring(0, 4)) {
                case "Temp":
                    holder.titleTV.setText("");
                    break;
                default:
                    holder.titleTV.setText(formattedFilename);
                    break;
            }
        } else
            holder.titleTV.setText(formattedFilename);


        Calendar calendar = Calendar.getInstance();

        // gets a calendar instance and changes views text to corresponding info.
        File file = new File(context.getFilesDir(), sortedFilesArrList.get(position));
        calendar.setTimeInMillis(file.lastModified());

        holder.timeTV.setText(MiscMethods.getLocalizedTime(calendar));

        // uses string builder to append info about the day of month.
        StringBuilder dayBuilder = new StringBuilder(String.format(locale, "%1$td", calendar));
        dayBuilder.append(",");
        holder.dayTV.setText(dayBuilder.toString());
        holder.monthTV.setText(String.format(locale, "%1$tB", calendar));


        // sets the faves view to the corresponding emoticon
        if (favArrList.get(position))
            holder.favTV.setText("★");
        else
            holder.favTV.setText("☆");


        setSummary(holder.summaryTV, currentFile);

    }




    // iterates through the tag arrays and creates a new TextView for the tag if it is necessary.
    private synchronized void tags(ViewHolder holder, int position){
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
                    final TextView tagTV = Tags.newTagView(context, userUIPreferences.textSize, userUIPreferences.tagsTextColor, userUIPreferences.tagsTVParams, userUIPreferences.tagsBackgroundDrawable,
                            userUIPreferences.fontStyle, userUIPreferences.userSelectedFontTF);
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


    // called whenever all ArrayLists all get changed.
    public void fileChanged(ArrayList<String> sortedFiles, ArrayList<String> tag1ArrList2, ArrayList<String> tag2ArrList2, ArrayList<String> tag3ArrList2, ArrayList<Boolean> favArrList2) {
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


    // user can delete entry from the respective main activity. Deleted entry is cached in case user undo's deletion using SnackBar
    public void childRemoved(int target){
        deletedEntry = new DeletedEntry(sortedFilesArrList.get(target), tag1ArrList.get(target), tag2ArrList.get(target), tag3ArrList.get(target), favArrList.get(target), target);

        sortedFilesArrList.remove(target);
        tag1ArrList.remove(target);
        tag3ArrList.remove(target);
        tag2ArrList.remove(target);
        favArrList.remove(target);

        this.notifyItemRemoved(target);
        this.notifyItemRangeChanged(target, this.getItemCount());

    }

    // Restores deleted entry from SnackBar undo button.
    public void restorePreviousDeletion(){
        int cachePosition = deletedEntry.cachePosition;
        sortedFilesArrList.add(cachePosition, deletedEntry.cacheFileName);
        tag1ArrList.add(cachePosition, deletedEntry.cacheTag1);
        tag2ArrList.add(cachePosition, deletedEntry.cacheTag2);
        tag3ArrList.add(cachePosition, deletedEntry.cachetTag3);
        favArrList.add(cachePosition, deletedEntry.cacheFileIsFavorite);
        deletedEntry = null;

        notifyItemInserted(cachePosition);
        notifyItemRangeChanged(cachePosition, this.getItemCount());
    }


     public void setFaveStatus (int target, boolean status) {
        favArrList.set(target, status);
        notifyItemChanged(target);
    }

    public void newEntry(String name, String tag1, String tag2, String tag3, boolean isFave){
        sortedFilesArrList.add(0, name);
        tag1ArrList.add(0, tag1);
        tag2ArrList.add(0, tag2);
        tag3ArrList.add(0, tag3);
        favArrList.add(0, isFave);

        notifyItemInserted(0);
        notifyItemRangeChanged(0, getItemCount());
    }

    }