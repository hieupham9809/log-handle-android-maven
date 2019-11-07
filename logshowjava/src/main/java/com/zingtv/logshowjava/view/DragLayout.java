package com.zingtv.logshowjava.view;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zingtv.logshowjava.R;

public class DragLayout extends RelativeLayout {
    private LinearLayout mainView;
//    private LinearLayout tvWrapper;
//    private TextView logItemTv;
    private TextView modeTv;
//    private ScrollView scrollView;
    private RecyclerView recyclerView;
    private View scaleZone;

    private Context context;
    private int dx = 0;
    private int dy = 0;

    private int x = 0;
    private int y = 0;
    private boolean isInitView = true;
    private boolean isScaling = false;

    private float x_previous;
    private float y_previous;

    private int currentScroll = 0;

    private ViewDragHelper mDragHelper;

    ScaleWindowListener listener;

    public DragLayout(Context context) {
        super(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mainView = findViewById(R.id.main_view);
        scaleZone = findViewById(R.id.scale_zone);
        recyclerView = findViewById(R.id.recyclerview_log);
//        scrollView = findViewById(R.id.scroll_view);
//        tvWrapper = findViewById(R.id.tv_wrapper);
        modeTv = findViewById(R.id.priority_tv);
//        logItemTv = findViewById(R.id.log_item_tv);
//        tv = findViewById(R.id.tv);
//        tv.setTextIsSelectable(true);



    }

    public class DragHelperCallback extends ViewDragHelper.Callback {


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            requestLayout();
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view.getId() == R.id.main_view);
        }


        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {


            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mainView.getHeight() - mainView.getPaddingBottom();

            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {


            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - mainView.getWidth();

            return Math.min(Math.max(left, leftBound), rightBound);
        }
    }

    public boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0]
                && screenX < viewLocation[0] + view.getWidth()
                && screenY >= viewLocation[1]
                && screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        if (isViewHit(scaleZone, (int) ev.getX(), (int) ev.getY())) {

            isScaling = true;
//            if (scrollView.getScrollY() != 0) {
//                currentScroll = scrollView.getScrollY();
//            }

//            tvWrapper.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        }


        if (ev.getAction() == MotionEvent.ACTION_UP && isScaling) {
            isScaling = false;
//            scrollView.scrollTo(0, currentScroll);

        }
        if (isScaling) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                x_previous = ev.getRawX();
                y_previous = ev.getRawY();
            }
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {

                if (listener != null) {
                    listener.OnScale((int) (ev.getRawX() - x_previous), (int) (ev.getRawY() - y_previous));

                }
                x_previous = ev.getRawX();
                y_previous = ev.getRawY();
            }
            mDragHelper.shouldInterceptTouchEvent(ev);


            dx = (int) mDragHelper.getdX(ev);
            dy = (int) mDragHelper.getdY(ev);
            x = (int) mDragHelper.getX(ev);
            y = (int) mDragHelper.getY(ev);

            requestLayout();

            return true;
        }


        return isViewHit(recyclerView, (int) ev.getX(), (int) ev.getY());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

//        if (isViewHit(scaleZone, (int) ev.getX(), (int) ev.getY())) {
//            tvWrapper.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//
//        } else {
//            tvWrapper.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//        }
        if (isViewHit(mainView, (int) ev.getX(), (int) ev.getY()) || isViewHit(modeTv, (int) ev.getX(), (int) ev.getY())) {
            return mDragHelper.shouldInterceptTouchEvent(ev);
        } else {


            return false;
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (isInitView) {
            mainView.layout(
                    left,
                    top,
                    right - 100,
                    top + 100
            );


            recyclerView.layout(
                    mainView.getLeft(),
                    mainView.getBottom(),
                    mainView.getRight() + 100,
                    mainView.getBottom() + 350
            );

            isInitView = false;
        }
        if (dx != 0 || dy != 0) {

            mainView.layout(
                    left,
                    top,
                    right - 100,
                    top + 100
            );
            recyclerView.layout(
                    mainView.getLeft(),
                    mainView.getBottom(),
                    right,
                    bottom
            );
            dx = 0;
            dy = 0;
        }


        recyclerView.layout(
                mainView.getLeft(),
                mainView.getBottom(),
                right,
                bottom
        );

        scaleZone.layout(
                right - 100,
                top,
                right,
                top + 100
        );
    }

    public interface ScaleWindowListener {
        void OnScale(int dx, int dy);
    }

    public void setScaleWindowListener(ScaleWindowListener listener) {
        this.listener = listener;
    }
}
