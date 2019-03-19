package com.goshoppi.pos.ui.inventory
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.model.Product

class ProductAdapter(var ctx: Context) : RecyclerView.Adapter<MyViewHolder>() {
    private var productList: ArrayList<Product>? = null

    fun setProductList(productList: ArrayList<Product>) {
        this.productList = productList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val v = MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.single_row_search_products, parent, false))
        return v
    }

    override fun getItemCount(): Int {
        if (null == productList) return 0
        return productList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val product = productList!![position]
        try {

            holder.tvName.text = product.productName
            holder.etPrice.text = product.productMrp
            holder.etStock.text = product.stockBalance
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

 class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    internal var tvName: TextView = view.findViewById<View>(R.id.tvName) as TextView
    internal var etPrice: TextView = view.findViewById<View>(R.id.etPrice) as TextView
    internal var etStock: TextView = view.findViewById<View>(R.id.etStock) as TextView
}