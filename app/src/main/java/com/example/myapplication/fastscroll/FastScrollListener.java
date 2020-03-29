package com.example.myapplication.fastscroll;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FastScrollListener extends RecyclerView.OnScrollListener {

    private final FastScroll fastScroll;

    public FastScrollListener(FastScroll _fastScroll) {
        fastScroll = _fastScroll;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (null!=recyclerView.getAdapter() && 100<recyclerView.getAdapter().getItemCount()) {
            if (RecyclerView.SCROLL_STATE_DRAGGING == newState) fastScroll.onScrollStateDragging();
            else if (RecyclerView.SCROLL_STATE_IDLE == newState) fastScroll.onScrollStateIdle();
        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (null!=recyclerView.getAdapter() && 100<recyclerView.getAdapter().getItemCount()) {
            fastScroll.onScrolling();
        }
    }

}
