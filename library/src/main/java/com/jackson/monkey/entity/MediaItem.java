/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jackson.monkey.entity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.jackson.monkey.MimeType;


public class MediaItem implements Parcelable {
    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        @Nullable
        public MediaItem createFromParcel(Parcel source) {
            return new MediaItem(source);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };
    public static final long ITEM_ID_CAPTURE = -1;
    public static final long ITEM_ID_RECORD = -2;
    public static final String ITEM_DISPLAY_NAME_CAPTURE = "Capture";
    public static final String ITEM_DISPLAY_NAME_RECORD = "Record";
    public final long id;
    public final String mimeType;
    public Uri uri;
    public final long size;
    public final long duration; // only for video, in ms
    private final double mLatitude;
    private final double mLongitude;
    private final String mDate;
    private final long mWidth;
    private final long mHeight;
    private final String mPath;

    public MediaItem(long id, String mimeType, long size, long duration,
                     double latitude, double longitude, long width, long height, String path, String date) {
        this.id = id;
        this.mimeType = mimeType;
        Uri contentUri;
        if (isImage()) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (isVideo()) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            // ?
            contentUri = MediaStore.Files.getContentUri("external");
        }
        this.uri = ContentUris.withAppendedId(contentUri, id);
        this.size = size;
        this.duration = duration;
        mLatitude = latitude;
        mLongitude = longitude;
        mDate = date;
        mWidth = width;
        mHeight = height;
        mPath = path;
    }

    private MediaItem(Parcel source) {
        id = source.readLong();
        mimeType = source.readString();
        uri = source.readParcelable(Uri.class.getClassLoader());
        size = source.readLong();
        duration = source.readLong();
        mLatitude = source.readDouble();
        mLongitude = source.readDouble();
        mDate = source.readString();
        mWidth = source.readLong();
        mHeight = source.readLong();
        mPath = source.readString();
    }

    public static MediaItem valueOf(Cursor cursor) {
        return new MediaItem(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration")),
                cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mimeType);
        dest.writeParcelable(uri, 0);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeString(mDate);
        dest.writeLong(mWidth);
        dest.writeLong(mHeight);
        dest.writeString(mPath);
    }

    public Uri getContentUri() {
        return uri;
    }

    public boolean isCapture() {
        return id == ITEM_ID_CAPTURE;
    }

    public boolean isRecord(){
        return id == ITEM_ID_RECORD;
    }

    public boolean isImage() {
        return mimeType.equals(MimeType.JPEG.toString())
                || mimeType.equals(MimeType.PNG.toString())
                || mimeType.equals(MimeType.GIF.toString())
                || mimeType.equals(MimeType.BMP.toString())
                || mimeType.equals(MimeType.WEBP.toString());
    }

    public boolean isGif() {
        return mimeType.equals(MimeType.GIF.toString());
    }

    public boolean isVideo() {
        return mimeType.equals(MimeType.MPEG.toString())
                || mimeType.equals(MimeType.MP4.toString())
                || mimeType.equals(MimeType.QUICKTIME.toString())
                || mimeType.equals(MimeType.THREEGPP.toString())
                || mimeType.equals(MimeType.THREEGPP2.toString())
                || mimeType.equals(MimeType.MKV.toString())
                || mimeType.equals(MimeType.WEBM.toString())
                || mimeType.equals(MimeType.TS.toString())
                || mimeType.equals(MimeType.AVI.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaItem item = (MediaItem) o;

        if (id != item.id) return false;
        if (size != item.size) return false;
        if (duration != item.duration) return false;
        if (Double.compare(item.mLatitude, mLatitude) != 0) return false;
        if (Double.compare(item.mLongitude, mLongitude) != 0) return false;
        if (mWidth != item.mWidth) return false;
        if (mHeight != item.mHeight) return false;
        if (mimeType != null ? !mimeType.equals(item.mimeType) : item.mimeType != null)
            return false;
        if (uri != null ? !uri.equals(item.uri) : item.uri != null) return false;
        if (mDate != null ? !mDate.equals(item.mDate) : item.mDate != null) return false;
        return mPath != null ? mPath.equals(item.mPath) : item.mPath == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        temp = Double.doubleToLongBits(mLatitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mLongitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mDate != null ? mDate.hashCode() : 0);
        result = 31 * result + (int) (mWidth ^ (mWidth >>> 32));
        result = 31 * result + (int) (mHeight ^ (mHeight >>> 32));
        result = 31 * result + (mPath != null ? mPath.hashCode() : 0);
        return result;
    }

    public long getId() {
        return id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Uri getUri() {
        return uri;
    }

    public long getSize() {
        return size;
    }

    public long getDuration() {
        return duration;
    }

    public long getWidth() {
        return mWidth;
    }

    public long getHeight() {
        return mHeight;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getDate() {
        return mDate;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPath() {
        return mPath;
    }
}
