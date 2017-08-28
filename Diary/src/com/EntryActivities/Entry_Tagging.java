package com.EntryActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.UtilityClasses.Tags;
import com.rtomyj.Diary.R;

/**
 * Created by MacMini on 8/25/17.
 */

public class Entry_Tagging extends Entry_Location {

    public void extractTagFromDialog(String newTag){
        newTag = newTag.replaceAll("[^a-zA-Z \\s]", "");

        if (! newTag.equals("") ) {
            if (! entryMeta.tagsArrList.contains(newTag)) {
                addTag(newTag, true);
            }
        }
    }

    public void addTag(String newTag, boolean addToList) {
        enteredTagsLL.setClickable(true);
        LinearLayout.LayoutParams tagsTVParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        tagsTVParams.setMargins(0, 0, 45, 0);


        if (addToList)
            entryMeta.tagsArrList.add(newTag);

        TextView tagTV = Tags.newTagView(context, userUIPreferences.textSize, userUIPreferences.textColorForDarkThemes, tagsTVParams, userUIPreferences.tagsBackgroundDrawable, userUIPreferences.fontStyle, userUIPreferences.userSelectedFontTF);
        tagTV.setText(newTag);
        enteredTagsLL.addView(tagTV);

        StringBuilder tagString = new StringBuilder("");
        tagString.append("\"");
        tagString.append(newTag);
        tagString.append("\"");
        tagString.append(getResources().getString(R.string.added));

    }

    public void newTag(View v) {
        AlertDialog.Builder tempDialog = new AlertDialog.Builder(context);
        final AutoCompleteTextView tagTV = new AutoCompleteTextView(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, entryMeta.tagSuggestionsArrList);

        AlertDialog.Builder newTagAlert = Tags.getAddTagDialog(context.getResources(), tempDialog, tagTV, adapter, entryMeta.tagSuggestionsArrList);

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

    public void EditTags(View v) {
        AlertDialog.Builder tagsList = new AlertDialog.Builder(this);
        CharSequence[] itemsT = new CharSequence[entryMeta.tagsArrList.size()];
        for (int i = 0; i < entryMeta.tagsArrList.size(); i++) {
            itemsT[i] = entryMeta.tagsArrList.get(i);
        }
        tagsList.setIcon(ContextCompat.getDrawable(context, R.drawable.tags));
        tagsList.setTitle(R.string.edit_tags_dialog_title);
        tagsList.setNegativeButton(R.string.CANCEL, null);
        tagsList.setSingleChoiceItems(itemsT, -1,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        entryMeta.tagsArrList.remove(item);
                        enteredTagsLL.removeViewAt(item);
                        if (entryMeta.tagsArrList.size() == 0)
                            enteredTagsLL.setClickable(false);

                        dialog.dismiss();

                    }
                });
        tagsList.show();

    }


    // puts tags into the UI
    public void placeTagsToEntry(){
        // adds the tags from bundle if there were any.
        for (CharSequence tag: entryMeta.tagsArrList) {
            addTag(tag.toString(), false);
        }

    }
}
