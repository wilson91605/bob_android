package com.example.hiwin.teacher_version_bob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.*;
import com.example.hiwin.teacher_version_bob.R;
import com.example.hiwin.teacher_version_bob.StoryAdapter;
import com.example.hiwin.teacher_version_bob.fragment.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StoryActivity extends BluetoothCommunicationActivity {

    @Override
    protected void receive(String str) {
        try {
            JSONObject obj = new JSONObject(str);
            String content = obj.getString("content");
            if (content.equals("all_stories_info")) {
                JSONArray array = obj.getJSONArray("data");
                postFragment(getSelectFragment(array));
            } else if (content.equals("story_content")) {
                JSONObject dataObj = obj.getJSONObject("data");
                StaticFragment fragment=getStoryPageFragment(dataObj.getJSONArray("pages"));
                postFragment(fragment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.story_toolbar);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_story;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
    }

    @Override
    protected String getDeviceAddress(Bundle savedInstanceState) {
        Intent it = getIntent();
        return it.getStringExtra("address");
    }

    @Override
    protected void onConnect() {
        sendMessage("STORY_GET LIST");

    }

    @Override
    protected void onDisconnect() {

    }

    @Override
    protected void onSerialError(Exception e) {
        runOnUiThread(() -> Toast.makeText(StoryActivity.this,"Serial Error:"+e.getMessage(),Toast.LENGTH_SHORT).show());
    }

    private Fragment getSelectFragment(JSONArray array) {
        StoriesSelectFragment selectFragment = new StoriesSelectFragment();
        selectFragment.setShowListener(views -> ((ListView) views[0]).setAdapter(new StoryAdapter(this, array)));
        selectFragment.setSelectListener(new StoriesSelectFragment.ItemSelectListener() {

            @Override
            public void onItemSelected(int position) {
                try {
                    JSONObject selectedObject = array.getJSONObject(position);
                    try {
                        String id = selectedObject.getString("id");
                        sendMessage("STORY_GET STORY " + id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return selectFragment;
    }


    private StaticFragment getStoryPageFragment(JSONArray pages) {
        StoryPageFragment storyPageFragment = new StoryPageFragment();
        storyPageFragment.initialize(this, pages);
        storyPageFragment.setCommandListener(this::sendMessage);
        return storyPageFragment;
    }

    private void postFragment(Fragment fragment) {
        if (fragment == null) return;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.story_frame, fragment, UUID.randomUUID().toString());
        fragmentTransaction.commit();
    }
}
