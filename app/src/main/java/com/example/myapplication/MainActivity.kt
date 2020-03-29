package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myapplication.circleslider.CircleSelectorTestActivity
import com.example.myapplication.fastscroll.FastScrollTestActivity
import com.example.myapplication.topdrawer.TopDrawerTestActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick_topDrawer(ignore: View) {
        startActivity(Intent(this, TopDrawerTestActivity::class.java))
    }

    fun onClick_circleSelector(ignore: View) {
        startActivity(Intent(this, CircleSelectorTestActivity::class.java))
    }

    fun onClick_fastScroll(ignore: View) {
        startActivity(Intent(this, FastScrollTestActivity::class.java))
    }

}
