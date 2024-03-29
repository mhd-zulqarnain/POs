package com.goshoppi.pos.di2.fragment.binding

import com.goshoppi.pos.di2.scope.FragmentScoped

import com.goshoppi.pos.ui.dashboard.*
import com.goshoppi.pos.ui.dashboard.sales.AdminSalesFragment
import com.goshoppi.pos.ui.dashboard.sales.CustomerFootfallFragment
import com.goshoppi.pos.ui.dashboard.sales.SalesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AdminDashboardFragmentBindingModule{
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAdminCashFragment(): AdminCashFragment
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAdminCustomersFragment(): AdminCustomersFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAdminDistributorFragment(): AdminDistributorFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAdminReportsFragment(): AdminReportsFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAdminSalesFragment(): AdminSalesFragment
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAdminStockFragment(): AdminStockFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideOverViewFragment(): OverViewFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideProfitFragment(): ProfitFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideCustomerFootfallFragment(): CustomerFootfallFragment
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideSalesFragment(): SalesFragment
}