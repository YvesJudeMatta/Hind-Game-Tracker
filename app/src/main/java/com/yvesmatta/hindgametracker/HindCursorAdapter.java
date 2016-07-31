package com.yvesmatta.hindgametracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HindCursorAdapter extends CursorAdapter {

    public HindCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.hind_list_item, viewGroup, false );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Format for the date
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        // Grab needed data from database
        String example = "Example Winner";
        Date date = new Date();

        // Set the Winner text
        TextView txtWinner = (TextView) view.findViewById(R.id.txtWinner);
        txtWinner.setText(example);

        // Set the formatted date
        TextView txtDateWon = (TextView) view.findViewById(R.id.txtDateWon);
        txtDateWon.setText(dateFormat.format(date));
    }
}
