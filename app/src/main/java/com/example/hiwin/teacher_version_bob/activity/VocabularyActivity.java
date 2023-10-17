package com.example.hiwin.teacher_version_bob.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.example.hiwin.teacher_version_bob.R;
import com.example.hiwin.teacher_version_bob.fragment.StaticFragment;
import com.example.hiwin.teacher_version_bob.fragment.VocabularyFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class VocabularyActivity extends BluetoothCommunicationActivity {

    @Override
    protected void receive(String str) {
        try {
            JSONObject obj = new JSONObject(str);
            String content = obj.getString("content");
            if (content.equals("all_vocabularies")) {
                JSONObject dataObj = obj.getJSONObject("data");
                postFragment(getVocabularyFragment(dataObj.getJSONArray("vocabularies")));
            }
        } catch (JSONException e) {
           e.printStackTrace();
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.vocabulary_toolbar);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_vocabulary;
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
        sendMessage("ALL_VOCABULARIES");
    }

    @Override
    protected void onDisconnect() {

    }

    @Override
    protected void onSerialError(Exception e) {
        runOnUiThread(() -> Toast.makeText(VocabularyActivity.this,"Serial Error:"+e.getMessage(),Toast.LENGTH_SHORT).show());
    }

    private StaticFragment getVocabularyFragment(JSONArray vocabularies) {
        VocabularyFragment vocabularyFragment = new VocabularyFragment();
        vocabularyFragment.initialize(this, vocabularies);
        vocabularyFragment.setCommandListener(this::sendMessage);
        return vocabularyFragment;
    }

    private void postFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.vocabulary_frame, fragment, UUID.randomUUID().toString());
        fragmentTransaction.commit();
    }
}