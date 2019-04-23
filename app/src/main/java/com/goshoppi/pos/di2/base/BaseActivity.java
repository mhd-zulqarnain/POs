package com.goshoppi.pos.di2.base;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import com.goshoppi.pos.di2.scope.ActivityScoped;
import dagger.android.support.DaggerAppCompatActivity;

@ActivityScoped
public abstract class BaseActivity extends DaggerAppCompatActivity {

    @LayoutRes
    protected abstract int layoutRes();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes());
    }
}