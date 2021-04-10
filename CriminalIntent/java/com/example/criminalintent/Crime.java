package com.example.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;
    private String mSuspect;
    public Crime() {
        this(UUID.randomUUID());
    }
    public Crime(UUID crimeId){
        mId = crimeId;
        mDate = new Date();
    }
    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }
    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }
    public UUID getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }
    public Date getDate() {
        return mDate;
    }
    public boolean isSolved() {
        return mSolved;
    }
    public void setId(UUID id) {
        mId = id;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public void setDate(Date date) {
        mDate = date;
    }
    public void setSolved(boolean solved) {
        mSolved = solved;
    }
    public String getSuspect() {
        return mSuspect;
    }
    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
    public String getPhotoFileName() { return "IMG_"+getId().toString() + ".jpg";}
}
