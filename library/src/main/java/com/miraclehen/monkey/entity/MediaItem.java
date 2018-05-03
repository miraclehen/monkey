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
package com.miraclehen.monkey.entity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.miraclehen.monkey.MimeType;
import com.miraclehen.monkey.utils.DateTimeUtil;


public class MediaItem implements Parcelable {

    public static final long ITEM_ID_CAPTURE = -1;
    public static final long ITEM_ID_RECORD = -2;
    public static final String ITEM_DISPLAY_NAME_CAPTURE = "Capture";
    public static final String ITEM_DISPLAY_NAME_RECORD = "Record";
    public long id;
    public String mimeType;
    public Uri uri;
    public long length;
    public long duration; // only for video, in ms
    private double latitude;
    private double longitude;
    private long createDate;
    private long addDate;
    private long width;
    private long height;
    private String originalPath;
    private int orientation;


    //----------->>没有去取的字段<<---------
    //大缩略图
    private String thumbnailBigPath;
    //小缩略图
    private String thumbnailSmallPath;
    //图片、视频最后修改时间
    private long modifiedDate;
    //文件夹相关
    private String bucketId;
    //是否已被选中
    private boolean isChecked = true;

    public MediaItem() {

    }

    public MediaItem(long id, String mimeType, long size, long duration,
                     double latitude, double longitude, long width, long height, String path, long date, long addDate) {
        this.id = id;
        this.mimeType = mimeType == null ? "" : mimeType;
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
        this.length = size;
        this.duration = duration;
        this.latitude = latitude;
        this.longitude = longitude;
        createDate = date;
        originalPath = path;
        this.addDate = addDate;
        this.width = width;
        this.height = height;

    }

    public MediaItem(long id) {
        this.id = id;
    }

    public MediaItem(String path) {
        this.originalPath = path;
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
                DateTimeUtil.timeToMs(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))),
                DateTimeUtil.timeToMs(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED))));
    }


    public static MediaItem valueOfId(Cursor cursor) {
        return new MediaItem(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)));
    }

    public static MediaItem valueOfPath(Cursor cursor) {
        return new MediaItem(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
    }


    public Uri getContentUri() {
        return uri;
    }

    public boolean isCapture() {
        return id == ITEM_ID_CAPTURE;
    }

    public boolean isRecord() {
        return id == ITEM_ID_RECORD;
    }

    public boolean isImage() {
        if (mimeType == null) {
            return false;
        }
        return mimeType.equals(MimeType.JPEG.toString())
                || mimeType.equals(MimeType.PNG.toString())
                || mimeType.equals(MimeType.GIF.toString())
                || mimeType.equals(MimeType.BMP.toString())
                || mimeType.equals(MimeType.WEBP.toString());
    }

    public boolean isGif() {
        if (mimeType == null) {
            return false;
        }
        return mimeType.equals(MimeType.GIF.toString());
    }

    public boolean isVideo() {
        if (mimeType == null) {
            return false;
        }
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

        return originalPath != null ? originalPath.equals(item.originalPath) : item.originalPath == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (int) (length ^ (length >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (createDate ^ (createDate >>> 32));
        result = 31 * result + (int) (width ^ (width >>> 32));
        result = 31 * result + (int) (height ^ (height >>> 32));
        result = 31 * result + (originalPath != null ? originalPath.hashCode() : 0);
        result = 31 * result + (thumbnailBigPath != null ? thumbnailBigPath.hashCode() : 0);
        result = 31 * result + (thumbnailSmallPath != null ? thumbnailSmallPath.hashCode() : 0);
        result = 31 * result + (int) (modifiedDate ^ (modifiedDate >>> 32));
        result = 31 * result + (bucketId != null ? bucketId.hashCode() : 0);
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getAddDate() {
        return addDate;
    }

    public void setAddDate(long addDate) {
        this.addDate = addDate;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getThumbnailBigPath() {
        return thumbnailBigPath;
    }

    public void setThumbnailBigPath(String thumbnailBigPath) {
        this.thumbnailBigPath = thumbnailBigPath;
    }

    public String getThumbnailSmallPath() {
        return thumbnailSmallPath;
    }

    public void setThumbnailSmallPath(String thumbnailSmallPath) {
        this.thumbnailSmallPath = thumbnailSmallPath;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.mimeType);
        dest.writeParcelable(this.uri, flags);
        dest.writeLong(this.length);
        dest.writeLong(this.duration);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeLong(this.createDate);
        dest.writeLong(this.addDate);
        dest.writeLong(this.width);
        dest.writeLong(this.height);
        dest.writeString(this.originalPath);
        dest.writeString(this.thumbnailBigPath);
        dest.writeString(this.thumbnailSmallPath);
        dest.writeLong(this.modifiedDate);
        dest.writeString(this.bucketId);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    protected MediaItem(Parcel in) {
        this.id = in.readLong();
        this.mimeType = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.length = in.readLong();
        this.duration = in.readLong();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.createDate = in.readLong();
        this.addDate = in.readLong();
        this.width = in.readLong();
        this.height = in.readLong();
        this.originalPath = in.readString();
        this.thumbnailBigPath = in.readString();
        this.thumbnailSmallPath = in.readString();
        this.modifiedDate = in.readLong();
        this.bucketId = in.readString();
        this.isChecked = in.readByte() != 0;
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel source) {
            return new MediaItem(source);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    @Override
    public String toString() {
        return "MediaItem{" +
                "id=" + id +
                ", mimeType='" + mimeType + '\'' +
                ", uri=" + uri +
                ", length=" + length +
                ", duration=" + duration +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", createDate=" + createDate +
                ", addDate=" + addDate +
                ", width=" + width +
                ", height=" + height +
                ", originalPath='" + originalPath + '\'' +
                ", thumbnailBigPath='" + thumbnailBigPath + '\'' +
                ", thumbnailSmallPath='" + thumbnailSmallPath + '\'' +
                ", modifiedDate=" + modifiedDate +
                ", bucketId='" + bucketId + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
