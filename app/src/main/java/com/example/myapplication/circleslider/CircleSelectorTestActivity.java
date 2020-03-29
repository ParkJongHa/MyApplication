package com.example.myapplication.circleslider;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class CircleSelectorTestActivity extends AppCompatActivity {

    private TextView csta_tv;
    private CircleSelector circleSelector;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_selector_test_activity);

        circleSelector = findViewById(R.id.csta_cs);
        circleSelector.setOnSelectorListener(listener);

        csta_tv = findViewById(R.id.csta_tv);
    }

    private CircleSelector.OnSelectorListener listener = new CircleSelector.OnSelectorListener() {
        @Override public void onSelectorMove(int _value) {
            csta_tv.setText(String.valueOf(_value));
        }
    };

    public void onClick_setValue_739(View _view) {
        circleSelector.setValue(739);
    }

    public void onClick_getValue(View _view) {
        Toast.makeText(this, String.valueOf(circleSelector.getValue()), Toast.LENGTH_SHORT).show();
    }


}
