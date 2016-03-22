package com.alchemik.radiorepublika.model;


/**
 * Created by Leszek Jasek on 3/19/2016.
 */
public class Track {
    private String mTrackTitle;
    private String mTrackSubtitle;
    private boolean mIsPremiere;
    private boolean mIsLive;
    private String mStartTime;
    private long mStartDateTime;
    private long mEndDateTime;
    private long mDurationTime;
    private String mPicUrl;

    public Track() {
        mIsPremiere = false;
        mIsLive = false;
        mTrackSubtitle = "";
    }

    public Track(String trackTitle, String startTime, String picUrl) {
        this();
        mTrackTitle = trackTitle;
        mStartTime = startTime;
        mPicUrl = picUrl;
    }

    public Track(String trackTitle, String startTime, long startDateTime, long endDateTime) {
        this();
        mTrackTitle = trackTitle;
        mStartTime = startTime;
        mStartDateTime = startDateTime;
        mEndDateTime = endDateTime;
    }

    public String getTrackTitle() {
        return mTrackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        mTrackTitle = trackTitle;
    }

    public String getTrackSubtitle() {
        return mTrackSubtitle;
    }

    public void setTrackSubtitle(String trackSubtitle) {
        mTrackSubtitle = trackSubtitle;
    }

    public boolean isLive() {
        return mIsLive;
    }

    public void setIsLive(boolean isLive) {
        mIsLive = isLive;
    }

    public boolean isPremiere() {
        return mIsPremiere;
    }

    public void setIsPremiere(boolean isPremiere) {
        mIsPremiere = isPremiere;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getPicUrl() {
        return mPicUrl;
    }

    public void setPicUrl(String picUrl) {
        mPicUrl = picUrl;
    }

    public long getStartDateTime() {
        return mStartDateTime;
    }

    public void setStartDateTime(long startDateTime) {
        mStartDateTime = startDateTime;
    }

    public long getDurationTime() {
        return mDurationTime;
    }

    public void setDurationTime(long durationTime) {
        mDurationTime = durationTime;
    }

    public long getEndDateTime() {
        return mEndDateTime;
    }

    public void setEndDateTime(long endDateTime) {
        mEndDateTime = endDateTime;
    }

    public boolean isCurrentlyPlaying() {
        return getStartDateTime() <= System.currentTimeMillis() && System.currentTimeMillis() < getEndDateTime();
    }

    @Override
    public String toString() {
        return "Track{" +
                "mTrackTitle='" + mTrackTitle + '\'' +
                ", mTrackSubtitle='" + mTrackSubtitle + '\'' +
                ", mIsPremiere=" + mIsPremiere +
                ", mIsLive=" + mIsLive +
                ", mStartTime='" + mStartTime + '\'' +
                ", mPicUrl='" + mPicUrl + '\'' +
                '}';
    }
}
