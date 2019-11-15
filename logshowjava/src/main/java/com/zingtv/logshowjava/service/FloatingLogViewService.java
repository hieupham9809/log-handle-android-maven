package com.zingtv.logshowjava.service;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.zingtv.logshowjava.R;

import com.zingtv.logshowjava.parser.HtmlIParser;
import com.zingtv.logshowjava.view.DragLayout;
import com.zingtv.logshowjava.view.EndlessRecyclerViewScrollListener;
import com.zingtv.logshowjava.view.LogItemAdapter;

import java.io.File;
import java.io.FileInputStream;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.zingtv.logshowjava.logconstant.LogConstant.BUFFER_SIZE;
import static com.zingtv.logshowjava.logconstant.LogConstant.MIN_HEIGHT_SIZE;
import static com.zingtv.logshowjava.logconstant.LogConstant.MIN_WIDTH_SIZE;

public class FloatingLogViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private DragLayout expandedView;
    private HashMap<String, String> priorityHM;


    //    private LinearLayout tvWrapper;
//    private TextView logTextView;
    private TextView priorityTextView;
//    private ScrollView scrollView;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LogItemAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private Context mContext;

    private EditText filterEditText;
    private Spinner prioritySpinner;
    private CustomArrayAdapter arrayAdapterSpinner;

    private ImageButton loadBtn;
    private Handler loadWebViewHandler;
    private Runnable loadWebViewRunnable;

    private String path = "";
    private String fileName = "";
    private int TYPE_FLAG;

    private int old_width = 0;
    private int old_height = 0;
    private int old_x = 0;
    private int old_y = 0;

    private int previousNumTag = 0;
    private String previousFilter = "";
    private String previousPriority = "";

    private FileObserver fileObserver;
    private String content = "";
    private String[] listPTag;

    int screenWidth;

    private boolean isWatching = true;
    private boolean isMoving = false;
    WindowManager.LayoutParams params;

    public static HtmlIParser htmlParser;

    private final long DELAY_FILTER = 1000;

    private int currentFileIndex = 0;

    public FloatingLogViewService() {
    }

    public int startSelf(Context context, String path) {

        Intent intent = new Intent(context, FloatingLogViewService.class);
        /* Send path, for example: /storage/emulated/0/Android/data/com.example.logshowjava/files/Documents/showlog/30-09-2019.html */
        intent.putExtra("path", path);
        mContext = context;
        context.startService(intent);
        return 0;
    }

    public static void setHtmlParserAdapter(HtmlIParser newHtmlParser) {
        htmlParser = newHtmlParser;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null) {
            path = intent.getStringExtra("path");

        }

        String[] pathArray = path.split("/");
        fileName = pathArray[pathArray.length - 1];
        String parentDic = TextUtils.join("/", Arrays.copyOfRange(pathArray, 0, pathArray.length - 1)) + "/";

        if (fileObserver == null) {
            fileObserver = new FileObserver(parentDic) {
                @Override
                public void onEvent(int event, String path) {

                    if (path == null || !path.equals(fileName)) {
                        return;
                    }

                    if (event == FileObserver.MODIFY) {

                        if (!isViewCollapsed() && recyclerView != null) {
//                        loadWebViewHandler.post(loadWebViewRunnable);
                            loadLogToWindow("true");
                            Log.d("ZINGLOGSHOW", "File changed, load again");

                        }
                    }
                }
            };
            fileObserver.startWatching();
            isWatching = true;
        }

        return START_STICKY;
    }


    @Override
    public void onCreate() {

        loadWebViewHandler = new Handler();
        loadWebViewRunnable = new Runnable() {
            @Override
            public void run() {
                loadLogToWindow("true");
            }
        };
        super.onCreate();

        priorityHM = new HashMap<>();
        priorityHM.put("Verbose", "2");
        priorityHM.put("Debug", "3");
        priorityHM.put("Info", "4");
        priorityHM.put("Warn", "5");
        priorityHM.put("Error", "6");
        priorityHM.put("Assert", "7");

        //Inflate the floating view layout we created

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_log_widget, null);
        mFloatingView.setFocusableInTouchMode(true);
        mFloatingView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        closeExpandViewAction();

                        Log.d("ZINGLOGSHOW", "back key touched");
                        return true;
                    }

                }
                return false;
            }
        });
        priorityTextView = mFloatingView.findViewById(R.id.priority_tv);
        prioritySpinner = mFloatingView.findViewById(R.id.priority_spinner);
        arrayAdapterSpinner = new CustomArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, priorityHM.keySet().toArray(new String[6]));

        arrayAdapterSpinner.setOnSelectItemListener(new CustomArrayAdapter.OnSelectItemListener() {
            @Override
            public void onSelected(String priority) {
                hideSpinnerDropDown(prioritySpinner);
                prioritySpinner.setSelection(arrayAdapterSpinner.getPosition(priority));

                loadLogToWindow("true");
                priorityTextView.setText(prioritySpinner.getSelectedItem().toString());
            }
        });


        prioritySpinner.setAdapter(arrayAdapterSpinner);
        prioritySpinner.setSelection(arrayAdapterSpinner.getPosition("Verbose"));
        priorityTextView.setText(prioritySpinner.getSelectedItem().toString());

        progressBar = mFloatingView.findViewById(R.id.progress_bar);
        recyclerView = mFloatingView.findViewById(R.id.recyclerview_log);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(mContext);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new LogItemAdapter(this);
        recyclerView.setAdapter(mAdapter);
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore() {
                loadLogToWindow("false");
                Log.d("ZINGLOGSHOW", "onLoadMore");

            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);


        filterEditText = mFloatingView.findViewById(R.id.search_et);

        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                loadWebViewHandler.removeCallbacks(loadWebViewRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                loadLogToWindow();
                loadWebViewHandler.postDelayed(loadWebViewRunnable, DELAY_FILTER);
            }
        });
        loadBtn = mFloatingView.findViewById(R.id.load_btn);

        if (isWatching) {
            loadBtn.setImageResource(R.drawable.media_stop);
        } else {
            loadBtn.setImageResource(R.drawable.media_play);
        }
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWatching) {

                    Log.d("ZINGLOGSHOW", "unset listener");
                    loadBtn.setImageResource(R.drawable.media_play);
                    if (fileObserver != null) {
                        fileObserver.stopWatching();
                    } else {
                        Log.d("ZINGLOGSHOW", "fileObserver is null");
                    }
                    isWatching = false;
                } else {
                    if (!TextUtils.isEmpty(path)) {
                        Log.d("ZINGLOGSHOW", "log file available, load html " + path);

                        loadLogToWindow("true");

                        if (fileObserver != null) {
                            fileObserver.startWatching();
                        } else {
                            Log.d("ZINGLOGSHOW", "fileObserver is null");
                        }
                        isWatching = true;
                        loadBtn.setImageResource(R.drawable.media_stop);
                    } else {
                        Log.d("ZINGLOGSHOW", "log file null");
                    }
                }
            }
        });

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TYPE_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            TYPE_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                TYPE_FLAG,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                ,
                PixelFormat.TRANSLUCENT);


        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;


        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //The root element of the collapsed view layout
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        collapsedView.setVisibility(View.VISIBLE);
        //The root element of the expanded view layout
        expandedView = mFloatingView.findViewById(R.id.drag_layout);
        expandedView.setVisibility(View.GONE);


        expandedView.setScaleWindowListener(new DragLayout.ScaleWindowListener() {
            @Override
            public void OnScale(int dx, int dy) {

                params.width = (params.width + dx < MIN_WIDTH_SIZE && dx < 0) ? MIN_WIDTH_SIZE : params.width + dx;
                params.height = (params.height - dy < MIN_HEIGHT_SIZE && dy > 0) ? MIN_HEIGHT_SIZE : params.height - dy;

                old_width = params.width;
                old_height = params.height;

                if (params.width <= MIN_WIDTH_SIZE) {
                    filterEditText.setVisibility(View.GONE);
                } else {
                    filterEditText.setVisibility(View.VISIBLE);
                }
                mWindowManager.updateViewLayout(mFloatingView, params);

            }
        });


        //Set the close ImageButton
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
            }
        });
        //Set the close button
        ImageView closeButtonDrag = (ImageView) mFloatingView.findViewById(R.id.close_button_at_drag);
        closeButtonDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeExpandViewAction();
            }
        });
        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return isViewCollapsed();
                    case MotionEvent.ACTION_MOVE:

                        isMoving = true;
                        //Calculate the X and Y coordinates of the view.

                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (isMoving && collapsedView.getVisibility() == View.VISIBLE) {
                            if (event.getRawX() > screenWidth / 2) {
                                params.x = screenWidth - collapsedView.getWidth() / 2;
                            } else {
                                params.x = 0;
                            }
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            isMoving = false;
                            return true;
                        }
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
//                                expandedView.setFocusable(true);
//                                expandedView.setKeyBackListener(new DragLayout.KeyBackListener() {
//                                    @Override
//                                    public void OnKeyBack() {
//                                        closeExpandViewAction();
//                                    }
//                                });


//                                if (mAdapter.getItemCount() == 0) {
                                loadLogToWindow("true");
//                                }

                                if (old_height == 0 || old_width == 0) {
                                    params.width = 600;
                                    params.height = 450;
                                } else {
                                    params.width = old_width;
                                    params.height = old_height;
                                }
                                if (old_x != 0 || old_y != 0) {
                                    params.x = old_x;
                                    params.y = old_y;
                                }
//
                                params.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                                mWindowManager.updateViewLayout(mFloatingView, params);

                            }
                        }


                        return isViewCollapsed();
                }
                return false;
            }
        });

    }


    public void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class LoadLogAsyncTask extends AsyncTask<String, Integer, List<Spanned>> {
        boolean isScrollToEnd = false;

        @Override
        protected void onPreExecute() {
            if (progressBar.getVisibility() != View.VISIBLE) {
//                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);

            }
            super.onPreExecute();
        }

        @Override
        protected List<Spanned> doInBackground(String... value) {
            if (value[3].equals("true")) { // load new file and not for scrolling, reset currentFileIndex to 0
                currentFileIndex = 0;
                isScrollToEnd = true;
            }
            FileInputStream fis;
//            String content = "";
//            Spanned spannedContent;
            List<Spanned> spannedList = new ArrayList<>();


            try {
//                Log.i("FLoatingLogView", "doInBackground: " + Thread.currentThread().toString());
                if (value[3].equals("true")) {
                    Log.d("ZINGLOGSHOW", "load file again");

                    fis = new FileInputStream(new File(value[0]));

                    int size = fis.available();

                    byte[] buffer = new byte[size];
                    while (fis.read(buffer) != -1) {

                    }
                    listPTag = new String(buffer).split("</p>");
                    if (listPTag.length > previousNumTag) {
                        previousNumTag = listPTag.length;

                    } else {
                        if (!previousFilter.equals(value[1]) || !previousPriority.equals(value[2])) {
                            previousFilter = value[1];
                            previousPriority = value[2];
                            Log.d("ZINGLOGSHOW", "first time repeat");
                        } else {
                            Log.d("ZINGLOGSHOW", "repeat list, not update UI");
                            currentFileIndex = previousNumTag;

                            endlessRecyclerViewScrollListener.setLoading(false);

                            return null;
                        }
                    }

                    currentFileIndex = listPTag.length;
//                    Log.d("ZINGLOGSHOW", "reset currentFileIndex = " + currentFileIndex);

                }

                int progress = 2;
                int sum = currentFileIndex;

                if (htmlParser != null) {
                    Log.d("ZINGLOGSHOW", "current file index " + currentFileIndex);

                    while (currentFileIndex > 0 && spannedList.size() == 0) {
                        if (currentFileIndex - BUFFER_SIZE < 0) {
                            spannedList = htmlParser.read(TextUtils.join("</p>", Arrays.copyOfRange(listPTag, 0, currentFileIndex)), value[1], value[2]);
                            currentFileIndex = 0;
                            Log.d("ZINGLOGSHOW", "reach top of file");

                        } else {
                            spannedList = htmlParser.read(TextUtils.join("</p>", Arrays.copyOfRange(listPTag, currentFileIndex - BUFFER_SIZE, currentFileIndex)), value[1], value[2]);
                            currentFileIndex -= BUFFER_SIZE;
                            Log.d("ZINGLOGSHOW", "load more");

                        }

                        progress = (int) ((sum - currentFileIndex) * 1f / sum * 100);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBar.setProgress(progress, true);
                        } else {
                            progressBar.setProgress(progress);
                        }
                        Log.d("ZINGLOGSHOW", "progress " + progress);

                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(100, true);
                    } else {
                        progressBar.setProgress(100);
                    }
//                    spannedContent = filterLog(content, value[1],value[2]);
//                    filterEditText.getText().toString().trim()
//                    priorityHM.get(prioritySpinner.getSelectedItem().toString())
                } else {
                    Log.d("ZINGLOGSHOW", "html Parser null");

                    throw new Exception("Need to implement and set HTML Parser");
                }
//                logTextView.setText(spannedContent);
//                sum+=spannedList.size();
                Log.d("ZINGLOGSHOW", "spannedList length " + spannedList.size());


                return spannedList;


            } catch (Exception e) {
                Log.d("ZINGLOGSHOW", "error reading file " + e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Spanned> result) {
//            Log.d("FLoatingLogView", "notify" );
//            logTextView.setText(result);
            // specify an adapter (see also next example)

            progressBar.setVisibility(View.INVISIBLE);
            if (result != null) {

                if (isScrollToEnd) {
                    mAdapter.clear();

                    mAdapter.setLog(result);
                    mAdapter.notifyDataSetChanged();

                    recyclerView.scrollToPosition(result.size() - 1);
                } else {
//                    Log.d("ZINGLOGSHOW", "result length " + result.size());
//                if (result.size() == 0 && currentFileIndex - BUFFER_SIZE < 0){
//                    loadLogToWindow("false");
//                }
                    mAdapter.insertLog(result);
                    mAdapter.notifyItemRangeInserted(0, result.size());
                }
            }


        }
    }

    private void closeExpandViewAction() {
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        old_x = params.x;
        old_y = params.y;
        if (params.x > screenWidth / 2) {
            params.x = screenWidth - collapsedView.getWidth() / 2;
        } else {
            params.x = 0;
        }
        collapsedView.setVisibility(View.VISIBLE);
        expandedView.setVisibility(View.GONE);

        params.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.updateViewLayout(mFloatingView, params);
    }

    public void loadLogToWindow(String isLoadFileChange) {
        new LoadLogAsyncTask().execute(path, filterEditText.getText().toString().trim(), priorityHM.get(prioritySpinner.getSelectedItem().toString()), isLoadFileChange);

    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fileObserver != null) {
            fileObserver.stopWatching();
        } else {
            Log.d("ZINGLOGSHOW", "fileObserver is null");
        }
        isWatching = false;
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
