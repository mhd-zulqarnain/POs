package com.goshoppi.pos.utils
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.model.local.LocalCustomer

class CustomerAdapter(var context: Context, var customerList: ArrayList<LocalCustomer>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder:ViewHolder
 if(convertView==null){
            val layout= LayoutInflater.from(context)
            view=layout.inflate(R.layout.single_search_customer_view,parent,false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder;
        }else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val customer = getItem(position) as LocalCustomer
        viewHolder.nameTxt?.text = customer.name
        viewHolder.PhoneTxt?.text = customer.phone.toString()

        return view as View
    }

    override fun getItem(position: Int): Any {
        return customerList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
    return customerList.size
    }

    private class ViewHolder(row: View){
        var nameTxt: TextView?=null
        var PhoneTxt: TextView?=null
        init {
            this.nameTxt = row.findViewById(R.id.tvPersonName) as TextView
            this.PhoneTxt = row.findViewById(R.id.tvPersonPhone) as TextView
        }
    }
}
