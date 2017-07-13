package com.EntryActivities;

import java.util.ArrayList;
import java.util.HashMap;


public class EntryInfo extends HashMap{

    private int tagNum;
    private int currMood;
    private int photoNum;

    private String location;
    private String currOccupation;
    private String currRelationship;

    private ArrayList<String> tagsArrList = new ArrayList<>();
    private long date;

    public EntryInfo(String filename){

    }
    public EntryInfo(short numberOfTags, short totalFiles, short bottomRange, short topRange, short startingPos){

    }
    private boolean isFav;


    private boolean getIsFav(){

        return isFav;
    }

    private int getTagNum(){
        return tagNum;

    }

    private int getCurrMood(){
        return currMood;
    }

    private int getPhotoNum(){
        return photoNum;
    }
}
