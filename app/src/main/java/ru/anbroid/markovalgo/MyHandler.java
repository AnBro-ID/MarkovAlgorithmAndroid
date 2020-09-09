package ru.anbroid.markovalgo;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

class MyHandler extends Handler
{
    protected WeakReference<MainActivity> MainActivityRef;

    public MyHandler(MainActivity myApp)
    {
        MainActivityRef = new WeakReference<>(myApp);
    }

    @Override
    public void handleMessage(Message msg)
    {
        MainActivity myApp = MainActivityRef.get();


    }
}
