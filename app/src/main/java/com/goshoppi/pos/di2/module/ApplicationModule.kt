package com.goshoppi.pos.di2.module

import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.PosCart
import dagger.Module
import dagger.Provides


@Module
class ApplicationModule {
    private val posCart = PosCart()
    @AppScoped
    @Provides
    internal fun providePosCart(): PosCart {
        return posCart
    }

}
