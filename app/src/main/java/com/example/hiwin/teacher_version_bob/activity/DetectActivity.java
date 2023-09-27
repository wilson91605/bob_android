package com.example.hiwin.teacher_version_bob.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.hiwin.teacher_version_bob.R;

public abstract class DetectActivity extends BluetoothCommunicationActivity {

    private static final String BT_LOG_TAG = "BluetoothInfo";

    private boolean isDetecting;
    private MenuItem item_detect;

    protected abstract void showDefault();

    @Override
    protected int getContentView() {
        return R.layout.activity_detect;
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.detect_toolbar);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        showDefault();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        item_detect = menu.add("Detect");
        item_detect.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        setDetectMenuItem(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!super.onOptionsItemSelected(item))
            if (item == item_detect) {
                try {
                    if (!isConnected())
                        throw new RuntimeException("Not Connected.");

                    if (isDetecting) {
                        detect_pause();
                        isDetecting = false;
                    } else {
                        detect_start();
                        isDetecting = true;
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(BT_LOG_TAG, e.getMessage());
                }
                return true;
            }
        return false;

    }

    protected void detect_pause() {
        if (!isConnected())
            throw new RuntimeException("Not Connected.");
        setDetectMenuItem(false);
        sendMessage("PAUSE_DETECT");
    }

    protected void detect_start() {
        if (!isConnected())
            throw new RuntimeException("Not Connected.");
        setDetectMenuItem(true);
        sendMessage("START_DETECT");
    }

    private void setDetectMenuItem(boolean isDetecting) {
        if (isDetecting) {
            item_detect.setTitle("Pause");
        } else {
            item_detect.setTitle("Start");
        }
    }

    protected void postFragment(Fragment fragment, String id) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.detect_frame, fragment, id);
        fragmentTransaction.commit();
    }
}
