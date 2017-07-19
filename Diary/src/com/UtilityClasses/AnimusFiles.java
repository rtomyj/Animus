package com.UtilityClasses;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by MacMini on 7/18/16.
 */

public class AnimusFiles {
    private AnimusFiles(){}

    public static class DeleteMultipleEntries extends AsyncTask<String, Integer, String> {
        Context context;
        ArrayList<String> deletedFileNamesArr;

        public DeleteMultipleEntries(Context context, ArrayList<String> deletedFileNamesArr){
            this.context = context;
            this.deletedFileNamesArr = new ArrayList<>(deletedFileNamesArr);
        }
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            for(String file: deletedFileNamesArr){
                AnimusFiles.deleteEntry(context.getFilesDir(), file);
            }
            AnimusXML.deleteMultipleEntriesFromXML(context.getFilesDir(), deletedFileNamesArr);


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }

    }



    public static void deleteEntry(File filesDir, String filename){         // takes filesDir and filename without extension and deletes all files with name  passed that were used to store txt, photos and audio.
       if (filename.contains(".txt")){
           filename = filename.substring(0, filename.indexOf('.'));
       }

        StringBuilder txtFilename = new StringBuilder(filename);
        StringBuilder mpegFilename = new StringBuilder(filename);
        StringBuilder pngFilename = new StringBuilder(filename);

        txtFilename.append(".txt");
        mpegFilename.append(".mpeg4");

        File txtFile = new File(filesDir, txtFilename.toString());
        txtFile.delete();

        File audioFile = new File(filesDir, mpegFilename.toString());
        audioFile.delete();

        int photoNum = 1;
        pngFilename.append('(');
        pngFilename.append(photoNum);
        pngFilename.append(").png");
        File photoFile = new File(filesDir, pngFilename.toString());
        while (photoFile.exists() ) {
            photoFile.delete();
            photoNum++;
            pngFilename.delete(0, pngFilename.length());
            pngFilename.append('(');
            pngFilename.append(photoNum);
            pngFilename.append(").png");

            photoFile = new File(filesDir, pngFilename.toString());
        }

        AnimusXML.deleteEntryFromXML(filesDir, filename);
    }


    public static String renameFiles(File filesDir, String oldFilename, String newFileName){
        StringBuilder old_txtFilename = new StringBuilder(oldFilename);
        StringBuilder old_mpegFilename = new StringBuilder(oldFilename);
        StringBuilder old_pngFilename = new StringBuilder(oldFilename);

        old_txtFilename.append(".txt");
        old_mpegFilename.append(".mpeg4");

        newFileName = findUnusedFileName(filesDir, newFileName);        //  tries to find an unused filename with the same starting sequence as the "newFileName" String

        StringBuilder new_txtFilename = new StringBuilder(newFileName);
        StringBuilder new_mpegFilename = new StringBuilder(newFileName);
        StringBuilder new_pngFilename = new StringBuilder(newFileName);

        new_txtFilename.append(".txt");
        new_mpegFilename.append(".mpeg4");

        File txtFile = new File(filesDir, old_txtFilename.toString());
        txtFile.renameTo(new File(filesDir, new_txtFilename.toString()));
        File mpegFile = new File(filesDir, old_mpegFilename.toString());
        mpegFile.renameTo(new File(filesDir, new_mpegFilename.toString()));


        int photoNum = 1;
        StringBuilder photoNumExtension = new StringBuilder("");

        photoNumExtension.append('(');
        photoNumExtension.append(photoNum);
        photoNumExtension.append(").png");

        old_pngFilename.append(photoNumExtension);
        new_pngFilename.append(photoNumExtension);

        File photoFile = new File(filesDir, old_pngFilename.toString());
        while (photoFile.exists() ) {
            photoFile.renameTo(new File(filesDir, new_pngFilename.toString()));
            photoNum++;
            photoNumExtension.delete(0, photoNumExtension.length());
            old_pngFilename.delete(0, old_pngFilename.length());
            new_pngFilename.delete(0, new_pngFilename.length());

            photoNumExtension.append('(');
            photoNumExtension.append(photoNum);
            photoNumExtension.append(").png");

            old_pngFilename.append(oldFilename);
            old_pngFilename.append(photoNumExtension);

            new_pngFilename.append(newFileName);
            new_pngFilename.append(photoNumExtension);

            photoFile = new File(filesDir, old_pngFilename.toString());
        }

        Log.i(oldFilename, "has been renamed to " + newFileName);

        return newFileName;

    }


    public static ArrayList<String> getContactSuggestions(Context context){
        ArrayList<String> tagSuggestionsArrList = new ArrayList<>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                if (!cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).equals("")) {
                    tagSuggestionsArrList.add(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                }
            }
            cur.close();
        }


        return tagSuggestionsArrList;
    }



    public static void saveEntryText(String filename, Context context, EditText entryTextTV) throws FileNotFoundException {
        String txt = ".txt";

        DataOutputStream dos = new DataOutputStream(context.openFileOutput(filename + txt, Context.MODE_PRIVATE));

        WeakReference<DataOutputStream> dosWeak = new WeakReference<>(dos);
        try {
            dosWeak.get().writeUTF(filename.trim() + "\n");
            dosWeak.get().writeUTF(entryTextTV.getText().toString());
            dosWeak.get().flush();
            dosWeak.get().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String createNewEntryFile(String filename, Context context) throws  IOException{
        String txt = ".txt";
            filename.replaceAll(",", " ");
            filename.replaceAll("'", "");

            filename = findUnusedFileName(context.getFilesDir(), filename);
            File entryFile = new File(context.getFilesDir(), filename + txt);

            entryFile.createNewFile();

        return filename;
    }

    private static String findUnusedFileName(File filesDir, String desiredFilename){
        if (desiredFilename.equals(""))
            desiredFilename = "Temp".concat(AnimusMiscMethods.randomizedExtension());

        String txt = ".txt";
        File entryFile = new File(filesDir, desiredFilename + txt);
        int counter = 1;
        while (entryFile.exists()) {
            String temp =  desiredFilename + Integer.toString(counter);
            entryFile = new File(filesDir, temp + txt);
            counter++;
        }
        return desiredFilename;
    }


    public static void saveNewEntryInfoToPreferences(SharedPreferences sp, String filename, ArrayList<CharSequence> arrTags, boolean isFave){
        String txt = ".txt";
        sp.edit().putString("NEWFILENAME", filename.concat(txt)).apply();
        if (arrTags.size() >0)
            sp.edit().putString("NEWFILETAG1", arrTags.get(0).toString()).apply();

        if (arrTags.size() >1)
            sp.edit().putString("NEWFILETAG2", arrTags.get(1).toString()).apply();
        if (arrTags.size() >2)
            sp.edit().putString("NEWFILETAG3", arrTags.get(2).toString()).apply();

        sp.edit().putBoolean("NEWFILEFAVE", isFave).apply();

    }

    public static void removeNewEntryFromPreference(SharedPreferences sp){
        sp.edit().remove("NEWFILENAME").apply();
        sp.edit().remove("NEWFILETAG1").apply();
        sp.edit().remove("NEWFILETAG2").apply();
        sp.edit().remove("NEWFILETAG3").apply();
        sp.edit().remove("NEWFILEFAVE").apply();

    }

    public static ArrayList<File> getFilesWithExtension(File filesDir, final String extension){
        ArrayList<File> filesArrayList = new ArrayList<>();

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(extension);
            }
        };
        File[]  entryFileArray = filesDir.listFiles(filter);

        Collections.addAll(filesArrayList, entryFileArray);
        return  filesArrayList;
    }

    public static void deleteFilesWithNoName(ArrayList<File> filesArrayList, String extension){
        for(File file: filesArrayList){
            String filename = file.getName().replace(extension, "");
            int length = filename.length();

            if ((length <= 4 && filename.charAt(0) =='(') || length == 0){
                file.delete();
            }
            else if (filename.substring(length - 3, length).equals("(0)")){
                file.delete();

        }
    }

    }

    public static boolean doesFileExist(File filesDirs, String filename, String extension){
        File file = new File(filesDirs, filename + extension);
        return file.exists();
    }

    /*
        creates a File object to see how many characters are in the file. if length is 1 then there is one character.

        Due to bad programming this App writes the filename in the file using .writeUTF() and as such there are characters that don't do anything in the beelining of the file and after the
        writing of the filename. The blocks of code that remove characters from the object summaryBuilder is there to mitigate this. Plans to change this oversight are in the way but
        to support legacy code the deletes will still have to remain.

        readLength is the length of the charBuffer. This limits over-reading characters for situations where only some of the text is necessary (ie summarizing a file)
     */

    public static String getTextFromFile(Context context, String filename, int readLength) throws IOException {
        File file = new File(context.getFilesDir(), filename);
        long fileLength = file.length();
        if (readLength> fileLength)
            readLength = (int) fileLength;

        fileLength -= filename.length();
        --fileLength;


        if (fileLength > 0) {
            char[] summaryBytes = new char[readLength];

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)), readLength);
            bufferedReader.read(summaryBytes, 0, readLength);
            bufferedReader.close();
            //Log.e("buffer size", Integer.toString(summaryBytes.length));

            StringBuilder summaryBuilder = new StringBuilder();
            summaryBuilder.append(summaryBytes);

            summaryBuilder.delete(0, 2);


            // removes filename from the text of the file
            filename = filename.replaceAll(" ", "_");
            for (int iter = 0; iter < filename.length(); iter++) {
                if (summaryBuilder.charAt(0) == filename.charAt(iter))
                    summaryBuilder.deleteCharAt(0);
                else
                    break;
            }

           summaryBuilder.delete(0, 3);

            return summaryBuilder.toString().trim();
        }else{
            return "";
        }
    }


}
