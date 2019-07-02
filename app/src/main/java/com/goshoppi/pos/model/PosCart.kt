package com.goshoppi.pos.model

import com.goshoppi.pos.model.local.LocalVariant

import java.util.ArrayList

class PosCart {

    val groceryAllVariantsFromCart = ArrayList<LocalVariant>()

    val posCartSize: Int
        get() = groceryAllVariantsFromCart.size

    fun getLocalVariantFromCart(position: Int): LocalVariant {
        return groceryAllVariantsFromCart[position]
    }

    fun setLocalVariantToCart(variant: LocalVariant) {
        groceryAllVariantsFromCart.add(variant)
    }

    fun setLocalVariantToCartAtIndex(index: Int, variant: LocalVariant) {
        groceryAllVariantsFromCart[index] = variant
    }

    fun CheckLocalVariantInCart(variant: LocalVariant): Boolean {
        return groceryAllVariantsFromCart.contains(variant)
    }

    fun clearAllPosCart() {
        groceryAllVariantsFromCart.clear()
    }

    fun testLog(){
        print("Test Log")
    }
    fun removeSinglePosCart(variant: LocalVariant) {
        groceryAllVariantsFromCart.remove(variant)
    }
}

