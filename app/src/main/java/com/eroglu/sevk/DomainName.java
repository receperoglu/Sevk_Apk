package com.eroglu.sevk;

public class DomainName {

    private static final String RAW_URL = "https://statu.space/"; //Domain URL


    private static final String BASE_URL = RAW_URL+ "StartApi.ashx?platform=android&ProcessType="; //Service URL

    public static String getURL() {
        return BASE_URL;
    }

    public static String getRawURL() {
        return RAW_URL;
    }


}
