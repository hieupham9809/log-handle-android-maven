package com.example.loghandleservice;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zingtv.logshowjava.parser.HtmlIParser;
import com.zingtv.logshowjava.parser.ZingTVHtmlParser;
import com.zingtv.logshowjava.service.FloatingLogViewService;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.zingtv.logshowjava.logconstant.LogConstant.CODE_DRAW_OVER_OTHER_APP_PERMISSION;
import static com.zingtv.logshowjava.logconstant.LogConstant.REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION;

public class MainActivity extends AppCompatActivity {
    Button button;
    Button startBtn;
    int counter = 0;

    String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    void initApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            showDialog2(""
                    , "this module needs DRAW_OVER_APP_PERMISSION to work"
                    , "Yes, grant"
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));

                            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                        }
                    },
                    "No",
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            dialogInterface.dismiss();

                        }
                    }, false);

        } else {
            HtmlIParser htmlIParser = new ZingTVHtmlParser();

            FloatingLogViewService.setHtmlParserAdapter(htmlIParser);

            FloatingLogViewService floatingLogViewService = new FloatingLogViewService();
            floatingLogViewService.startSelf(this, "/storage/emulated/0/Download/04-11-2019.html");


        }
//        HtmlIParser htmlIParser = new ZingTVHtmlParser();
//
//        FloatingLogViewService.setHtmlParserAdapter(htmlIParser);
//        FloatingLogViewService floatingLogViewService = new FloatingLogViewService(this);
//        floatingLogViewService.startSelf(this, "/storage/emulated/0/Download/04-11-2019.html");
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

        Log.d("ZINGLOGSHOW", "path "+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        button = findViewById(R.id.button);
        startBtn = findViewById(R.id.startBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        Timber.e("Log time %d", counter);
                String logTimeStamp = new SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                        Locale.getDefault()).format(new Date());
                String tag = "TEST";
                String message = "test"+counter;
                try {
                    FileWriter writer = new FileWriter("/storage/emulated/0/Download/04-11-2019.html", true);
                    writer.append("<p priority=\"").append(String.valueOf(2))
                            .append("\" style=\"background:lightgray;\"><strong ").append("style=\"background:lightblue;\">&nbsp&nbsp")
                            .append(logTimeStamp)
                            .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                            .append(tag)
                            .append("</strong> - ")
                            .append(message)
                            .append("</p>");
                    writer.flush();
                    writer.close();
                    counter++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                counter++;
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    Log.d("ZINGLOGSHOW", "init app");
                    initApp();
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            initApp();

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
