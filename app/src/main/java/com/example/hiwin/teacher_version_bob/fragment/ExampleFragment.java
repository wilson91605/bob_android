package com.example.hiwin.teacher_version_bob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.hiwin.teacher_version_bob.R;

public class ExampleFragment extends StaticFragment {
    private View layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_example, container, false);
        layout = root.findViewById(R.id.example_layout);
        return root;
    }

    @Override
    protected View[] getViews() {
        View[] views = new View[2];
        views[0] = layout.findViewById(R.id.example_sentence);
        views[1] = layout.findViewById(R.id.example_tr_sentence);
        return views;
    }

    @Override
    public void interrupt() {

    }
}
