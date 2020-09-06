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

        if (myApp.markovAdapter.exec_line < myApp.markovAdapter.markovArrayList.size() && MainActivityRef != null)
        {
           ;
        }
        else
        {
            myApp.stop();
            myApp.showError();
        }
    }
}
