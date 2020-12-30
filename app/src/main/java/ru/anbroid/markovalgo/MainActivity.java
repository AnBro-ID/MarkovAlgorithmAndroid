package ru.anbroid.markovalgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.FilenameFilter;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author AnBro-ID, 2018
 * Главная активность приложения
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    protected MarkovAdapter markovAdapter;
    private AsyncTask currentTask;
    protected EditText workString;

    private boolean isPlay;
    private boolean isPaused;
    private boolean isPlayBySteps;
    protected static int speed;             // скорость выполнения

    protected String Task;                  // условие задачи
    protected String backupString;
    private String ChosenFile;              // открытый файл

    protected Handler handler;              // обработчик сообщений
    private Thread thread;                  // поток выполнения МП

    private ImageButton playBtn;            // кнопка для начала выполнения программы МП

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initUI();
    }

    protected void initUI()
    {
        isPlay = false;
        isPaused = false;
        isPlayBySteps = false;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        speed = Integer.parseInt(sp.getString("speed_list", "500"));

        markovAdapter = new MarkovAdapter(this);
        handler = new MyHandler(this);

        ListView lvMain = findViewById(R.id.lvMain);
        workString = findViewById(R.id.workString);
        lvMain.setAdapter(markovAdapter);

        findViewById(R.id.add_line_up).setOnClickListener(this);
        findViewById(R.id.add_line_down).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.delete_line).setOnClickListener(this);
        findViewById(R.id.stop_btn).setOnClickListener(this);
        findViewById(R.id.restore_ribbon).setOnClickListener(this);
        findViewById(R.id.backup_ribbon).setOnClickListener(this);
        findViewById(R.id.create_btn).setOnClickListener(this);
        findViewById(R.id.open_btn).setOnClickListener(this);
        findViewById(R.id.save_btn).setOnClickListener(this);
        findViewById(R.id.save_btn).setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showSaveDialog(true);
                return true;
            }
        });
        findViewById(R.id.step_btn).setOnClickListener(this);

        playBtn = findViewById(R.id.play_btn);
        playBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_settings:
            {
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
            }

            break;

            case R.id.menu_help:
            {
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
            }

            break;

            case R.id.menu_task:
            {
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra("task", Task);
                startActivityForResult(intent, 1);
            }

            break;

            case R.id.menu_about:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.about_dialog, null);
                TextView about_text = view.findViewById(R.id.about);
                String about_string = getString(R.string.about_desc);
                about_string += "\n© AnBro-ID\nE-mail: andrey-mail-mail@inbox.ru\n";

                try
                {
                    PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);

                    about_string += getString(R.string.about_version) + ' ' + pInfo.versionName;
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    e.printStackTrace();
                }

                builder.setTitle(R.string.menu_about);
                about_text.setText(about_string);
                builder.setView(view);

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.add_line_up:

                if (markovAdapter.isSelected)
                {
                    markovAdapter.markovArrayList.add(markovAdapter.current_line, new Markov());
                    markovAdapter.markovArrayList.trimToSize();
                    markovAdapter.notifyDataSetChanged();
                }

                break;

            case R.id.add_line_down:

                if (markovAdapter.isSelected)
                {
                    markovAdapter.markovArrayList.add(markovAdapter.current_line + 1, new Markov());
                    markovAdapter.markovArrayList.trimToSize();
                    markovAdapter.notifyDataSetChanged();
                }

                break;

            case R.id.up:

                if (markovAdapter.isSelected && markovAdapter.current_line > 0)
                {
                    Markov markovString = new Markov(markovAdapter.markovArrayList.get(markovAdapter.current_line - 1));
                    markovAdapter.markovArrayList.set(markovAdapter.current_line - 1, markovAdapter.markovArrayList.get(markovAdapter.current_line));
                    markovAdapter.markovArrayList.set(markovAdapter.current_line, markovString);
                    markovAdapter.current_line--;
                    markovAdapter.notifyDataSetChanged();
                }

                break;

            case R.id.down:

                if (markovAdapter.isSelected && markovAdapter.current_line + 1 < markovAdapter.getCount())
                {
                    Markov markovString = new Markov(markovAdapter.markovArrayList.get(markovAdapter.current_line + 1));
                    markovAdapter.markovArrayList.set(markovAdapter.current_line + 1, markovAdapter.markovArrayList.get(markovAdapter.current_line));
                    markovAdapter.markovArrayList.set(markovAdapter.current_line, markovString);
                    markovAdapter.current_line++;
                    markovAdapter.notifyDataSetChanged();
                }

                break;

            case R.id.delete_line:

                if (markovAdapter.markovArrayList.size() > 1 && markovAdapter.isSelected)
                {
                    if (markovAdapter.markovArrayList.get(markovAdapter.current_line).hasText())
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        builder.setTitle(R.string.line_delete_head);
                        builder.setMessage(R.string.line_delete_confirm);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                markovAdapter.markovArrayList.remove(markovAdapter.current_line);
                                markovAdapter.markovArrayList.trimToSize();

                                if (markovAdapter.current_line == markovAdapter.markovArrayList.size())
                                    markovAdapter.current_line--;

                                markovAdapter.notifyDataSetChanged();
                            }
                        });

                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else
                    {
                        markovAdapter.markovArrayList.remove(markovAdapter.current_line);
                        markovAdapter.markovArrayList.trimToSize();

                        if (markovAdapter.current_line == markovAdapter.markovArrayList.size()) markovAdapter.current_line--;

                        markovAdapter.notifyDataSetChanged();
                    }
                }

                break;

            case R.id.play_btn:

                if (isPlay) pause();
                else if (!isPlayBySteps) start();

                break;

            case R.id.stop_btn: stop(); break;
            case R.id.restore_ribbon:

                if (backupString != null)
                    workString.setText(backupString);

                break;

            case R.id.backup_ribbon: backupString = workString.getText().toString(); break;
            case R.id.create_btn: newFile(); break;
            case R.id.open_btn: showOpenDialog(); break;
            case R.id.save_btn: showSaveDialog(false); break;
            case R.id.step_btn: if (!(isPlay || isPaused)) startBySteps();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) Task = data.getStringExtra("task");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        workString.clearFocus();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (isPlay) pause();
    }

    private void start()
    {
        playBtn.setImageResource(R.drawable.pause);

        isPlay = true;

        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(speed);
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }

                    handler.sendEmptyMessage(0);
                }
            }
        });

        if (!isPaused) markovAdapter.exec_line = 0;

        isPaused = false;

        thread.start();
    }

    private void startBySteps()
    {
        isPlayBySteps = true;

        if (thread == null)
        {
            markovAdapter.exec_line = 0;
            thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    handler.sendEmptyMessage(0);
                }
            });

            thread.run();
        }
        else thread.run();
    }

    protected void stop()
    {
        if (thread != null)
        {
            isPlay = false;
            isPaused = false;
            isPlayBySteps = false;

            playBtn.setImageResource(R.drawable.play);

            thread.interrupt();
            thread = null;

            handler.removeCallbacksAndMessages(null);

            markovAdapter.exec_line = -1;
            markovAdapter.notifyDataSetChanged();
        }
    }

    protected void pause()
    {
        playBtn.setImageResource(R.drawable.play);

        isPlay = false;
        isPaused = true;

        handler.removeCallbacksAndMessages(null);
        thread.interrupt();
    }

    protected void showStopMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.stop_head);
        builder.setMessage(R.string.stop_desc);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed()
    {
        saveFileConfirm();
    }

    private void saveFileConfirm()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.close_head);
        builder.setMessage(R.string.close_confirm);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) { finish(); }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showOpenDialog()
    {
        if (!StorageUtils.checkStoragePermission(this)) return;

        final String[] mFileList;
        File mPath = new File(Environment.getExternalStorageDirectory().toString());

        FilenameFilter filter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                File file = new File(dir, filename);

                return file.isFile() && file.getName().endsWith(".mna");
            }
        };

        mFileList = mPath.list(filter);

        if (mFileList.length == 0)
                Toast.makeText(this, getString(R.string.no_file) + ' ' +
                        Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_LONG).show();
        else
        {
            if (isPlay || isPaused || isPlayBySteps) stop();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.open_file_head) + '(' +
                    Environment.getExternalStorageDirectory().toString() + ')');

            builder.setItems(mFileList, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    ChosenFile = mFileList[which];
                    currentTask = new OpenFile(MainActivity.this, ChosenFile).execute();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * Метод, показывающий диалог сохранения файла
     * @param saveAs - тип диалога
     */

    private void showSaveDialog(boolean saveAs)
    {
        if (!StorageUtils.checkStoragePermission(this)) return;

        if (isPlay || isPaused || isPlayBySteps) stop();

        if (ChosenFile == null || saveAs)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.filesave_dialog, null);
            final EditText editText = view.findViewById(R.id.file_name);

            builder.setTitle(R.string.save_as);
            builder.setView(view);
            builder.setPositiveButton(R.string.ok, null);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });

            AlertDialog alert = builder.create();

            alert.setOnShowListener(new DialogInterface.OnShowListener()
            {
                @Override
                public void onShow(final DialogInterface dialog)
                {
                    Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            String filename = editText.getText().toString();

                            if (!filename.isEmpty())
                            {
                                ChosenFile = filename + ".mna";
                                currentTask = new SaveFile(MainActivity.this, ChosenFile).execute();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });

            alert.show();
        }
        else currentTask = new SaveFile(MainActivity.this, ChosenFile).execute();
    }

    private void newFile()
    {
        if (isPlay || isPaused || isPlayBySteps) stop();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.close_head);
        builder.setMessage(R.string.close_confirm);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                resetState();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void resetState()
    {
        Task = null;
        ChosenFile = null;
        backupString = null;
        workString.setText("");

        markovAdapter.resetAdapter();
        markovAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
            Toast.makeText(this, R.string.access_error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);

        isPlay = inState.getBoolean("isPlay");
        isPlayBySteps = inState.getBoolean("isPlayBySteps");
        isPaused = inState.getBoolean("isPaused");
        ChosenFile = inState.getString("file");
        Task = inState.getString("task");
        backupString = inState.getString("backupString");
        workString.setText(inState.getString("workString"));

        markovAdapter.current_line = inState.getInt("current_line");
        markovAdapter.exec_line = inState.getInt("exec_line");
        markovAdapter.isSelected = inState.getBoolean("isSelected");

        markovAdapter.markovArrayList.clear();

        for (int i = 0; i < inState.getInt("size"); ++i)
            markovAdapter.markovArrayList.add((Markov) inState.getSerializable(Integer.toString(i)));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        stop();

        outState.putBoolean("isPlay", isPlay);
        outState.putBoolean("isPlayBySteps", isPlayBySteps);
        outState.putBoolean("isPaused", isPaused);
        outState.putString("file", ChosenFile);
        outState.putString("task", Task);
        outState.putString("backupString", backupString);
        outState.putString("workString", workString.getText().toString());

        outState.putInt("current_line", markovAdapter.current_line);
        outState.putInt("exec_line", markovAdapter.exec_line);
        outState.putBoolean("isSelected", markovAdapter.isSelected);
        outState.putInt("size", markovAdapter.getCount());

        for (int i = 0; i < markovAdapter.getCount(); ++i)
            outState.putSerializable(Integer.toString(i), markovAdapter.markovArrayList.get(i));

        super.onSaveInstanceState(outState);
    }

    protected void lockScreenOrientation()
    {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    protected void unlockScreenOrientation()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }
}
