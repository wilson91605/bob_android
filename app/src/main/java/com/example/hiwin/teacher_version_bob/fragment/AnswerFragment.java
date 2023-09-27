package com.example.hiwin.teacher_version_bob.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.hiwin.teacher_version_bob.R;


public class AnswerFragment extends StaticFragment {

    private ImageView img;
    private boolean correct = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_answer, container, false);
        img = (ImageView) root.findViewById(R.id.answer_img);
        img.setImageDrawable(getContext().getDrawable(correct ? R.drawable.correct: R.drawable.incorrect));
        MediaPlayer.create(getContext(),correct?R.raw.sound_good_job:R.raw.sound_try_again).start();
        return root;
    }

    public void correct() {
        correct = true;
    }

    public void incorrect() {
        correct = false;
    }

    @Override
    protected View[] getViews() {
        return new View[0];
    }

    @Override
    public void interrupt() {

    }
}
