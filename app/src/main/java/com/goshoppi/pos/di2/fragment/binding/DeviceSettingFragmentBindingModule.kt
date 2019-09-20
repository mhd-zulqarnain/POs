package com.goshoppi.pos.di2.fragment.binding

import com.goshoppi.pos.di2.scope.FragmentScoped
import com.goshoppi.pos.ui.auth.AdminAuthFragment
import com.goshoppi.pos.ui.settings.DeviceSettingFragment
import com.goshoppi.pos.ui.settings.AddUserFragment
import com.goshoppi.pos.ui.settings.UserManagmentFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeviceSettingFragmentBindingModule{
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideDeviceSettingFragment(): DeviceSettingFragment
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAddUserFragment(): AddUserFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideAdminAuthFragment(): AdminAuthFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideUserManagmentFragment(): UserManagmentFragment
}