package com.goshoppi.pos.di2.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.goshoppi.pos.architecture.viewmodel.PosMainViewModel;
import com.goshoppi.pos.architecture.viewmodel.ProductViewModel;
import com.goshoppi.pos.di2.scope.AppScoped;
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory;
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelKey;
import com.goshoppi.pos.view.inventory.viewmodel.InvProdDetailViewModel;
import com.goshoppi.pos.view.inventory.viewmodel.InventoryHomeViewModel;
import com.goshoppi.pos.view.inventory.viewmodel.LocalInventoryViewModel;
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
    @IntoMap
    @ViewModelKey(PosMainViewModel.class)
    abstract ViewModel posMainViewModel(PosMainViewModel posMainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(InventoryHomeViewModel.class)
    abstract ViewModel inventoryHomeViewModel(InventoryHomeViewModel inventoryHomeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(InvProdDetailViewModel.class)
    abstract ViewModel invProdDetailViewModel(InvProdDetailViewModel invProdDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LocalInventoryViewModel.class)
    abstract ViewModel localInventoryViewModel(LocalInventoryViewModel localInventoryViewModel);

    @Binds
    @AppScoped
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
