package com.hencoder.hencoderpracticelayout1;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class GestureLockActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GestureLockView(this));
    }
}
