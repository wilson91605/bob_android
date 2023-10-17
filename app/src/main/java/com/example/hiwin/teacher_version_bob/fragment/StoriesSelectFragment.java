package com.example.hiwin.teacher_version_bob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.hiwin.teacher_version_bob.R;

public class StoriesSelectFragment extends StaticFragment {
    public abstract static class ItemSelectListener {
        public abstract void onItemSelected(int position);
    }

    private ItemSelectListener selectListener;

    private ListView listView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stories_select, container, false);
        listView = (ListView) root.findViewById(R.id.stories_list);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (selectListener != null) {
                selectListener.onItemSelected(position);
            }
        });
        return root;
    }

    @Override
    protected View[] getViews() {
        View[] views = new View[1];
        views[0] = listView;
        return views;
    }

    @Override
    public void interrupt() {

    }

    public void setSelectListener(ItemSelectListener selectListener) {
        this.selectListener = selectListener;
    }
}
