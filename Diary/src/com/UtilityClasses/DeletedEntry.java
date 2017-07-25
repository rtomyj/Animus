package com.UtilityClasses;

/**
 * Created by CaptainSaveAHoe on 7/24/17.
 */

public class DeletedEntry {
    // cache for deleted content
    public String cacheFileName, cacheTag1, cacheTag2, cachetTag3;
    public boolean cacheFileIsFavorite;
    public int cachePosition;

    public DeletedEntry(String cacheFileName, String cacheTag1, String cacheTag2, String cachetTag3, boolean cacheFileIsFavorite, int cachePosition){
        this.cacheFileName = cacheFileName;
        this.cacheTag1 = cacheTag1;
        this.cacheTag2 = cacheTag2;
        this.cachetTag3 = cachetTag3;
        this.cacheFileIsFavorite = cacheFileIsFavorite;
        this.cachePosition = cachePosition;
    }
}
