package com.example.hiwin.teacher_version_bob.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.example.hiwin.teacher_version_bob.activity.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModeDialogFragment extends DialogFragment {
    public enum Mode {
        face_detect("Face Detect", FaceDetectActivity.class),
        object_detect("Object Detect", ObjectDetectActivity.class),
        interactive_object_detect("Interactive Object Detect", InteractiveObjectDetectActivity.class),
        story("Story", StoryActivity.class),
        vocabulary("Vocabulary",VocabularyActivity.class);

        final String description;
        private final Class<?> clazz;

        Mode(String description, Class<?> clazz) {
            this.description = description;
            this.clazz = clazz;
        }

        public String getDescription() {
            return description;
        }

        public Class<?> getSelectedClass() {
            return clazz;
        }
    }

    public interface OptionListener {
        void select(Mode mode);
    }

    private OptionListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Mode[] list = Mode.values();
        String[] items = new String[list.length];
        Arrays.stream(Mode.values()).map(Mode::getDescription).collect(Collectors.toList()).toArray(items);
        builder.setTitle("Mode")
                .setItems(items, (dialog, which) -> {
                    if (listener != null)
                        listener.select(list[which]);
                });
        return builder.create();
    }

    public void setListener(OptionListener listener) {
        this.listener = listener;
    }
}