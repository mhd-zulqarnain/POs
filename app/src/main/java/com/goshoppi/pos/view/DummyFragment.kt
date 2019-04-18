package com.goshoppi.pos.view


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View

import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.viewmodel.ProductViewModel
import com.goshoppi.pos.di2.base.BaseFragment
import com.goshoppi.pos.di2.viewmodel.utils.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class DummyFragment : BaseFragment() {
    override fun layoutRes(): Int {
        return R.layout.fragment_dummy
    }

    @Inject
    lateinit var viewModelFactory : ViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.e("DummyFragment onViewCreated")
        val productViewModelFactory = ViewModelProviders.of(baseActivity, viewModelFactory).get(ProductViewModel::class.java)
        productViewModelFactory.showTags()
    }
}
