package com.myappcompany.rajan.healthdepot.model;

import com.google.firebase.Timestamp;

public class RecordItem {

    private Timestamp mTimestamp;
    private String mClinic;
    private String mDisease;
    private String mRemarks;

    public RecordItem(Timestamp timestamp, String clinic, String disease, String remarks) {
        mTimestamp = timestamp;
        mClinic = clinic;
        mDisease = disease;
        mRemarks = remarks;
    }

    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        mTimestamp = timestamp;
    }

    public String getClinic() {
        return mClinic;
    }

    public void setClinic(String clinic) {
        mClinic = clinic;
    }

    public String getDisease() {
        return mDisease;
    }

    public void setDisease(String disease) {
        mDisease = disease;
    }

    public String getRemarks() {
        return mRemarks;
    }

    public void setRemarks(String remarks) {
        mRemarks = remarks;
    }
}
