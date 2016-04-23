package com.baxamoosa.helpwanted.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.baxamoosa.helpwanted.BuildConfig;

import timber.log.Timber;

/**
 * Created by hasnainbaxamoosa on 4/20/16.
 */
public class JobPost implements Parcelable {

    public static final Creator<JobPost> CREATOR = new Creator<JobPost>() {
        @Override
        public JobPost createFromParcel(Parcel in) {
            return new JobPost(in);
        }

        @Override
        public JobPost[] newArray(int size) {
            return new JobPost[size];
        }
    };
    private String id;
    private String name;
    private String address;
    private String phone;
    private String website;
    private boolean favorite;
    private long date;

    public JobPost() {
        if (BuildConfig.DEBUG) {
            Timber.v("JobPost()");
        }
    }

    public JobPost(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        phone = in.readString();
        website = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long setDate() {
        return date;
    }

    public void getDate(long date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(website);
    }
}
