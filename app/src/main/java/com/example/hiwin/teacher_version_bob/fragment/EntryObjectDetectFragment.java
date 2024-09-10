package com.example.hiwin.teacher_version_bob.fragment;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.hiwin.teacher_version_bob.R;

import java.util.Locale;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EntryObjectDetectFragment extends StaticFragment{

    private String definition;
    private TextToSpeech tts;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inter_entry_object_detect, container, false);
        TextView definition = (TextView) root.findViewById(R.id.inter_obj_detect_definition);
        definition.setText(this.definition);
        //修改嫦娥發音 ; 將填空區"_____"當成字串分割符號
        String[] word = definition.getText().toString().replace("Chang E","Chaung' er.").replace("Qu Yuan","Chuyuán.").split("_____");
        //多線程念句子
        Executor threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(() -> {
            tts.setLanguage(Locale.US);
            tts.setSpeechRate(0.8f);
            for (int i=0;i< word.length;i++) {
                tts.speak(word[i], 1, null, "1");//念出句子
                if (i != word.length-1) {
                    tts.speak("bla bla bla", 1, null, "1");//念出bla bla bla
                    //try {Thread.sleep(2000);} catch (Exception ignored) {} //delay(毫秒)
                }
            }
        });
        //tts.speak(this.definition, 1, null, "1");
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