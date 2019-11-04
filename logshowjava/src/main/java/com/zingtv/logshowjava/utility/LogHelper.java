package com.zingtv.logshowjava.utility;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogHelper {
    private static WriteNewLogListener mListener;

    public static File getLogsFile(Context context) {
        String fileNameTimeStamp = new SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).format(new Date());
        String fileName = fileNameTimeStamp + ".html";

        // Create file
        return generateFile(context, fileName);
    }

    /*  Helper method to create file*/
    @Nullable
    private static File generateFile(Context context, @NonNull String fileName) {
        File file = null;
        if (isExternalStorageAvailable()) {
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/showlog";

            String path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/showlog";
            Log.d("ZINGLOGSHOW", path);
            File root = new File(path);

            boolean dirExists = true;

            if (!root.exists()) {
                dirExists = root.mkdirs();
            }

            if (dirExists) {
                file = new File(root, fileName);
            }
        }
        return file;
    }

    public static boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public static void writeNewLogTrigger(String path){
        if (mListener != null){
            Log.d("ZINGLOGSHOW", "listener not null");
            mListener.onWriteNewLog(path);
        } else {
            Log.d("ZINGLOGSHOW", "listener null");

        }
    }
    public interface WriteNewLogListener {
        void onWriteNewLog(String path);
    }

    public static void setWriteNewLogListener(WriteNewLogListener listener){
        Log.d("ZINGLOGSHOW", "set listener");
        mListener = listener;
    }
    public static void unSetWriteNewLogListener(){
        mListener = null;
    }
    public static boolean hasListener(){
        return mListener != null;
    }
}
