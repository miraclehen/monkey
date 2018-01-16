package com.miraclehen.monkey.utils;

import android.media.ExifInterface;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author: hd
 * since: 2017/12/5
 */
public class ExifInterfaceProcessor {

    private String mFilePath;
    private ExifInterface mExifInterface;

    public ExifInterfaceProcessor(String path) {
        this.mFilePath = path;
        try {
            mExifInterface = new ExifInterface(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDateTime() {
        try {
            String dateTime = mExifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.CHINA);
            Date date = format.parse(dateTime);
            return String.valueOf(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public long getSize() {
        return mExifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
    }

    public double getLatitude() {
        String latValue = mExifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
        String latRef = mExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        if (TextUtils.isEmpty(latValue) || TextUtils.isEmpty(latRef)) {
            return 0D;
        } else {
            return convertRationalLatLonToFloat(latValue, latRef);
        }
    }

    public double getLongitude() {
        String longValue = mExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String longRef = mExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        if (TextUtils.isEmpty(longValue) || TextUtils.isEmpty(longRef)) {
            return 0D;
        } else {
            return convertRationalLatLonToFloat(longValue, longRef);
        }
    }

    public long getWidth() {
        return Long.parseLong(mExifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
    }

    public long getHeight() {
        return Long.parseLong(mExifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
    }

    private double convertRationalLatLonToFloat(String rationalString, String ref) {
        String[] parts = rationalString.split(",");
        String[] pair;
        pair = parts[0].split("/");
        double degrees = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());

        pair = parts[1].split("/");
        double minutes = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());

        pair = parts[2].split("/");
        double seconds = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());

        double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
        if (ref.equals("S") || ref.equals("W")) {
            return -result;
        }
        return result;
    }

}
