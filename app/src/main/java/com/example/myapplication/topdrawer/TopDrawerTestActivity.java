package com.example.myapplication.topdrawer;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class TopDrawerTestActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_drawer_test_activity);

        new TopDrawer(
            (ViewGroup) findViewById(R.id.tdta_ll_drawer_room)
            , (ViewGroup) findViewById(R.id.tdta_ll_drawer_knob)
            , true
        );
    }

}
