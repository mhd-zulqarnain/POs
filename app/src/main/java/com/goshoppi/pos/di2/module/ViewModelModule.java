package com.goshoppi.pos.di2.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.goshoppi.pos.architecture.viewmodel.PosMainViewModel;
import com.goshoppi.pos.architecture.viewmodel.ProductViewModel;
import com.goshoppi.pos.di2.scope.AppScoped;
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory;
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelKey;
import com.goshoppi.pos.view.customer.viewmodel.BillDetailViewModel;
import com.goshoppi.pos.view.customer.viewmodel.SummeryViewModel;
import com.goshoppi.pos.view.customer.viewmodel.TransactionViewModel;
import com.goshoppi.pos.view.distributors.viewmodel.DistributorSummeryViewModel;
import com.goshoppi.pos.view.distributors.viewmodel.DistributorTransactionViewModel;
import com.goshoppi.pos.view.inventory.viewmodel.InvProdDetailViewModel;
import com.goshoppi.pos.view.inventory.viewmodel.InventoryHomeViewModel;
import com.goshoppi.pos.view.inventory.viewmodel.LocalInventoryViewModel;
import com.goshoppi.pos.view.inventory.viewmodel.ReceiveInventoryViewModel;
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
    @ViewModelKey(SummeryViewModel.class)
    abstract ViewModel summeryViewModel(SummeryViewModel summeryViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(InvProdDetailViewModel.class)
    abstract ViewModel invProdDetailViewModel(InvProdDetailViewModel invProdDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LocalInventoryViewModel.class)
    abstract ViewModel localInventoryViewModel(LocalInventoryViewModel localInventoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BillDetailViewModel.class)
    abstract ViewModel billDetailViewModel(BillDetailViewModel billDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel.class)
    abstract ViewModel transactionViewModel(TransactionViewModel transactionViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ReceiveInventoryViewModel.class)
    abstract ViewModel receiveInventoryViewModelViewModel(ReceiveInventoryViewModel receiveInventoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DistributorSummeryViewModel.class)
    abstract ViewModel distributorSummeryViewModel(DistributorSummeryViewModel distributorSummeryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DistributorTransactionViewModel.class)
    abstract ViewModel distributorTransactionViewModelViewModel(DistributorTransactionViewModel distributorTransactionViewModel);

    @Binds
    @AppScoped
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
