package com.goshoppi.pos.ui.weighted.viewmodel

import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import javax.inject.Inject

class WeightedProductViewModel @Inject constructor(
    var localProductRepository: LocalProductRepository,
    var localVariantRepository: LocalVariantRepository
) : ViewModel() {
    var test = "1"
}