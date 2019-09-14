package com.goshoppi.pos.di2.fragment.binding

import com.goshoppi.pos.di2.scope.FragmentScoped
import com.goshoppi.pos.view.category.FragmentCategory
import com.goshoppi.pos.view.category.SubCategoryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CategoryFragmentBindingModule{

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideFragmentCategory(): FragmentCategory

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun provideSubCategoryFragment(): SubCategoryFragment


}