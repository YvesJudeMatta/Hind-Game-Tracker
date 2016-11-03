package com.yvesmatta.hindscoreboard.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yvesmatta.hindscoreboard.R;

public class HindAboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Show the back butting in the menu bar
        showBackButton();

        // Set the title
        getActivity().setTitle(getString(R.string.about_hind));

        // Return inflated view
        return inflater.inflate(R.layout.hind_about_fragment, container, false);
    }

    public void showBackButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
