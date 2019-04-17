package com.goshoppi.pos.di2.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import com.goshoppi.pos.architecture.viewmodel.ProductViewModel;
import com.goshoppi.pos.di2.scope.AppScoped;
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory;
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelKey;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProductViewModel.class)
    abstract ViewModel productDetailsViewModel(ProductViewModel productViewModel);

    @Binds
    @AppScoped
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
