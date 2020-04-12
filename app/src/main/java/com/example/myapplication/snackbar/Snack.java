package com.example.myapplication.snackbar;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class Snack {

    private long remainShowTime = 3000; //
    private String message;
    private View.OnClickListener onClickListener = null;
    private Status status = Status.PENDING;
    private int color = Color.parseColor("#61AAFB");
    private Priority priority = Priority.Weak;

    public enum Status {
        PENDING,
        SHOWING,
        FINISHING,
        FINSHED
    }

    public enum Priority {
        Strong,
        Weak
    }

    public Priority getPriority() {
        return priority;
    }

    public Snack setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Snack setColor(int color) {
        this.color = color;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Snack setStatus(Status status) {
        this.status = status;
        return this;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public Snack setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public long getRemainShowTime() {
        return remainShowTime;
    }

    public Snack setRemainShowTime(long remainShowTime) {
        this.remainShowTime = remainShowTime;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Snack setMessage(String message) {
        this.message = message;
        return this;
    }

    public Snack setMessage(Context _context, int _stringId) {
        setMessage(_context.getString(_stringId));
        return this;
    }

}
