package com.zingtv.logshowjava.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by hieupm@vng.com.vn on 18,November,2019
 */
public class SlowRecyclerView extends RecyclerView {

    public SlowRecyclerView(@NonNull Context context) {
        super(context);
    }

    public SlowRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean fling(int velocityX, int velocityY){
        int newVelocityY = velocityY ;
        return super.fling(velocityX, newVelocityY);
    }
}
