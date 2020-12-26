package com.eroglu.sevk;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ImageDownloader {
    private static final String TAG = "Utils";

    public static List<Image> loadImages(Context context, JSONArray array) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            List<Image> imageList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                Image image = gson.fromJson(array.getString(i), Image.class);
                imageList.add(image);
            }
            return imageList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
