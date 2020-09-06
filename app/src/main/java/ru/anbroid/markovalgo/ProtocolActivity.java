package ru.anbroid.markovalgo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProtocolActivity extends AppCompatActivity
{
    private TextView editTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        Toolbar toolbar = findViewById(R.id.toolbar_protocol);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { finish(); }
        });

        editTask = findViewById(R.id.task);
        editTask.setText(getIntent().getStringExtra("task"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putString("text", editTask.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);

        editTask.setText(inState.getString("text"));
    }
}
