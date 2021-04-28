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

        if (myApp.markovAdapter.exec_line > myApp.markovAdapter.getCount() - 1)
        {
            myApp.stop();
            myApp.showStopMessage();
            return;
        }

        myApp.markovAdapter.notifyDataSetChanged();

        String workString = myApp.workString.getText().toString();
        String sample = myApp.markovAdapter.markovArrayList.get(myApp.markovAdapter.exec_line).getSample();
        String replace = myApp.markovAdapter.markovArrayList.get(myApp.markovAdapter.exec_line).getReplacement();

        if (StringUtils.contains(workString, sample) && !sample.isEmpty())
        {
            myApp.markovAdapter.match_line = myApp.markovAdapter.exec_line;
            myApp.markovAdapter.notifyDataSetChanged();

            if (replace.endsWith("."))
            {
                replace = StringUtils.removeEnd(replace, ".");
                myApp.markovAdapter.exec_line = myApp.markovAdapter.getCount() - 1;
            }
            else
            {
                myApp.markovAdapter.exec_line = -1;
            }

            myApp.workString.setText(StringUtils.replaceOnce(workString, sample, replace));
        }
        else if (sample.isEmpty() && !replace.isEmpty())
        {
            myApp.markovAdapter.match_line = myApp.markovAdapter.exec_line;
            myApp.markovAdapter.notifyDataSetChanged();

            if (replace.endsWith("."))
            {
                replace = StringUtils.removeEnd(replace, ".");
                myApp.markovAdapter.exec_line = myApp.markovAdapter.getCount() - 1;
            }
            else
            {
                myApp.markovAdapter.exec_line = -1;
            }

            myApp.workString.setText(replace + workString);
        }

        myApp.markovAdapter.exec_line++;
    }
}
