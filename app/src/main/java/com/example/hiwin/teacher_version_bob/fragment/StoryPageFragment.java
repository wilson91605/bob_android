package com.example.hiwin.teacher_version_bob.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.hiwin.teacher_version_bob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.droidsonroids.gif.GifDrawable;

import java.io.IOException;

import static com.example.hiwin.teacher_version_bob.Constants.getResourceIDByString;

public class StoryPageFragment extends StaticFragment {
    private TextView text;
    private ImageView imageview;
    private Context context;
    private JSONArray pages;
    private int index = 0;
    private MediaPlayer player;
    private Button previous;
    private Button next;
    private boolean auto = false;
    private CommandListener commandListener;

    public interface CommandListener {
        void onCommand(String cmd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_story_page, container, false);
        imageview = root.findViewById(R.id.story_page_imageview);

        text = root.findViewById(R.id.story_page_text);
        previous = root.findViewById(R.id.story_page_previous);
        previous.setOnClickListener(onClickListener);

        Button speak = root.findViewById(R.id.story_page_speak);
        speak.setOnClickListener(onClickListener);

        next = root.findViewById(R.id.story_page_next);
        next.setOnClickListener(onClickListener);


        ((ToggleButton) root.findViewById(R.id.story_page_auto)).setOnCheckedChangeListener(onCheckedChangeListener);
        ((ToggleButton) root.findViewById(R.id.story_page_auto)).setChecked(auto);
        ((ToggleButton) root.findViewById(R.id.story_page_auto)).setTextOn("Automatically");
        ((ToggleButton) root.findViewById(R.id.story_page_auto)).setTextOff("Manually");


        try {
            show(pages.getJSONObject(index));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    public void initialize(Context context, JSONArray pages) {
        this.context = context;
        this.pages = pages;
    }

    private void show(JSONObject page) throws JSONException {
        final int audio_id = getResourceIDByString(context, page.getString("audio"), "raw");
        final int drawable_id = getResourceIDByString(context, page.getString("image"), "drawable");
        final String text = page.getString("text");
        this.text.setText(text);

        if (commandListener != null)
            commandListener.onCommand("DO_ACTION " + page.getString("action"));

//        Drawable drawable = drawable_id <= 0 ? null : context.getDrawable(drawable_id);
        player = MediaPlayer.create(context, audio_id);
        if (player != null)
            player.setOnCompletionListener(getSpeakerListener());

        next.setEnabled(index < pages.length() - 1);
        previous.setEnabled(index > 0);

        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), drawable_id);
            gifDrawable.setLoopCount(10);
            imageview.setImageDrawable(gifDrawable);
            gifDrawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        story_text.setText(text);
        player.start();
    }

    @Override
    protected View[] getViews() {
        return new View[0];
    }

    @Override
    public void interrupt() {
        end();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if (commandListener != null)
            commandListener.onCommand("STOP_ALL_ACTION");
    }

    final View.OnClickListener onClickListener = v -> {
        if (v.getId() == R.id.story_page_previous) {
            player.release();
            previousPage();
        } else if (v.getId() == R.id.story_page_speak) {
            player.seekTo(0);
            player.start();
        } else if (v.getId() == R.id.story_page_next) {
            player.stop();
            player.release();
            nextPage();
        } else
            throw new IllegalStateException();
    };


    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            auto = isChecked;
            if (player != null)
                player.setOnCompletionListener(getSpeakerListener());
        }
    };


    private void nextPage() {
        if (index < pages.length() - 1 && index >= 0) {
            if (commandListener != null)
                commandListener.onCommand("STOP_ALL_ACTION");
            try {
                show(pages.getJSONObject(++index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void previousPage() {
        if (index < pages.length() && index > 0) {
            if (commandListener != null)
                commandListener.onCommand("STOP_ALL_ACTION");
            try {
                show(pages.getJSONObject(--index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private MediaPlayer.OnCompletionListener getSpeakerListener() {
        return mp -> {
            if (auto) {
                nextPage();
            }
//            if (this.commandListener != null)
//                this.commandListener.onCommand("STOP_ALL_ACTION");

        };

    }

    public void setCommandListener(CommandListener listener) {
        this.commandListener = listener;
    }
}
