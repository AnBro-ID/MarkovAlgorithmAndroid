package ru.anbroid.markovalgo;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author AnBro-ID, 2022
 * Модифицированный адаптер для списка строк программы МП
 */

public class MarkovAdapter extends ArrayAdapter<Markov>
{
    public ArrayList<Markov> markovArrayList;

    public int current_line;
    public int exec_line;
    public int match_line;
    public boolean isSelected;
    private boolean isUnlocked;

    static class ViewHolder
    {
        public TextView Count;
        public EditText Sample;
        public EditText Replacement;
        public EditText Comment;
    }

    /**
     * Конструктор класса
     * @param context - активность приложения
     */

    public MarkovAdapter(Context context)
    {
        super(context, R.layout.command_list);

        markovArrayList = new ArrayList<>(1);
        markovArrayList.add(new Markov());

        current_line = -1;
        exec_line = -1;
        match_line = -1;
        isSelected = false;
        isUnlocked = true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;

        if (rowView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.command_list, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.Count = rowView.findViewById(R.id.Count);
            viewHolder.Sample = rowView.findViewById(R.id.Sample);
            viewHolder.Replacement = rowView.findViewById(R.id.Replacement);
            viewHolder.Comment = rowView.findViewById(R.id.Comment);

            View.OnClickListener OCL = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    switch (v.getId())
                    {
                        case R.id.Count:

                            if (isSelected && current_line == (int) v.getTag())
                            {
                                isSelected = false;
                                current_line = -1;
                                notifyDataSetChanged();
                            }
                            else
                            {
                                isSelected = true;
                                current_line = (int) v.getTag();
                                notifyDataSetChanged();
                            }

                            break;

                        case R.id.Sample:
                        {
                            final int pos = (int) v.getTag();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.comment_dialog, null);
                            final EditText sample = view.findViewById(R.id.editComment);

                            sample.setText(markovArrayList.get(pos).getSample());

                            builder.setTitle(R.string.sample_text);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    markovArrayList.get(pos).setSample(sample.getText().toString());
                                    notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });

                            builder.setView(view);

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        break;

                        case R.id.Replacement:
                        {
                            final int pos = (int) v.getTag();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.comment_dialog, null);
                            final EditText replacement = view.findViewById(R.id.editComment);

                            replacement.setText(markovArrayList.get(pos).getReplacement());

                            builder.setTitle(R.string.replace_text);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    markovArrayList.get(pos).setReplacement(replacement.getText().toString());
                                    notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });

                            builder.setView(view);

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        break;

                        case R.id.Comment:
                        {
                            final int pos = (int) v.getTag();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.comment_dialog, null);
                            final EditText comment = view.findViewById(R.id.editComment);

                            comment.setText(markovArrayList.get(pos).getComment());

                            builder.setTitle(R.string.comment_text);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    markovArrayList.get(pos).setComment(comment.getText().toString());
                                    notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });

                            builder.setView(view);

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                }
            };

            viewHolder.Count.setOnClickListener(OCL);
            viewHolder.Sample.setOnClickListener(OCL);
            viewHolder.Replacement.setOnClickListener(OCL);
            viewHolder.Comment.setOnClickListener(OCL);

            rowView.setTag(viewHolder);
        }

        if (match_line == position) rowView.setBackgroundResource(R.color.match_line);
        else if (exec_line == position) rowView.setBackgroundResource(R.color.exec);
        else if (current_line == position) rowView.setBackgroundResource(R.color.selected);
        else rowView.setBackgroundResource(R.color.normal);

        ViewHolder holder = (ViewHolder) rowView.getTag();

        if (isUnlocked)
        {
            holder.Sample.setEnabled(true);
            holder.Replacement.setEnabled(true);
            holder.Comment.setEnabled(true);
        }
        else
        {
            holder.Sample.setEnabled(false);
            holder.Replacement.setEnabled(false);
            holder.Comment.setEnabled(false);
        }

        holder.Count.setText(String.valueOf(position + 1));
        holder.Count.setTag(position);
        holder.Sample.setText(markovArrayList.get(position).getSample());
        holder.Sample.setTag(position);
        holder.Replacement.setText(markovArrayList.get(position).getReplacement());
        holder.Replacement.setTag(position);
        holder.Comment.setText(markovArrayList.get(position).getComment());
        holder.Comment.setTag(position);

        return rowView;
    }

    /**
     * Метод, сбрасывающий значения полей класса
     */

    public void resetAdapter()
    {
        current_line = -1;
        exec_line = -1;
        match_line = -1;
        isSelected = false;

        markovArrayList.clear();
        markovArrayList.add(new Markov());
        markovArrayList.trimToSize();
    }

    public void lock()
    {
        isUnlocked = false;
    }

    public void unlock()
    {
        isUnlocked = true;
    }

    @Override
    public int getCount()
    {
        return markovArrayList.size();
    }
}
