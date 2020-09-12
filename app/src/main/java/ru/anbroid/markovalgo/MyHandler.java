package ru.anbroid.markovalgo;

import android.os.Handler;
import android.os.Message;

import org.apache.commons.lang3.StringUtils;
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

        if (myApp.markovAdapter.exec_line < myApp.markovAdapter.markovArrayList.size())
        {
            String sample = myApp.markovAdapter.markovArrayList.get(myApp.markovAdapter.exec_line).getSample();
            String replace = myApp.markovAdapter.markovArrayList.get(myApp.markovAdapter.exec_line).getReplacement();
            String workString = myApp.workString.getText().toString();

            if (StringUtils.contains(workString, sample) && !sample.isEmpty() || !replace.isEmpty() && sample.isEmpty())
            {
                myApp.markovAdapter.notifyDataSetChanged();
                myApp.markovAdapter.exec_line = 0;
            }
            else
                myApp.markovAdapter.exec_line++;

            if (replace.endsWith("."))
            {
                replace = StringUtils.removeEnd(replace, ".");
                myApp.workString.setText(StringUtils.replaceOnce(workString, sample, replace));

                myApp.stop();
                myApp.showStopMessage();
            }
            else
                myApp.workString.setText(StringUtils.replaceOnce(workString, sample, replace));
            }
        else
        {
            myApp.stop();
            myApp.showStopMessage();
        }
    }
}
