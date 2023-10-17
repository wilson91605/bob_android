package com.example.hiwin.teacher_version_bob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hiwin.teacher_version_bob.R;

public class DescriptionFragment extends StaticFragment {
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_description, container, false);
        return root;
    }

    @Override
    protected View[] getViews() {
        View[] views = new View[3];
        views[0] = root.findViewById(R.id.description_imageview);
        views[1] = root.findViewById(R.id.description_textview1);
        views[2] = root.findViewById(R.id.description_textview2);
        return views;
    }

    @Override
    public void interrupt() {

    }
}
