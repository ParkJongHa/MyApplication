package com.example.myapplication.fastscroll;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class FastScrollTestActivity extends AppCompatActivity {

    private List<Integer> integerList = new ArrayList<>();

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fast_scroll_test_activity);

        FrameLayout fsta_fl_outer = findViewById(R.id.fsta_fl_outer);
        RecyclerView fsta_rv = findViewById(R.id.fsta_rv);

        for (int i=0; i<10000; i++) integerList.add(i);

        fsta_rv.setAdapter(new Adapter());
        fsta_rv.setLayoutManager(new LinearLayoutManager(this));


        fsta_rv.addOnScrollListener(new FastScrollListener(new FastScroll(fsta_fl_outer, fsta_rv)));
    }



    private class Adapter extends RecyclerView.Adapter<AdapterViewHolder> {

        @Override public int getItemCount() {
            return integerList.size();
        }

        @NonNull
        @Override public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AdapterViewHolder(
                LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fast_scroll_item, parent, false)
            );
        }

        @SuppressLint("InflateParams")
        @Override public void onBindViewHolder(AdapterViewHolder _holder, int _position) {
            _holder.fsta_tv.setText(String.valueOf(integerList.get(_position)));
        }

    }

    private static class AdapterViewHolder extends RecyclerView.ViewHolder {

        TextView fsta_tv;

        AdapterViewHolder(View itemView) {
            super(itemView);

            fsta_tv = itemView.findViewById(R.id.fsta_tv);
        }
    }



}
