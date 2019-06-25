package com.goshoppi.pos.di2.fragment.binding

import com.goshoppi.pos.di2.scope.FragmentScoped
import com.goshoppi.pos.view.auth.AdminAuthFragment
import com.goshoppi.pos.view.settings.DeviceSettingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeviceSettingFragmentBindingModule{
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideDeviceSettingFragment(): DeviceSettingFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAdminAuthFragment(): AdminAuthFragment

}