package com.example.myapplication.snackbar;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class SnackBarTestActivity extends SnackBarTestParentActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snack_bar_test_activity);
    }

    public void onClick_snackbar(View _view) {
        snackbar(new Snack().setMessage("Hello"));
    }

}
