package com.popdeem.sdk.core.theme;

/**
 * Created by niall on 02/12/2016.
 */

public class PDThemeFonts {
    private String primaryFont;
    private String boldFont;
    private String lightFont;

    public PDThemeFonts () {}

    public String getPrimaryFont() {
        return primaryFont;
    }

    public void setPrimaryFont(String primaryFont) {
        this.primaryFont = primaryFont;
    }

    public String getBoldFont() {
        return boldFont;
    }

    public void setBoldFont(String boldFont) {
        this.boldFont = boldFont;
    }

    public String getLightFont() {
        return lightFont;
    }

    public void setLightFont(String lightFont) {
        this.lightFont = lightFont;
    }
}
