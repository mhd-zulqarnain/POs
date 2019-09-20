package com.goshoppi.pos.di2.fragment.binding

import com.goshoppi.pos.di2.scope.FragmentScoped
import com.goshoppi.pos.ui.customer.CustomerBillFragment
import com.goshoppi.pos.ui.customer.CustomerSummeryFragment
import com.goshoppi.pos.ui.customer.CustomerWalletFragment
import com.goshoppi.pos.ui.customer.TransactionFragment
import com.goshoppi.pos.ui.distributors.DistributorsSummeryFragment
import com.goshoppi.pos.ui.distributors.DistributorsTransactionFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PosMainFragmentBindingModule {



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
