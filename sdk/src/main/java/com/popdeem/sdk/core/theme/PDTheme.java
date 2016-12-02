package com.popdeem.sdk.core.theme;

/**
 * Created by niall on 02/12/2016.
 */

public class PDTheme {
    private PDThemeColors colors;
    private PDThemeImages images;
    private PDThemeFonts fonts;

    public PDTheme(){}

    public PDThemeColors getColors() {
        return colors;
    }

    public void setColors(PDThemeColors colors) {
        this.colors = colors;
    }

    public PDThemeImages getImages() {
        return images;
    }

    public void setImages(PDThemeImages images) {
        this.images = images;
    }

    public PDThemeFonts getFonts() {
        return fonts;
    }

    public void setFonts(PDThemeFonts fonts) {
        this.fonts = fonts;
    }
}

