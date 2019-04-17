package com.goshoppi.pos.di2.viewmodel.utils;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import com.goshoppi.pos.di2.scope.AppScoped;
import timber.log.Timber;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

@AppScoped
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        this.creators = creators;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<? extends ViewModel> creator = creators.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass);
        }
        try {
            Timber.e("ViewModel Factory Success");
            return (T) creator.get();
        } catch (Exception e) {
            Timber.e("ViewModel Factory Exception %s", e.getCause());
            Timber.e("ViewModel Factory Exception %s", e.getMessage());
            Timber.e("ViewModel Factory Exception %s", e.getLocalizedMessage());
            Timber.e("ViewModel Factory Exception %s", e.getStackTrace());
            throw new RuntimeException(e);
        }
    }
}