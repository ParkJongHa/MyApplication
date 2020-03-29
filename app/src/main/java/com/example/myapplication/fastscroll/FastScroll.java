package com.example.myapplication.fastscroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class FastScroll {

    private final FrameLayout fastScrollBarOuter;
    private final FrameLayout fastScrollBar;
    private final RecyclerView recyclerView;

    private Context getContext() {
        return fastScrollBarOuter.getContext();
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    @SuppressLint("InflateParams")
    public FastScroll(FrameLayout _fastScrollBarOuter, RecyclerView _recyclerView) {
        fastScrollBarOuter = _fastScrollBarOuter;
        recyclerView = _recyclerView;

        fastScrollBar = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.fast_scroll_bar, null);
        fastScrollBarOuter.addView(fastScrollBar);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fastScrollBar.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getContext().getResources().getDisplayMetrics());
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getContext().getResources().getDisplayMetrics());
        params.gravity = Gravity.END;
        fastScrollBar.setLayoutParams(params);
        fastScrollBar.setVisibility(View.GONE);

        init();
    }

    private TouchState touchState = TouchState.NO_TOUCH;

    enum TouchState {
        TOUCHING,
        NO_TOUCH
    }

    private int positionDeltaY;

    @SuppressLint("ClickableViewAccessibility")
    private void init () {
        fastScrollBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        touchState = TouchState.TOUCHING;
                        positionDeltaY = Y - ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchState = TouchState.TOUCHING;
                        FastScroll.this.setFastScrollBarTopMargin(Y - positionDeltaY);

                        double percent = FastScroll.this.getPercent();
                        int itemPosition = FastScroll.this.getItemPosition(percent);

                        FastScroll.this.setRecyclerViewItemPosition(itemPosition);
                        break;
                    case MotionEvent.ACTION_UP:
                        touchState = TouchState.NO_TOUCH;
                        fastScrollBar.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });
    }

    private void setFastScrollBarTopMargin(int _topMargin) {
        if ( ! (fastScrollBar.getLayoutParams() instanceof ViewGroup.MarginLayoutParams))
            return;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fastScrollBar.getLayoutParams();
        params.topMargin = getFastScrollBarValidTopMargin(_topMargin);
        fastScrollBar.setLayoutParams(params);
    }

    private int getItemPosition(double _percent) {
        if (null == recyclerView.getAdapter()) return 0;
        return (int)(recyclerView.getAdapter().getItemCount() * _percent / 100d);
    }

    private Handler scrollEndhandler = new Handler();
    private Runnable scrollEndRunnable;

    private void setRecyclerViewItemPosition(int _itemPosition) {
        if (null!=recyclerView.getAdapter()
        && _itemPosition == recyclerView.getAdapter().getItemCount()-1) {
            if (null != scrollEndRunnable) scrollEndhandler.removeCallbacks(scrollEndRunnable);
            scrollEndRunnable = new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }
            };
            scrollEndhandler.postDelayed(scrollEndRunnable, 250);
            return;
        } else {
            if (null != scrollEndRunnable) scrollEndhandler.removeCallbacks(scrollEndRunnable);
        }

        recyclerView.scrollToPosition(_itemPosition);
    }

    private int getFastScrollBarValidTopMargin(int _rawTopMargin) {
        if (_rawTopMargin < 0) return 0;

        int moveableDistance =
                ((ViewGroup)fastScrollBar.getParent()).getHeight()
                        - fastScrollBar.getHeight();

        return Math.min(moveableDistance, _rawTopMargin);
    }

    private int getFastScrollBarTopMargin() {
        if ( !(fastScrollBar.getLayoutParams() instanceof ViewGroup.MarginLayoutParams))
            return 0;

        return ((ViewGroup.MarginLayoutParams) fastScrollBar.getLayoutParams()).topMargin;
    }

    private double getPercent() {
        double percent =
            (double) getFastScrollBarTopMargin()
            * 100d
            / (double) (fastScrollBarOuter.getHeight() - fastScrollBar.getHeight()); // total move able distance;

        return getValidPercent(percent);
    }

    private int getTopMargin(double percent) {
        double totalMoveAbleDistance = fastScrollBarOuter.getHeight() - fastScrollBar.getHeight(); // total move able distance;
        double topMargin = totalMoveAbleDistance * getValidPercent(percent) / 100d;

        return (int)topMargin;
    }

    private double getValidPercent(double percent) {
        if (percent < 0) return 0d;
        else if (100 < percent) return 100d;
        return percent;
    }

    void onScrollStateDragging(){  // scroll start
        fastScrollBar.setVisibility(View.VISIBLE);
    }

    void onScrollStateIdle(){ // scroll end
        if (TouchState.NO_TOUCH == touchState) fastScrollBar.setVisibility(View.GONE);
    }

    void onScrolling(){ // ing
        if (TouchState.TOUCHING == touchState || null == recyclerView.getAdapter()) return;

        int itemCount = recyclerView.getAdapter().getItemCount();
        int itemCountPerScreen = getLinearLayoutManager().findLastVisibleItemPosition()
                        - getLinearLayoutManager().findFirstVisibleItemPosition();
        double percent = getLinearLayoutManager().findFirstVisibleItemPosition() * 100d / (itemCount - itemCountPerScreen);

        setFastScrollBarTopMargin(getTopMargin(percent));
    }

}
