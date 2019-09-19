package com.goshoppi.pos.di2.module

import com.goshoppi.pos.di2.fragment.binding.*
import com.goshoppi.pos.view.home.PosMainActivity
import com.goshoppi.pos.view.auth.LoginActivity
import com.goshoppi.pos.view.category.AddCategoryActivity
import com.goshoppi.pos.view.home.CheckoutActivity
import com.goshoppi.pos.view.customer.CustomerBillDetailActivity
import com.goshoppi.pos.view.customer.CustomerManagmentActivity
import com.goshoppi.pos.view.dashboard.DashboardActivity
import com.goshoppi.pos.view.distributors.DistributorsManagmentActivity
import com.goshoppi.pos.view.inventory.InventoryHomeActivity
import com.goshoppi.pos.view.inventory.InventoryProductDetailsActivity
import com.goshoppi.pos.view.inventory.LocalInventoryActivity
import com.goshoppi.pos.view.inventory.ReceiveInventoryActivity
import com.goshoppi.pos.view.settings.SettingsActivity
import com.goshoppi.pos.view.weighted.WeightedProductsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [PosMainFragmentBindingModule::class])
    internal abstract fun  contributePosMainActivity(): PosMainActivity


    @ContributesAndroidInjector(modules = [CategoryFragmentBindingModule::class])
    internal abstract fun  contributeAddCategoryActivity(): AddCategoryActivity

    @ContributesAndroidInjector(modules = [DeviceSettingFragmentBindingModule::class])
    internal abstract fun  contributeLoginActivity(): LoginActivity


    @ContributesAndroidInjector
    internal abstract fun  contributeInventoryHomeActivity(): InventoryHomeActivity

    @ContributesAndroidInjector
    internal abstract fun  contributeCheckoutActivity(): CheckoutActivity


    @ContributesAndroidInjector
    internal abstract fun  contributeInventoryProductDetailsActivity(): InventoryProductDetailsActivity


    @ContributesAndroidInjector
    internal abstract fun  contributeCustomerBillDetailActivity(): CustomerBillDetailActivity


    @ContributesAndroidInjector
    internal abstract fun  contributeLocalInventoryActivity(): LocalInventoryActivity

    @ContributesAndroidInjector
    internal abstract fun  contributeWeightedProductsActivity(): WeightedProductsActivity

    @ContributesAndroidInjector(modules = [AdminDashboardFragmentBindingModule::class])
    internal abstract fun  contributeDashboardActivity(): DashboardActivity


    @ContributesAndroidInjector(modules = [PosMainFragmentBindingModule::class])
    internal abstract fun  contributeCustomerManagmentActivity(): CustomerManagmentActivity

    @ContributesAndroidInjector(modules = [DistributorFragmentBindingModule::class])
    internal abstract fun  contributeDistributorsManagmentActivity(): DistributorsManagmentActivity

    @ContributesAndroidInjector
    internal abstract fun  contributeReceiveInventoryActivity(): ReceiveInventoryActivity

    @ContributesAndroidInjector(modules = [DeviceSettingFragmentBindingModule::class])
    internal abstract fun  contributeSettingActivity(): SettingsActivity


}
