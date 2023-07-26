package com.dki.hybridapptest.utils;

import android.os.Handler;
import android.os.HandlerThread;

public class WorkThread {

    /**
     * work thread 에서 작업을 수행하기 위한 핸들러
     */
    private static HandlerThread handlerThread = null;
    private static Handler m_Handler = null;

    /**
     * work thread 이외의 곳에서 UI 변경.
     *
     * @param r 수행할 일
     */
    public static void execute(Runnable r) {
        initializeHandler();
        m_Handler.post(r);
    }

    /**
     * work thread 이외의 곳에서 UI 변경.
     *
     * @param r    수행할 일
     * @param time 지연시간.
     */
    public static void execute(Runnable r, long time) {
        initializeHandler();
        m_Handler.postDelayed(r, time);
    }


    /**
     * work thread 생성
     */
    public static void initializeHandler() {
        if (handlerThread == null) {
            handlerThread = new HandlerThread("WorkThread");
            handlerThread.start();

            m_Handler = new Handler(handlerThread.getLooper());
        }
    }
}
