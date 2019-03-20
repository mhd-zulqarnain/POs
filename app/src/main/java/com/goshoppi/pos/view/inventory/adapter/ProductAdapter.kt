package com.goshoppi.pos.view.inventory.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.utils.Utils

class ProductAdapter(var ctx: Context, private val onItemClick: (productObj: Product) -> Unit) :
    RecyclerView.Adapter<MyViewHolder>() {
    private var productList: ArrayList<Product>? = null

    fun setProductList(productList: ArrayList<Product>) {
        this.productList = productList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val v = MyViewHolder(
            LayoutInflater.from(ctx).inflate(
                R.layout.single_product_view,
                parent,
                false
            )
        )
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

            product.productImages.forEachIndexed { index, img ->
                Utils.saveImage(img, "${product.storeProductId}_$index", product.storeProductId)
            }
            holder.itemView.setOnClickListener { onItemClick(product) }
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