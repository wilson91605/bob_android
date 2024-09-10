package com.example.hiwin.teacher_version_bob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ListView;
import android.widget.Toast;

import com.example.hiwin.teacher_version_bob.R;
import com.example.hiwin.teacher_version_bob.StoryAdapter;
import com.example.hiwin.teacher_version_bob.objectAdapter;
import com.example.hiwin.teacher_version_bob.fragment.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.UUID;

import static com.example.hiwin.teacher_version_bob.Constants.getResourceIDByString;

/**
 * 物品動辨識遊戲主畫面
 */
public class InteractiveObjectDetectActivity extends BluetoothCommunicationActivity {


    private static final String THIS_LOG_TAG = "InteractiveObjectDetectActivity";
    private final LinkedList<JSONObject> available_vocabulary = new LinkedList<>();
    private JSONArray objects;
    private String answer=null;
    private final Handler handler = new Handler();
    private Fragment currentQuestion;
    private TextToSpeech tts;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        tts = new TextToSpeech(this, null);
    }

    @Override
    protected String getDeviceAddress(Bundle savedInstanceState) {
        Intent it = getIntent();
        return it.getStringExtra("address");
    }

    @Override
    protected void receive(String str) {
        try {
            Log.d(THIS_LOG_TAG, "received string:");
            Log.d(THIS_LOG_TAG, str);
//            將str字串轉成JSONObject
            JSONObject json = new JSONObject(str);

//            內容類型
            String content = json.getString("content");
            if (content.equals("all_objects_info")) {
                JSONArray array = json.getJSONArray("data");
                postFragment_obj(getSelectFragment(array));
            }
            else if (content.equals("objects_content")) {

                JSONArray raw = json.getJSONArray("data");
//                全部物品陣列
                objects = new JSONArray();

                for (int i = 0; i < raw.length(); i++) {
                    objects.put(raw.getJSONObject(i));
                }
//
                selectNewAnswer();
//                重置資料
                reset();

//               通知主控制器開始物品辨識
                sendMessage("OBJECT_DETECTOR ENABLE");
            }
            else if (content.equals("single_object")) {
                if(answer==null)
                    return;
//                如果接收到實際辨識到的物件
//                sendMessage("OBJECT_DETECTOR DISABLE");

                String detected_object = json.getJSONObject("data").getString("name");

//                如果接收到實際辨識到的物件
                AnswerFragment fragment = new AnswerFragment();

//                判斷辨識到的物品是否與畫面上顯示一樣
                boolean correct = detected_object.equals(answer);


//                顯示答案畫面
                postFragment(fragment, "A");

                new Thread(() -> {

                    if (correct) {
//                        顯示正確畫面
                        fragment.correct();

//                        做出打圈動作
                        sendMessage("DO_ACTION correct.csv");
                    } else {
//                        顯示錯誤畫面
                        fragment.incorrect();

//                        做出打叉動作
                        sendMessage("DO_ACTION incorrect.csv");
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendMessage("OBJECT_DETECTOR ENABLE");
                    handler.post(() -> postFragment(currentQuestion, "UU"));
                }).start();
            } else {
                throw new IllegalStateException("Unknown state");
            }
        } catch (IllegalArgumentException | JSONException e) {
            Log.e(THIS_LOG_TAG, e.getMessage());
        }
    }

    MenuItem next_item;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        next_item = menu.add("Next");
        next_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == next_item) {
            try {
                selectNewAnswer();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void reset() throws JSONException {
        available_vocabulary.clear();
        for (int i = 0; i < objects.length(); i++) {
            available_vocabulary.add(objects.getJSONObject(i));
        }
    }

    /**
     * 選擇新題目
     *
     * @throws JSONException json錯誤
     */
    private void selectNewAnswer() throws JSONException {
        if (objects == null || objects.length() == 0)
            return;
        if (available_vocabulary.size() == 0)
            reset();


        int i = (int) (Math.random() * available_vocabulary.size());
        JSONObject selected = available_vocabulary.get(i).getJSONObject("data");

        answer = selected.getString("name");
        String definition = selected.getString("definition");
        currentQuestion = getEntryDetectFragment(definition);
        postFragment(currentQuestion, "AA");
        available_vocabulary.remove(i);
    }


//    public interface DetectedListener {
//        void onDetected(String obj);
//    }

    private Fragment getEntryDetectFragment(String definition) {
        EntryObjectDetectFragment objectDetectFragment = new EntryObjectDetectFragment();
        objectDetectFragment.initialize(definition, tts);
        return objectDetectFragment;
    }


    @Override
    protected void onConnect() {
//        抓取所有物件資料
        sendMessage("DB_GET_ALL LIST");
    }

    @Override
    protected void onDisconnect() {
        sendMessage("STOP_DETECT");
    }

    @Override
    protected void onSerialError(Exception e) {
        runOnUiThread(() -> Toast.makeText(InteractiveObjectDetectActivity.this, "Serial Error:" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStop() {
        if (isConnected())
            sendMessage("STOP_DETECT");
        super.onStop();
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.inter_obj_detect_toolbar);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_inter_obj_detect;
    }

    /**
     * @param fragment 要顯示之Fragment
     * @param id       ID
     */
    private void postFragment(Fragment fragment, String id) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.inter_obj_detect_frame, fragment, id);
        fragmentTransaction.commit();
    }
    private Fragment getSelectFragment(JSONArray array) {
        objectSelectFragment selectFragment = new objectSelectFragment();
        selectFragment.setShowListener(views -> ((ListView) views[0]).setAdapter(new objectAdapter(this, array)));
        selectFragment.setSelectListener(new objectSelectFragment.ItemSelectListener() {

            @Override
            public void onItemSelected(int position) {
                try {
                    JSONObject selectedObject = array.getJSONObject(position);
                    try {
                        String id = selectedObject.getString("story");
                        sendMessage("DB_GET_ALL object " + id);
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
    private void postFragment_obj(Fragment fragment) {
        if (fragment == null) return;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.inter_obj_detect_frame, fragment, UUID.randomUUID().toString());
        fragmentTransaction.commit();
    }
}
