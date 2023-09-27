package com.example.hiwin.teacher_version_bob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.hiwin.teacher_version_bob.R;


public class DefaultFragment extends StaticFragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_default, container, false);
        ImageView img = (ImageView) root.findViewById(R.id.default_img);
        img.setOnClickListener(v -> {
            end();
        });
        return root;
    }

    @Override
    protected View[] getViews() {
        return new View[0];
    }

    @Override
    public void interrupt() {

    }
}
