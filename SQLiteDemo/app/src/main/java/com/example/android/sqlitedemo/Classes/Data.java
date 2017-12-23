package com.example.android.sqlitedemo.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import static android.os.UserHandle.readFromParcel;

/**
 * Created by Shiv Kumar Aggarwal on 25-11-2017.
 */


public class Data implements Parcelable {

    String name;
    String doj;
    float percentage;

    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {

        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        public Data[] newArray(int size) {

            return new Data[size];
        }

    };

    public Data() {

    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
        doj = in.readString();
        percentage = in.readFloat();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(doj);
        dest.writeFloat(percentage);
    }
    public Data(Parcel in) {
        super();
        readFromParcel(in);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }


}
