package com.example.hiwin.teacher_version_bob;

import android.content.Context;

public class Constants {
    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";

    public static int getResourceIDByString(Context context, String resName, String type) {
        return context.getApplicationContext().getResources()
                .getIdentifier(resName
                        , type
                        , context.getPackageName());
    }


}
