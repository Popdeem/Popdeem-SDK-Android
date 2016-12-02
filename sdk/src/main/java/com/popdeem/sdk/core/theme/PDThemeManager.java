package com.popdeem.sdk.core.theme;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by niall on 02/12/2016.
 */
public class PDThemeManager {

    private static PDThemeManager ourInstance = new PDThemeManager();
    private PDTheme theme;
    private Context mContext;

    public static PDThemeManager getInstance() {
        return ourInstance;
    }

    private PDThemeManager() {
    }

    public void setup(Context context, String filename) {
        ourInstance.mContext = context;
        try {
            JSONObject jobj = new JSONObject(loadJSONFromAsset(filename));
            String themeString = jobj.getString("popdeem");
            Gson gson = new GsonBuilder().serializeNulls().create();
            PDTheme theme = gson.fromJson(themeString, PDTheme.class);
            if (theme != null) {
                ourInstance.theme = theme;
            } else {

            }
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public int getPrimaryAppColor() {
        return Color.parseColor(ourInstance.theme.getColors().getPrimaryAppColor());
    }

    public int getPrimaryInverseColor() {
        return Color.parseColor(ourInstance.theme.getColors().getPrimaryInverseColor());
    }

    public int getViewBackGroundColor() {
        return Color.parseColor(ourInstance.theme.getColors().getViewBackgroundColor());
    }
}
