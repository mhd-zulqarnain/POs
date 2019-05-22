package com.goshoppi.pos.di2.fragment.binding

import com.goshoppi.pos.di2.scope.ActivityScoped
import com.goshoppi.pos.di2.scope.FragmentScoped
import com.goshoppi.pos.view.DummyFragment
import com.goshoppi.pos.view.customer.CustomerBillFragment
import com.goshoppi.pos.view.customer.CustomerSummeryFragment
import com.goshoppi.pos.view.customer.CustomerWalletFragment
import com.goshoppi.pos.view.customer.TransactionFragment
import com.goshoppi.pos.view.distributors.DistributorsSummeryFragment
import com.goshoppi.pos.view.distributors.DistributorsTransactionFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PosMainFragmentBindingModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideDummyFragment(): DummyFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideCustomerSummeryFragment(): CustomerSummeryFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideCustomerBillFragment(): CustomerBillFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideCustomerWalletFragment(): CustomerWalletFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideTransactionFragment(): TransactionFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideDistributorsTransactionFragment(): DistributorsTransactionFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideDistributorsSummeryFragment(): DistributorsSummeryFragment

}
