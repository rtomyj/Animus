package com.UtilityClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by CaptainSaveAHoe on 6/8/17.
 */

public class Tags {

    // returns a new TextView with custom padding and margins. It will be used to display tags.
    public synchronized static TextView newTagView(Context context, final float textSize, final int lightTextColor, final LinearLayout.LayoutParams tagsTVParams, final Drawable tagsBackgroundDrawable, String fontStyle, Typeface userSelectedFontTF){
        // New TextView object is created to house tag
        TextView textView = new TextView(context);
        textView.setTextColor(lightTextColor);
        // sets the background according to the theme being used.
        textView.setLayoutParams(tagsTVParams);
        textView.setPadding(24, 12, 24, 12);
        textView.setBackground(tagsBackgroundDrawable);

        if (textSize > 12)
            textView.setTextSize(textSize - 4);
        else
            textView.setTextSize(textSize - 2);

        switch (fontStyle){
            case "Default":
                break;
            default:
                textView.setTypeface(userSelectedFontTF);
                break;
        }
        return textView;
    }


    public static AlertDialog.Builder getAddTagDialog(Resources resources, AlertDialog.Builder newTagAlert, final AutoCompleteTextView tagTV, ArrayAdapter<String> adapter, ArrayList<String> tagSuggestionsArrList){

        LinearLayout.LayoutParams tagsTVParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        tagsTVParams.setMargins(20, 10, 20, 10);
        tagTV.setLayoutParams(tagsTVParams);

        tagTV.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        tagTV.setAdapter(adapter);

        tagTV.setThreshold(2);
        tagTV.requestFocus();

        newTagAlert.setTitle(resources.getString(R.string.new_tag));
        newTagAlert.setView(tagTV);

        newTagAlert.setNegativeButton(resources.getString(R.string.dismiss), null);

        return newTagAlert;
    }




    public static ArrayList<String> getUniqueTags(File fileDir){
        GetUniqueTags getUniqueTags = new GetUniqueTags(fileDir);
        getUniqueTags.execute("");

        ArrayList<String> tagSuggestionsArrList = new ArrayList<>();
        try {
            tagSuggestionsArrList.addAll(getUniqueTags.get());
        }catch (InterruptedException interruptedException){

        }
        catch(ExecutionException execException){

        }

        return tagSuggestionsArrList;
    }



    public static class GetUniqueTags extends AsyncTask<String, Integer, ArrayList<String>> {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc;
        File xml2 ;

        public GetUniqueTags(File fileDir) {
            xml2 = new File(fileDir, "Files.xml");
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> tagSuggestionsArrList = new ArrayList<>();
            factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            try {
                builder = factory.newDocumentBuilder();
                doc = builder.parse(xml2);

                NodeList nodeList = doc.getElementsByTagName("Files");
                Node node = nodeList.item(0);
                nodeList = node.getChildNodes();

                for (int z = 0; z < nodeList.getLength(); z++) {
                    node = nodeList.item(z);
                    if (node.getNodeName() != "#text") {
                        // searches through all tags in all files and adds those that are unique to suggestionsArrList
                        String tagsInCurrentNode = ((Element) node).getAttribute("tags");
                        int tagsInCurrentNodeInt = Integer.parseInt(tagsInCurrentNode);

                        for (int j = 0; j < tagsInCurrentNodeInt; j++) {
                            String tagName = ((Element) node).getAttribute("tag" + Integer.toString(j + 1));
                            tagName = tagName.replaceAll("_", " ");

                            if (! tagSuggestionsArrList.contains(tagName))
                                tagSuggestionsArrList.add(tagName);

                        }
                    }
                }
                Log.e("size of suggestions", Integer.toString(tagSuggestionsArrList.size()));
            } catch (Exception e) {
                Log.e("Err", e.toString());
            }

            return tagSuggestionsArrList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> tagSuggestionsArrList) {
            super.onPostExecute(tagSuggestionsArrList);

        }

    }
}
