package com.goshoppi.pos.model

import java.util.ArrayList

class PosCart {

    val allorderItemsFromCart = ArrayList<OrderItem>()
    fun getOrderItemFromCart(position: Int): OrderItem {
        return allorderItemsFromCart[position]
    }
    fun setOrderItemToCart(orderItem: OrderItem) {
        allorderItemsFromCart.add(orderItem)
    }
    fun setOrderItemToCartAtIndex(index: Int, orderItem: OrderItem) {
        allorderItemsFromCart[index] = orderItem
    }
    fun checkOrderItemInCart(variantId: Long?): Int {
        var  tmp :Int=-1
        allorderItemsFromCart.forEachIndexed {index,it->
           if(it.variantId==variantId){
               tmp = index
               return@forEachIndexed
           }
        }

        return tmp
    }

    fun getOrderItemFromCartById(variantId: Long?): OrderItem {

        var  tmp :OrderItem = OrderItem()
        allorderItemsFromCart.forEachIndexed {index,it->
            if(it.variantId==variantId){
                tmp = it
                return@forEachIndexed
            }
        }
        return tmp

    }

    fun clearAllPosCart() {
        allorderItemsFromCart.clear()
    }

    fun removeSingleOrderItemPosCart(index: Int) {
        allorderItemsFromCart.removeAt(index)
    }

    val allWightedorderItemsFromCart = ArrayList<OrderItem>()

    fun getWightedOrderItemFromCart(position: Int): OrderItem {
        return allWightedorderItemsFromCart[position]
    }
    fun setWightedOrderItemToCart(orderItem: OrderItem) {
        allWightedorderItemsFromCart.add(orderItem)
    }
    fun setWightedOrderItemToCartAtIndex(index: Int, orderItem: OrderItem) {
        allWightedorderItemsFromCart[index] = orderItem
    }
    fun checkWightedOrderItemInCart(variantId: Long?): Int {
        var  tmp :Int=-1
        allWightedorderItemsFromCart.forEachIndexed {index,it->
           if(it.variantId==variantId){
               tmp = index
               return@forEachIndexed
           }
        }

        return tmp
    }

    fun getWightedOrderItemFromCartById(variantId: Long?): OrderItem {

        var  tmp :OrderItem = OrderItem()
        allWightedorderItemsFromCart.forEachIndexed {index,it->
            if(it.variantId==variantId){
                tmp = it
                return@forEachIndexed
            }
        }
        return tmp

    }

    fun clearAllWightedPosCart() {
        allWightedorderItemsFromCart.clear()
    }
    fun removeSingleWightedOrderItemPosCart(index: Int) {
        allWightedorderItemsFromCart.removeAt(index)
    }
}

