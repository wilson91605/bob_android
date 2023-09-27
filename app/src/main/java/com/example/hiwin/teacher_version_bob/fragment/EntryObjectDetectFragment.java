package com.example.hiwin.teacher_version_bob.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.hiwin.teacher_version_bob.R;

import java.util.Locale;


public class EntryObjectDetectFragment extends StaticFragment {

    private String definition;
    private TextToSpeech tts;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inter_entry_object_detect, container, false);
        TextView definition = (TextView) root.findViewById(R.id.inter_obj_detect_definition);
        definition.setText(this.definition);
        tts.setLanguage(Locale.US);
        tts.setSpeechRate(0.8f);
        tts.speak(this.definition, 1, null, "1");
        return root;
    }

    public void initialize(String definition, TextToSpeech tts) {
        this.definition = definition;
        this.tts = tts;
    }

    @Override
    protected View[] getViews() {
        return new View[0];
    }

    @Override
    public void interrupt() {

    }
}
