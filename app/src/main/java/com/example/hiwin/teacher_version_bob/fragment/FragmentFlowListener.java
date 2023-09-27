package com.example.hiwin.teacher_version_bob.fragment;

import android.support.v4.app.Fragment;

public abstract class FragmentFlowListener implements FragmentListener {

    private final Fragment next;
    private final String nextId;


    protected abstract void postFragment(Fragment next, String nextId);

    public FragmentFlowListener(Fragment next, String nextId) {
        this.next = next;
        this.nextId = nextId;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {
        if (next != null)
            postFragment(next,nextId);
    }

}
