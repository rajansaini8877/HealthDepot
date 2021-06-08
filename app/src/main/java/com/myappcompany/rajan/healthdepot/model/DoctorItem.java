package com.myappcompany.rajan.healthdepot.model;

public class DoctorItem {

    private String mUid;
    private String mName;
    private String mEmail;
    private String mAadharNumber;
    private String mImrNumber;

    public DoctorItem(String uid, String name, String email, String aadharNumber, String imrNumber) {
        mUid = uid;
        mName = name;
        mEmail = email;
        mAadharNumber = aadharNumber;
        mImrNumber = imrNumber;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getAadharNumber() {
        return mAadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        mAadharNumber = aadharNumber;
    }

    public String getImrNumber() {
        return mImrNumber;
    }

    public void setImrNumber(String imrNumber) {
        mImrNumber = imrNumber;
    }
}
