package com.goshoppi.pos.di2.module

import com.goshoppi.pos.di2.fragment.binding.PosMainFragmentBindingModule
import com.goshoppi.pos.view.PosMainActivity
import com.goshoppi.pos.view.auth.LoginActivity
import com.goshoppi.pos.view.inventory.InventoryHomeActivity
import com.goshoppi.pos.view.inventory.InventoryProductDetailsActivity
import com.goshoppi.pos.view.inventory.LocalInventoryActivity
import com.goshoppi.pos.view.user.AddUserActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.view.customer.CustomerBillDetailActivity
import com.goshoppi.pos.view.customer.CustomerManagmentActivity
import com.goshoppi.pos.view.inventory.ReceiveInventoryActivity

@Module
abstract class ActivityBindingModule {


    @ContributesAndroidInjector(modules = [PosMainFragmentBindingModule::class])
    internal abstract fun bindPosMainActivity(): PosMainActivity


    @ContributesAndroidInjector
    internal abstract fun bindAddUserActivity(): AddUserActivity


    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity


    @ContributesAndroidInjector
    internal abstract fun bindInventoryHomeActivity(): InventoryHomeActivity


    @ContributesAndroidInjector
    internal abstract fun bindInventoryProductDetailsActivity(): InventoryProductDetailsActivity


    @ContributesAndroidInjector
    internal abstract fun bindCustomerBillDetailActivity(): CustomerBillDetailActivity


    @ContributesAndroidInjector
    internal abstract fun bindLocalInventoryActivity(): LocalInventoryActivity


    @ContributesAndroidInjector(modules = [PosMainFragmentBindingModule::class])
    internal abstract fun bindCustomerManagmentActivity(): CustomerManagmentActivity

    @ContributesAndroidInjector
    internal abstract fun bindReceiveInventoryActivity(): ReceiveInventoryActivity
}
