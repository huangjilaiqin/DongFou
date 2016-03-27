package com.lessask.dongfou;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by huangji on 2015/8/12.
 */
public class ResponseError implements Parcelable {
    private String error;
    private int errno;

    public ResponseError(){
    }
    public ResponseError(int errno, String error){
        this.errno = errno;
        this.error = error;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(error);
        dest.writeInt(errno);
    }

    public static final Creator<ResponseError> CREATOR
             = new Creator<ResponseError>() {
         public ResponseError createFromParcel(Parcel in) {
             String error = in.readString();
             int errno = in.readInt();

             return new ResponseError(errno,error);
         }

         public ResponseError[] newArray(int size) {
             return new ResponseError[size];
         }
    };

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }
}
