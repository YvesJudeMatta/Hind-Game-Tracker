package com.yvesmatta.hindgametracker;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HindPlayFragment extends Fragment {

    private static final String TAG = HindPlayFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.hind_play_fragment, container, false);
        return view;
    }
}
