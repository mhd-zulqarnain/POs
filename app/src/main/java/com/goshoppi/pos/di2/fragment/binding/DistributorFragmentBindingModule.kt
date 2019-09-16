package com.goshoppi.pos.di2.fragment.binding

import com.goshoppi.pos.di2.scope.FragmentScoped
import com.goshoppi.pos.view.distributors.DistributorsOrdersFragment
import com.goshoppi.pos.view.distributors.DistributorsSummeryFragment
import com.goshoppi.pos.view.distributors.DistributorsTransactionFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DistributorFragmentBindingModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideDistributorsTransactionFragment(): DistributorsTransactionFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideDistributorsSummeryFragment(): DistributorsSummeryFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun  provideDistributorOrderFragement():DistributorsOrdersFragment
}
