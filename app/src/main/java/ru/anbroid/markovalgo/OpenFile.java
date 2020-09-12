package ru.anbroid.markovalgo;

import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

class OpenFile extends SaveFile
{
    String taskDesc;
    ArrayList<Markov> arrayList;
    String workString;

    public OpenFile(MainActivity myApp, String filename)
    {
        super(myApp, filename);
    }

    protected void onPreExecute()
    {
        activity.get().lockScreenOrientation();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());

        View view = activity.get().getLayoutInflater().inflate(R.layout.progress_indicator, null);
        TextView textView = view.findViewById(R.id.progressTitle);

        textView.setText(activity.get().getString(R.string.opening_file));
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean success)
    {
        if (dialog.isShowing())
            dialog.dismiss();

        if (!success)
        {
            Toast.makeText(activity.get(), R.string.access_error, Toast.LENGTH_LONG).show();
            activity.get().resetState();
        }
        else
        {
            activity.get().Task = taskDesc;
            activity.get().markovAdapter.resetAdapter();
            activity.get().markovAdapter.markovArrayList = arrayList;
            activity.get().workString.setText(workString);
            activity.get().markovAdapter.notifyDataSetChanged();
            activity.get().backupString = workString;
        }

        activity.get().unlockScreenOrientation();
    }

    protected Boolean doInBackground(Void... args)
    {
        DataInputStream in = null;

        int Length;

        try
        {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            in = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(file)));

            Length = in.readBoolean() ? 1 : 0;

            if (Length == 1)
                taskDesc = in.readUTF();

            Length = in.readInt();
            arrayList = new ArrayList<>(Length);

            for (int i = 0; i < Length; ++i)
            {
                String sample = in.readUTF();
                String replacement = in.readUTF();
                String comment = in.readUTF();

                arrayList.add(new Markov(sample, replacement, comment));
            }

            workString = in.readUTF();
        }
        catch (IOException e)
        {
            return false;
        }
        finally
        {
            if (in != null)
                try
                {
                    in.close();
                }
                catch (IOException logOrIgnore)
                {
                    logOrIgnore.printStackTrace();
                }
        }
        return true;
    }
}
