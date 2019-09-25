package com.goshoppi.pos.di2.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goshoppi.pos.ui.home.viewmodel.PosMainViewModel
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelKey
import com.goshoppi.pos.ui.customer.viewmodel.BillDetailViewModel
import com.goshoppi.pos.ui.customer.viewmodel.SummeryViewModel
import com.goshoppi.pos.ui.customer.viewmodel.TransactionViewModel
import com.goshoppi.pos.ui.dashboard.viewmodel.DashboardCustomerViewModel
import com.goshoppi.pos.ui.dashboard.viewmodel.DashboardDistributorViewModel
import com.goshoppi.pos.ui.dashboard.viewmodel.DashboardViewModel
import com.goshoppi.pos.ui.distributors.viewmodel.DistributorOrdersViewModel
import com.goshoppi.pos.ui.distributors.viewmodel.DistributorSummeryViewModel
import com.goshoppi.pos.ui.distributors.viewmodel.DistributorTransactionViewModel
import com.goshoppi.pos.ui.home.viewmodel.CheckoutViewModel
import com.goshoppi.pos.ui.inventory.viewmodel.InvProdDetailViewModel
import com.goshoppi.pos.ui.inventory.viewmodel.InventoryHomeViewModel
import com.goshoppi.pos.ui.inventory.viewmodel.LocalInventoryViewModel
import com.goshoppi.pos.ui.inventory.viewmodel.ReceiveInventoryViewModel
import com.goshoppi.pos.ui.weighted.viewmodel.WeightedProductViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {



    @Binds
    @IntoMap
    @ViewModelKey(PosMainViewModel::class)
    internal abstract fun posMainViewModel(posMainViewModel: PosMainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CheckoutViewModel::class)
    internal abstract fun checkoutViewModel(checkoutViewModel: CheckoutViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InventoryHomeViewModel::class)
    internal abstract fun inventoryHomeViewModel(inventoryHomeViewModel: InventoryHomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SummeryViewModel::class)
    internal abstract fun summeryViewModel(summeryViewModel: SummeryViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(InvProdDetailViewModel::class)
    internal abstract fun invProdDetailViewModel(invProdDetailViewModel: InvProdDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocalInventoryViewModel::class)
    internal abstract fun localInventoryViewModel(localInventoryViewModel: LocalInventoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BillDetailViewModel::class)
    internal abstract fun billDetailViewModel(billDetailViewModel: BillDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel::class)
    internal abstract fun transactionViewModel(transactionViewModel: TransactionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReceiveInventoryViewModel::class)
    internal abstract fun receiveInventoryViewModelViewModel(receiveInventoryViewModel: ReceiveInventoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DistributorSummeryViewModel::class)
    internal abstract fun distributorSummeryViewModel(distributorSummeryViewModel: DistributorSummeryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DistributorTransactionViewModel::class)
    internal abstract fun distributorTransactionViewModel(distributorTransactionViewModel: DistributorTransactionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WeightedProductViewModel::class)
    internal abstract fun weightedProductViewModelViewModel(weightedProductViewModel: WeightedProductViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DistributorOrdersViewModel::class)
    internal abstract fun distributorOrdersViewModel(distributorOrdersViewModel: DistributorOrdersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    internal abstract fun dashboardViewModel(dashboardViewModel: DashboardViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(DashboardDistributorViewModel::class)
    internal abstract fun dashboardDistributorViewModel(dashboardViewModel: DashboardDistributorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardCustomerViewModel::class)
    internal abstract fun dashboardCustomerViewModelViewModel(dashboardCustomerViewModel: DashboardCustomerViewModel): ViewModel

    @Binds
    @AppScoped
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
