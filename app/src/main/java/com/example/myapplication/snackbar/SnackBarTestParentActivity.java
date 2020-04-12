package com.example.myapplication.snackbar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@SuppressLint("Registered")
public class SnackBarTestParentActivity extends AppCompatActivity {

    private ArrayList<Snack> snackQueueList;
    private Disposable snackDisposable;
    private CardView snackView;
    private int snackAnimDistance = Integer.MIN_VALUE;
    private final long snackCheckPeriod = 100L;
    private ObjectAnimator snackAnimation;

    private void isFinishAShack() {
        if (null==snackQueueList || 1>snackQueueList.size()) {
            if (null != snackDisposable) {
                snackDisposable.dispose();
                snackDisposable = null;
            }
            if (null != snackView) {
                snackView.setVisibility(View.INVISIBLE);
            }
            return;
        }

        final Snack snack = snackQueueList.get(0);

        if (Snack.Status.PENDING == snack.getStatus()) return;
        if (Snack.Status.FINSHED == snack.getStatus()) {
            snackQueueList.remove(snack);
            return;
        }

        if (0 >= snack.getRemainShowTime() && Snack.Status.SHOWING == snack.getStatus()) {
            snack.setStatus(Snack.Status.FINISHING);

            snackAnimation = ObjectAnimator.ofFloat(snackView, "y", -1*snackAnimDistance);
            snackAnimation.setDuration(250);
            snackAnimation.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
                @Override public void onAnimationStart(Animator animation) {}
                @Override public void onAnimationEnd(Animator animation) {
                    snack.setStatus(Snack.Status.FINSHED);
                    snackQueueList.remove(snack);
                    snackView.setVisibility(View.INVISIBLE);
                    startASnack();
                }
            });
            snackAnimation.start();
            return;
        }

        if (Snack.Status.SHOWING == snack.getStatus()) {
            ((TextView)snackView.findViewById(R.id.s_tv_remain)).setText(String.valueOf((1000+snack.getRemainShowTime())/1000));
            snack.setRemainShowTime(snack.getRemainShowTime() - snackCheckPeriod);
        }
    }

    public void snackbar(Snack _snack) {
        if (null == snackQueueList) snackQueueList = new ArrayList<>();

        if (0 < snackQueueList.size()) {
            if (Snack.Priority.Strong == _snack.getPriority()
            && Snack.Priority.Strong == snackQueueList.get(0).getPriority()) {
                return;
            } else if (Snack.Priority.Strong == _snack.getPriority()) {
                snackQueueList.add(1, _snack);
            } else {
                snackQueueList.add(_snack);
            }
        } else {
            snackQueueList.add(_snack);
        }

        if (null == snackView) {
            snackView = findViewById(R.id.s_cv);
        }

        if (Integer.MIN_VALUE == snackAnimDistance) {
            snackAnimDistance = snackView.getHeight() * 3 / 4;
        }

        if (null == snackDisposable)
            snackDisposable = Observable
                .interval(snackCheckPeriod, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override public void accept(Long aLong) {
                        SnackBarTestParentActivity.this.isFinishAShack();
                    }
                });

        startASnack();
    }

    private void startASnack() {
        if (null == snackView
                || null == snackQueueList || 1 > snackQueueList.size()
                || View.VISIBLE == snackView.getVisibility()) {
            return;
        }

        Snack snack = snackQueueList.get(0);

        if (Snack.Status.PENDING != snack.getStatus()) return;

        snackView.setVisibility(View.VISIBLE);
        snack.setStatus(Snack.Status.SHOWING);

        // setText
        ((TextView)snackView.findViewById(R.id.s_tv)).setText(snack.getMessage());

        // setEvent
        if (null == snack.getOnClickListener()) {
            snackView.setOnClickListener(null);
        } else {
            snackView.setOnClickListener(snack.getOnClickListener());
        }

        // setColor
        snackView.setCardBackgroundColor(snack.getColor());

        snackAnimation = ObjectAnimator.ofFloat(snackView, "y", snackAnimDistance);
        snackAnimation.setDuration(250);
        snackAnimation.start();
    }



}
