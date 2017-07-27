package com.UtilityClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import com.rtomyj.Diary.R;

/**
 * Created by CaptainSaveAHoe on 6/5/17.
 */

public  class UI {

    // Changes the base theme to the user selected theme.
    public static void setTheme(Context context, String theme){
        switch(theme){
            case "Onyx P":
                context.setTheme(R.style.OnyxP);
                break;
            case "Onyx B":
                context.setTheme(R.style.OnyxB);
                break;
            case "Material":
                context.setTheme(R.style.Material);
                break;
            case "Material 2":
                context.setTheme(R.style.Material2);
                break;
            case "Material 3":
                context.setTheme(R.style.Material3);
                break;
            case "Material 4":
                context.setTheme(R.style.Material4);
                break;
            case "Material 5":
                context.setTheme(R.style.Material5);
                break;
            case "Material 6":
                context.setTheme(R.style.Material6);
                break;
            default:
                break;

        }
    }

    public static int[] getThemeElements(Context context, String theme){
        int primaryColor = 0, secondaryColor =0, darkThemeTextColor = 0, darkThemeBackgroundColor = 0, darkThemeForegroundColor = 0;

        switch (theme) {
            case "Default":
                primaryColor = ContextCompat.getColor(context, R.color.UIPink);
                secondaryColor = ContextCompat.getColor(context, R.color.UIBlue);
                break;
            case "Material":
                primaryColor = ContextCompat.getColor(context, R.color.UIMaterialPink);
                secondaryColor = ContextCompat.getColor(context, R.color.UIMaterialLightGreen);
                break;
            case "Material 2":
                primaryColor = ContextCompat.getColor(context, R.color.UIMaterialBlue);
                secondaryColor = ContextCompat.getColor(context, R.color.UIMaterialDeepOrange);
                break;
            case "Material 3":
                primaryColor = ContextCompat.getColor(context, R.color.UIMaterialPurple);
                secondaryColor = ContextCompat.getColor(context, R.color.UIMaterialDeepYellow);
                break;
            case "Material 4":
                primaryColor = ContextCompat.getColor(context, R.color.UIMaterialOrange);
                secondaryColor = ContextCompat.getColor(context, R.color.UIMaterialGreen);
                break;
            case "Material 5":
                primaryColor = ContextCompat.getColor(context, R.color.UIMaterialDeepGreen);
                secondaryColor = ContextCompat.getColor(context, R.color.UIMaterialRed);
                break;
            case "Material 6":
                primaryColor = ContextCompat.getColor(context, R.color.UIMaterialDarkTeal);
                secondaryColor = ContextCompat.getColor(context, R.color.UIMaterialBrown);
                break;
            case "Onyx P":
                secondaryColor = ContextCompat.getColor(context, R.color.UIDark_Pink);
                darkThemeTextColor = ContextCompat.getColor(context,R.color.UIDarkNormalText);
                darkThemeForegroundColor = ContextCompat.getColor(context, R.color.UIDarkForeground);
                darkThemeBackgroundColor = ContextCompat.getColor(context, R.color.UIDarkBackground);
                break;
            case "Onyx B":
                darkThemeTextColor = ContextCompat.getColor(context,R.color.UIDarkNormalText);
                secondaryColor = ContextCompat.getColor(context, R.color.UIDarkBlue);
                darkThemeForegroundColor = ContextCompat.getColor(context, R.color.UIDarkForeground);
                darkThemeBackgroundColor = ContextCompat.getColor(context, R.color.UIDarkBackground);
                break;
        }

        int [] colors = new int[6];
        colors[0] = primaryColor;
        colors[1] = secondaryColor;
        colors[2] = darkThemeTextColor;
        colors[3] = darkThemeBackgroundColor;
        colors[4] = darkThemeForegroundColor;
        colors[5] = ContextCompat.getColor(context, R.color.UILightForeground);

        return colors;
    }

    public static Drawable getTagsBackgroundDrawable(Context context, String theme){
        Drawable tagsBackgroundDrawable = null;
        switch (theme) {
            case "Default":
                tagsBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.tag_background_color_default);
                break;
            case "Material":
                tagsBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.tag_background_color_material_1);
                break;
            case "Material 2":
                tagsBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.tag_background_color_material_2);
                break;
            case "Material 3":
                tagsBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.tag_background_color_material_3);
                break;
            case "Material 4":
                tagsBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.tag_background_color_material_4);
                break;
            case "Material 5":
                tagsBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.tag_background_color_material_5);
                break;
            case "Material 6":
                tagsBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.tag_background_color_material_6);
                break;
            case "Onyx P":
            case "Onyx B":
                tagsBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.dark_drop_shadow_purple);
                break;
        }
        return tagsBackgroundDrawable;
    }


     static Drawable getDarkSelectorDrawable(Context context, String theme){
        Drawable selectorDrawable = null;
        switch (theme) {
            case "Onyx P":
                selectorDrawable = ContextCompat.getDrawable(context, R.drawable.onyx_selector);
                break;
            case "Onyx B":
                selectorDrawable = ContextCompat.getDrawable(context, R.drawable.onyx_selector);
                break;
        }
        return selectorDrawable;
    }



}
