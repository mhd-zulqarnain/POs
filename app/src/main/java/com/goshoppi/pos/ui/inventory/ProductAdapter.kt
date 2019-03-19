package com.goshoppi.pos.ui.inventory

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.utils.Utils

class ProductAdapter(var ctx: Context) : RecyclerView.Adapter<MyViewHolder>() {
    private var productList: ArrayList<Product>? = null

    fun setProductList(productList: ArrayList<Product>) {
        this.productList = productList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val v = MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.single_product_view, parent, false))
        return v
    }

    override fun getItemCount(): Int {
        if (null == productList) return 0
        return productList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val product = productList!![position]
        try {

            holder.product_item_title.text = product.productName
            holder.product_weight_range.text = product.productMrp
            holder.product_item_new_price.text = product.offerPrice
            if (product.productImages[0] != null)
                Utils.saveImage(product.productImages[0], product.productName.substring(1, 6))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    internal var product_item_title: TextView = view.findViewById<View>(R.id.product_item_title) as TextView
    internal var product_weight_range: TextView = view.findViewById<View>(R.id.product_item_new_price) as TextView
    internal var product_item_new_price: TextView = view.findViewById<View>(R.id.product_item_new_price) as TextView
}