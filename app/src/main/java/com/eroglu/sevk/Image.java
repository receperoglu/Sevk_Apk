package com.eroglu.sevk;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.eroglu.sevk.DomainName.getRawURL;

public class Image {
    private static final String TAG = "Utils";
    @SerializedName("Path")
    @Expose
    private String imageUrl;

    public String getImageUrl() {
        return getRawURL() + "/abi/dosyalar/" + imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}



