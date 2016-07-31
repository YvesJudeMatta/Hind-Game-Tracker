package com.yvesmatta.hindgametracker;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class HindListFragment extends Fragment {

    private static final String TAG = HindListFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.hind_list_fragment, container, false);

        // Instantiate the CursorAdapter
        CursorAdapter cursorAdapter = new HindCursorAdapter(getActivity(), null, 0);

        // Attach the CursorAdapter to the list
        ListView list = (ListView) view.findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);
        
        insertSampleData();



        return view;
    }

    private void insertSampleData() {
        insertGame();
    }

    private void insertGame() {
    }
}
