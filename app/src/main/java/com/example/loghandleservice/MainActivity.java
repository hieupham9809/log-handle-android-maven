package com.example.loghandleservice;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zingtv.logshowjava.parser.HtmlIParser;
import com.zingtv.logshowjava.parser.ZingTVHtmlParser;
import com.zingtv.logshowjava.service.FloatingLogViewService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zingtv.logshowjava.logconstant.LogConstant.CODE_DRAW_OVER_OTHER_APP_PERMISSION;
import static com.zingtv.logshowjava.logconstant.LogConstant.REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION;

public class MainActivity extends AppCompatActivity {
    String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    void initApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));

            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            HtmlIParser htmlIParser = new ZingTVHtmlParser();

            FloatingLogViewService.setHtmlParserAdapter(htmlIParser);
            FloatingLogViewService floatingLogViewService = new FloatingLogViewService();
            floatingLogViewService.startSelf(this, "/storage/emulated/0/Download/04-11-2019.html");

        }
    }

    boolean checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);

            return false;
        }
        return true;
    }

    AlertDialog showDialog2(String title, String msg, String positiveLabel,
                            DialogInterface.OnClickListener positiveOnClick,
                            String negativeLabel, DialogInterface.OnClickListener negativeOnClick,
                            boolean isCancelAble) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (checkAndRequestPermissions()) {
            initApp();
        }
//        if (isExternalStorageAvailable()) {
//
//            // Check whether this app has write external storage permission or not.
//            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            // If do not grant write external storage permission.
//            if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
//
//                // Request user to grant write external storage permission.
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
//            } else {
////                Log.d("ZINGLOGSHOW", "HERE");
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//
//
//                    //If the draw over permission is not available open the settings screen
//                    //to grant the permission.
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                            Uri.parse("package:" + getPackageName()));
//
//                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
//                } else {
//
//                    FloatingLogViewService.setHtmlParserAdapter(new ZingTVHtmlParser());
//                    FloatingLogViewService floatingLogViewService = new FloatingLogViewService();
//                    floatingLogViewService.startSelf(this, "/storage/emulated/0/Download/19-09-2019.html");
//
//
//
////                    handler.postDelayed(runnable, 2000);
//
//                }
//            }
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            initApp();
//
//            //Check if the permission is granted or not.
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
//                FloatingLogViewService.setHtmlParserAdapter(new ZingTVHtmlParser());
//                FloatingLogViewService floatingLogViewService = new FloatingLogViewService();
//                floatingLogViewService.startSelf(this, "/storage/emulated/0/Download/19-09-2019.html");
//            } else { //Permission is not available
//                Toast.makeText(this,
//                        "Draw over other app permission not available. Closing the application",
//                        Toast.LENGTH_SHORT).show();
//
//                finish();
//            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {

            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            if (deniedCount == 0) {
                initApp();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        showDialog2(""
                                , "this app needs " + permName + " to work"
                                , "Yes, grant"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        checkAndRequestPermissions();
                                    }
                                },
                                "No, Exit app",
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i){
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                    } else {
                        showDialog2(""
                                , "You have denied " + permName + " Allow all at Setting manually and open app again"
                                , "Goto Settings"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(),null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },
                                "No, Exit app",
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i){
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                        break;
                    }
                }
            }

        }
    }
}
