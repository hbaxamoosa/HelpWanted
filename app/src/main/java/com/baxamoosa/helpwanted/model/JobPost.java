package com.baxamoosa.helpwanted.model;

import android.os.Parcel;
import android.os.Parcelable;

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

    public String id;
    public String name;
    public String address;
    public String phone;
    public String website;
    public double latitude;
    public double longitude;
    public long date;
    public String user;

    public JobPost() {
    }

    public JobPost(String id, String name, String address, String phone, String website, double latitude, double longitude, long date, String user) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.user = user;
    }

    public JobPost(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        phone = in.readString();
        website = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        date = in.readLong();
        user = in.readString();
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long setDate() {
        return date;
    }

    public void getDate(long date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(date);
        dest.writeString(user);
    }
}
