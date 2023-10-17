package com.example.hiwin.teacher_version_bob.utils;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.*;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;

public class DataSpeaker {
    public interface SpeakerListener {
        void onSpeakComplete();
    }

    private final LinkedList<String> queue = new LinkedList<>();
    private final TextToSpeech tts;
    private SpeakerListener speakerListener;

    public DataSpeaker(TextToSpeech textToSpeech) {
        tts = textToSpeech;
        tts.setLanguage(Locale.US);
//        tts.setSpeechRate(0.3f);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                queue.remove(utteranceId);
                if (queue.isEmpty()) {
                    if (speakerListener != null)
                        speakerListener.onSpeakComplete();
                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    public void speakFully(String name, String tr_name, String sentence, String tr_sentence) {
        if (!tts.isSpeaking()) {
            for (int i = 0; i < 2; i++) {
                setLanguage(Locale.US);
                addTextToQueue(name);
                addDelayToQueue(600);
                spellVocabulary(name);
                setLanguage(Locale.TAIWAN);
                addTextToQueue(tr_name);
                addDelayToQueue(600);
            }

            setLanguage(Locale.US);
            addTextToQueue(sentence);
            addDelayToQueue(100);
            addTextToQueue(sentence);
            addDelayToQueue(100);
            addTextToQueue(sentence);
            addDelayToQueue(100);

            setLanguage(Locale.TAIWAN);
            addTextToQueue(tr_sentence);
        }


    }

    public void speakExampleSentence(String sentence, String tr_sentence) {

        setLanguage(Locale.US);
        addTextToQueue(sentence);
        addDelayToQueue(100);
        addTextToQueue(sentence);
        addDelayToQueue(100);
        addTextToQueue(sentence);
        addDelayToQueue(100);

        setLanguage(Locale.TAIWAN);
        addTextToQueue(tr_sentence);

    }

    private void spellVocabulary(String vocabulary) {
        for (int i = 0; i < vocabulary.length(); i++) {
            addTextToQueue(vocabulary.charAt(i) + "");
            addDelayToQueue(600);
        }
    }

    private void addTextToQueue(String string) {
        String id = UUID.randomUUID().toString();
        tts.speak(string, QUEUE_ADD, null, id);
        queue.add(id);
    }

    private void addDelayToQueue(int durationInMs) {
        String id = UUID.randomUUID().toString();
        tts.playSilentUtterance(durationInMs, QUEUE_ADD, id);
        queue.add(id);
    }

    private void setLanguage(Locale locale) {
        tts.setLanguage(locale);
    }

    public void setSpeakerListener(SpeakerListener speakerListener) {
        this.speakerListener = speakerListener;
    }

    public void shutdown() {
        tts.shutdown();
    }

}
