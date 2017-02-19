package com.infiniteexpandablelistview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by Keval on 16-Feb-17.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class SecondLevelExpandableListView extends ExpandableListView {


    public SecondLevelExpandableListView(Context context) {
        super(context);
    }

    public SecondLevelExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecondLevelExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SecondLevelExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //999999 is a size in pixels. ExpandableListView requires a maximum height in order to do measurement calculations.
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}