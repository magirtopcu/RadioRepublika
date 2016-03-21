package com.alchemik.radiorepublika.model;

/**
 * Created by lesze on 3/19/2016.
 */
public class News {
    private String mTitle;
    private String mTime;
    private String mURL;

    public News(String title, String time, String URL) {
        mTitle = title;
        mTime = time;
        mURL = URL;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String URL) {
        mURL = URL;
    }
}
