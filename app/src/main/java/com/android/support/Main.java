package com.android.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

public class Main {

    
    static {

        System.loadLibrary("MyLibName");
    }

    private static native void CheckOverlayPermission(Context context);

    

    public static void Start(Context context) {
        CrashHandler.init(context, false);

        CheckOverlayPermission(context);
    }
}
