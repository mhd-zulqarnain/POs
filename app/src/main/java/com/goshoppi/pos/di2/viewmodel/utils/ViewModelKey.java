package com.goshoppi.pos.di2.viewmodel.utils;

import android.arch.lifecycle.ViewModel;
import dagger.MapKey;

import java.lang.annotation.*;

@Documented
@MapKey
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}
