package com.UtilityClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * Created by CaptainSaveAHoe on 7/4/17.
 */

public  class CustomAttributes {
    // UI Customization
    public int primaryColor = 0, secondaryColor = 0, tagsTextColor = 0, numLines, textColorForDarkThemes = 0, darkThemeForegroundColor, darkThemeBackgroundColor = 0;
    public float textSize = 0;
    public String fontStyle = "", theme = "";

    public Typeface userSelectedFontTF;
    public Animation animation;
    public  LinearLayout.LayoutParams tagsTVParams;
    public  Drawable tagsBackgroundDrawable, darkThemeSelectorShader;


    public CustomAttributes(Context context, SharedPreferences sp){
        // stores the primary/secondary app color as integers and the tags background color as a drawable. Less calls to the xml.
        theme =  sp.getString("Theme", "Default");
        int [] colors = AnimusUI.getThemeElements(context, theme);
        primaryColor = colors[0];
        secondaryColor = colors[1];
        textColorForDarkThemes = colors[2];
        darkThemeBackgroundColor = colors[3];
        darkThemeForegroundColor = colors[4];
        tagsTextColor = colors[5];

        textSize = Float.parseFloat(sp.getString("TextSize", "14"));
        fontStyle = sp.getString("FONTSTYLE", "DEFAULT").trim() + ".ttf";
        numLines = Integer.parseInt(sp.getString("LineNum", "3"));

        setupResources(context);

    }
    public CustomAttributes(Context context, int primaryColor, int secondaryColor, int tagsTextColor, int textColorForDarkThemes, int darkThemeBackgroundColor,
                            int numLines, float textSize, String fontStyle, String theme){
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.tagsTextColor = tagsTextColor;
        this. textColorForDarkThemes = textColorForDarkThemes;
        this.darkThemeBackgroundColor = darkThemeBackgroundColor;
        this.numLines = numLines;
        this.textSize = textSize;
        this.fontStyle = fontStyle;
        this.theme = theme;

        setupResources(context);
    }

    private void setupResources(Context context){
        if ( ! fontStyle.contains("DEFAULT"))
            userSelectedFontTF = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontStyle);

        tagsTVParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        tagsTVParams.setMargins(0, 0, 45, 0);

        tagsBackgroundDrawable = AnimusUI.getTagsBackgroundDrawable(context, theme);
        darkThemeSelectorShader = AnimusUI.getDarkSelectorDrawable(context, theme);
    }

    public void setAnimation(Animation animation){
        this.animation = animation;
    }

}
