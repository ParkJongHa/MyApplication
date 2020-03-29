package com.example.myapplication.topdrawer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;


/**
 * Drawer 로 동작하려면 다음과 같은 레이아웃을 구성해야 함
 *
 * ---------------
 * |
 * |    ----------
 * |    |
 * |    |   room 공간 : touch 이벤트에 따른 topMargin 이 설정 되어야 할 부분
 * |    |
 * |    ----------
 * |
 * |    ----------
 * |    |
 * |    |   knob 손잡이 : touch 이벤트가 설정 되어야 될 부분
 * |    |
 * |    ----------
 * |
 * ----------------
 */
public class TopDrawer {

    private final ViewGroup room;
    private final ViewGroup knob;
    private final boolean snap;

    private final long ANIMATION_COUNT = 10;

    public TopDrawer(ViewGroup _room, ViewGroup _knob, boolean _snap) {
        room = _room;
        knob = _knob;
        snap = _snap;
        init();
    }

    private int positionDeltaY;

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        knob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != snapAnimThread) return true; // animation 중이면 이벤트 block
                final int Y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        positionDeltaY = Y - ((ViewGroup.MarginLayoutParams) room.getLayoutParams()).topMargin;
                        TopDrawer.this.touchOn();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        TopDrawer.this.setRoomTopMargin(Y - positionDeltaY);
                        break;
                    case MotionEvent.ACTION_UP:
                        TopDrawer.this.touchOff();
                        if (snap) TopDrawer.this.snap();
                        break;
                }
                return true;
            }
        });
    }

    private int TOUCH_ON_COLOR = Color.parseColor("#66000000");

    private void touchOn() {
        try {
            knob.getChildAt(0).setBackgroundColor(TOUCH_ON_COLOR);
        } catch (Exception ignore) {}
    }

    private void touchOff() {
        try {
            knob.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception ignore) {}
    }

    private void snap() {
        int movingDistance = room.getHeight() + getRoomTopMargin();
        boolean open = (room.getHeight() / 2) < movingDistance;

        snapAnimHandler = new Handler(new SnapAnimHandler(open, movingDistance));
        snapAnimThread = new SnapAnimThread(snapAnimHandler);

        snapAnimThread.start();
    }

    private SnapAnimThread snapAnimThread = null;
    private Handler snapAnimHandler = null;

    private class SnapAnimHandler implements Handler.Callback {

        private final boolean OPEN;
        private final int UNIT_DISTANCE;

        SnapAnimHandler(boolean _open, int _movingDistance) {
            OPEN = _open;
            UNIT_DISTANCE = _movingDistance/(int)ANIMATION_COUNT;
        }

        @Override
        public boolean handleMessage(@NotNull Message msg) {
            if (OPEN) { // open
                if (0 == msg.what) setRoomTopMargin(0);
                else setRoomTopMargin(getRoomTopMargin() + UNIT_DISTANCE);
            } else { // close
                if (0 == msg.what) setRoomTopMargin(-1*room.getHeight());
                else setRoomTopMargin(getRoomTopMargin() - UNIT_DISTANCE);
            }

            if (0 >= msg.what) {
                snapAnimThread = null;
                snapAnimHandler = null;
            }

            return true;
        }
    }

    private class SnapAnimThread extends Thread {

        private final Handler handler;

        SnapAnimThread(Handler _handler) {
            handler = _handler;
        }

        @Override
        public void run() {
            if (null == handler) return;

            for (int i=(int)ANIMATION_COUNT; i>0; i--) {
                handler.sendEmptyMessage(i);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.sendEmptyMessage(0);
        }
    }

    private void setRoomTopMargin(int _topMargin) {
        if ( ! (room.getLayoutParams() instanceof ViewGroup.MarginLayoutParams))
            return;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) room.getLayoutParams();
        params.topMargin = getValidTopMargin(_topMargin);
        room.setLayoutParams(params);
    }

    private int getRoomTopMargin() {
        if ( !(room.getLayoutParams() instanceof ViewGroup.MarginLayoutParams))
            return 0;

        return ((ViewGroup.MarginLayoutParams) room.getLayoutParams()).topMargin;
    }

    private int getValidTopMargin(int _rawTopMargin) {
        if (0 < _rawTopMargin) return 0;
        return Math.max(_rawTopMargin, (-1 * room.getHeight()));
    }

}
