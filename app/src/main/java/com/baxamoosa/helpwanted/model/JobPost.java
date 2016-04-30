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

    public String _id;
    public String businessId;
    public String businessName;
    public String businessAddress;
    public String businessPhone;
    public String businessWebsite;
    public double businessLatitude;
    public double businessLongitude;
    public int wageRate;
    public long date;
    public String user;

    public JobPost() {
    }

    public JobPost(String _id, String businessId, String businessName, String businessAddress, String businessPhone, String businessWebsite, double businessLatitude, double businessLongitude, int wageRate, long date, String user) {
        this._id = _id;
        this.businessId = businessId;
        this.businessName = businessName;
        this.businessAddress = businessAddress;
        this.businessPhone = businessPhone;
        this.businessWebsite = businessWebsite;
        this.businessLatitude = businessLatitude;
        this.businessLongitude = businessLongitude;
        this.wageRate = wageRate;
        this.date = date;
        this.user = user;
    }

    public JobPost(Parcel in) {
        _id = in.readString();
        businessId = in.readString();
        businessName = in.readString();
        businessAddress = in.readString();
        businessPhone = in.readString();
        businessWebsite = in.readString();
        businessLatitude = in.readDouble();
        businessLongitude = in.readDouble();
        wageRate = in.readInt();
        date = in.readLong();
        user = in.readString();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getbusinessName() {
        return businessName;
    }

    public void setbusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getbusinessAddress() {
        return businessAddress;
    }

    public void setbusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getbusinessPhone() {
        return businessPhone;
    }

    public void setbusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getbusinessWebsite() {
        return businessWebsite;
    }

    public void setbusinessWebsite(String businessWebsite) {
        this.businessWebsite = businessWebsite;
    }

    public double getbusinessLatitude() {
        return businessLatitude;
    }

    public void setbusinessLatitude(double businessLatitude) {
        this.businessLatitude = businessLatitude;
    }

    public double getbusinessLongitude() {
        return businessLongitude;
    }

    public void setbusinessLongitude(double businessLongitude) {
        this.businessLongitude = businessLongitude;
    }

    public int getWageRate() {
        return wageRate;
    }

    public void setWageRate(int wageRate) {
        this.wageRate = wageRate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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
        dest.writeString(_id);
        dest.writeString(businessId);
        dest.writeString(businessName);
        dest.writeString(businessAddress);
        dest.writeString(businessPhone);
        dest.writeString(businessWebsite);
        dest.writeDouble(businessLatitude);
        dest.writeDouble(businessLongitude);
        dest.writeInt(wageRate);
        dest.writeLong(date);
        dest.writeString(user);
    }
}
