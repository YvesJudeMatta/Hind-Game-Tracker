package com.yvesmatta.hindgametracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Initialize a date
        Date date = new Date();

        // Grab needed data from database
        String winner = cursor.getString(cursor.getColumnIndex(DBOpenHelper.GAME_PLAYER_WINNER));
        try {
            date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(DBOpenHelper.GAME_CREATED)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // If its an empty string, set it as a tie
        if (winner == null) {
            winner = context.getString(R.string.tie);
        } else {
            winner += " Won!";
        }

        // Set the Winner text
        TextView txtWinner = (TextView) view.findViewById(R.id.txtWinner);
        txtWinner.setText(winner);

        // Set the formatted date
        TextView txtDateWon = (TextView) view.findViewById(R.id.txtDateWon);
        txtDateWon.setText(dateFormat.format(date));
    }
}
