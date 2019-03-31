package com.goshoppi.pos.view.inventory.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.utils.Utils
import com.squareup.picasso.Picasso
import timber.log.Timber
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import com.goshoppi.pos.R


class ProductAdapter(var ctx: Context, private val onItemClick: (productObj: MasterProduct,isOption:Boolean) -> Unit) :
    RecyclerView.Adapter<MyViewHolder>() {
    private var productList: ArrayList<MasterProduct>? = null

    fun setProductList(productList: ArrayList<MasterProduct>) {
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
        holder.tv_Options.visibility = View.VISIBLE

        holder.itemView.setOnClickListener {
            onItemClick(product,false)
        }
        holder.tv_Options.setOnClickListener {
            val popup = PopupMenu(ctx, holder.tv_Options)
            popup.inflate(R.menu.pop_up_product_menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when(item!!.itemId){
                        R.id.nav_add_to_local->{
                            onItemClick(product,true)
                            return true
                        }
                    }
                    return true
                }

            })
            popup.show()
        }
        val file = Utils.getProductImage(product.storeProductId,"1")
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
    internal var tv_Options: TextView = view.findViewById<View>(R.id.tv_Options) as TextView
}

