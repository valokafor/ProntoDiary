package com.okason.diary.utils;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.utils.date.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * @author Valentine
 *
 * Utilities for performing various operations on client information.
 */
public class FileUtility {

    public static File getattachmentFileName(String format) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Constants.ATTACHMENTS_FOLDER), "");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File recordFile = new File(folder, TimeUtils.getDatetimeSuffix(System.currentTimeMillis()) + format);
        return recordFile;

    }

    public static File createImageFile(String extension) throws IOException {
        // Create an image file name
        String timeStamp = TimeUtils.getDatetimeSuffix(System.currentTimeMillis());
        String imageFileName = "Image_" + timeStamp + "_";
        File storageDir = ProntoDiaryApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                extension,         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


//    /**
//     * Creates the image file to which the image must be saved.
//     * @return
//     * @throws IOException
//     * @param mimeType
//     */
//    public static File createImageFile(Context context, String mimeType) throws IOException {
//        // Create an image file name
//        String timeStamp = TimeUtils.getDatetimeSuffix(System.currentTimeMillis());
//        String imageFileName = "Image_" + timeStamp + "_";
//        File storageDir = Context.getP
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                Constants.MIME_TYPE_IMAGE_EXT,         /* suffix */
//                storageDir      /* directory */
//        );
//        return image;
//    }


    /**
     * Empty constructor to prevent instantiation.
     */
    protected FileUtility() {}

    public static String getPreviousDatabase(Context context) {
        return context.getCacheDir() + File.separator + "prev.db";
    }

    public static String getTemporaryDatabase(Context context) {
        return context.getCacheDir() + File.separator + "temp.db";
    }

    public static String getDeviceId(Context context) {
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }




    /**
     * Copies a file byte for byte.
     *
     * @param fromFile FileInputStream for the file to copy from.
     * @param toFile FileInputStream for the file to copy to.
     */
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile)
            throws IOException {

        FileChannel fromChannel = null;
        FileChannel toChannel = null;

        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    public static Date getLastModifyTime(File file) {
        return new Date(file.lastModified());
    }


    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }












}
