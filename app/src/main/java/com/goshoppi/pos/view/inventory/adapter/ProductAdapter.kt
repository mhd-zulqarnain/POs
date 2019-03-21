package com.goshoppi.pos.view.inventory.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.utils.Utils
import com.squareup.picasso.Picasso
import timber.log.Timber

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

        holder.product_item_title.text = product.productName
        holder.product_weight_range.text = product.productMrp
        holder.product_item_new_price.text = product.offerPrice

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
        val file = Utils.getProductImage(product.storeProductId,"1")
        Timber.e("File is there ? ${file.exists()}")
        if (file.exists()) {
            Picasso.get()
                .load(file)
                .error(R.drawable.no_image)
                .into(holder.product_item_icon)

        } else {
            Picasso.get()
                .load(R.drawable.no_image)
                .into(holder.product_item_icon)
        }
    }
}

class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    internal var product_item_title: TextView = view.findViewById<View>(R.id.product_item_title) as TextView
    internal var product_weight_range: TextView = view.findViewById<View>(R.id.product_item_new_price) as TextView
    internal var product_item_new_price: TextView = view.findViewById<View>(R.id.product_item_new_price) as TextView
    internal var product_item_icon: ImageView = view.findViewById<View>(R.id.product_item_icon) as ImageView
}