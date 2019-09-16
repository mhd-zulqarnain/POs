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
    internal abstract fun bindPosMainActivity(): PosMainActivity


    @ContributesAndroidInjector(modules = [CategoryFragmentBindingModule::class])
    internal abstract fun bindAddCategoryActivity(): AddCategoryActivity

    @ContributesAndroidInjector(modules = [DeviceSettingFragmentBindingModule::class])
    internal abstract fun bindLoginActivity(): LoginActivity


    @ContributesAndroidInjector
    internal abstract fun bindInventoryHomeActivity(): InventoryHomeActivity

    @ContributesAndroidInjector
    internal abstract fun bindCheckoutActivity(): CheckoutActivity


    @ContributesAndroidInjector
    internal abstract fun bindInventoryProductDetailsActivity(): InventoryProductDetailsActivity


    @ContributesAndroidInjector
    internal abstract fun bindCustomerBillDetailActivity(): CustomerBillDetailActivity


    @ContributesAndroidInjector
    internal abstract fun bindLocalInventoryActivity(): LocalInventoryActivity

    @ContributesAndroidInjector
    internal abstract fun bindWeightedProductsActivity(): WeightedProductsActivity

    @ContributesAndroidInjector(modules = [AdminDashboardFragmentBindingModule::class])
    internal abstract fun bindDashboardActivity(): DashboardActivity


    @ContributesAndroidInjector(modules = [PosMainFragmentBindingModule::class])
    internal abstract fun bindCustomerManagmentActivity(): CustomerManagmentActivity

    @ContributesAndroidInjector(modules = [DistributorFragmentBindingModule::class])
    internal abstract fun bindDistributorsManagmentActivity(): DistributorsManagmentActivity

    @ContributesAndroidInjector
    internal abstract fun bindReceiveInventoryActivity(): ReceiveInventoryActivity

    @ContributesAndroidInjector(modules = [DeviceSettingFragmentBindingModule::class])
    internal abstract fun bindSettingActivity(): SettingsActivity


}
