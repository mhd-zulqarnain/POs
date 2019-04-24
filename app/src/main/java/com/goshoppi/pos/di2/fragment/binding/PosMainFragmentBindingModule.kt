package com.goshoppi.pos.di2.fragment.binding

import com.goshoppi.pos.di2.scope.ActivityScoped
import com.goshoppi.pos.di2.scope.FragmentScoped
import com.goshoppi.pos.view.DummyFragment
import com.goshoppi.pos.view.customer.CustomerSummeryFragment
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
}
