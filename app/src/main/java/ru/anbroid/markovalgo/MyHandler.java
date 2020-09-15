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

        int i = 0;

        for (; i < myApp.markovAdapter.getCount(); ++i)
        {
            String workString = myApp.workString.getText().toString();
            String sample = myApp.markovAdapter.markovArrayList.get(i).getSample();
            String replace = myApp.markovAdapter.markovArrayList.get(i).getReplacement();

            if (replace.endsWith("."))
            {
                myApp.markovAdapter.exec_line = i;
                myApp.markovAdapter.notifyDataSetChanged();

                replace = StringUtils.removeEnd(replace, ".");

                if (sample.isEmpty())
                    myApp.workString.setText(replace + workString);
                else
                    myApp.workString.setText(StringUtils.replaceOnce(workString, sample, replace));

                myApp.stop();
                myApp.showStopMessage();

                break;
            }

            if (StringUtils.contains(workString, sample) && !sample.isEmpty())
            {
                myApp.markovAdapter.exec_line = i;
                myApp.markovAdapter.notifyDataSetChanged();

                myApp.workString.setText(StringUtils.replaceOnce(workString, sample, replace));

                break;
            }
            else if (sample.isEmpty() && !replace.isEmpty())
            {
                myApp.markovAdapter.exec_line = i;
                myApp.markovAdapter.notifyDataSetChanged();

                myApp.workString.setText(replace + workString);

                break;
            }
        }

        if (i == myApp.markovAdapter.getCount())
        {
            myApp.stop();
            myApp.showStopMessage();
        }
    }
}
