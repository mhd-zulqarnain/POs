package com.goshoppi.pos.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.goshoppi.pos.R
import com.goshoppi.pos.ui.PosMainActivity
import java.util.*

class AdminAuthFragment : Fragment() {

    lateinit var btn_login_sign_in: Button;
    lateinit var spnLocation: Spinner;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_auth, container, false)

        initView(view)
        return view

    }

    private fun initView(view: View) {
        btn_login_sign_in = view.findViewById(R.id.btn_login_sign_in)

        spnLocation = view.findViewById(R.id.spnLocation)

        val allCountries = ArrayList<ListItem>()
        val strCountries = resources.getStringArray(R.array.locations_array)
        val resourceIds = intArrayOf(
            R.drawable.flag_uae,
            R.drawable.flag_australia,
            R.drawable.flag_india,
            R.drawable.flag_usa,
            R.drawable.flag_netherland,
            R.drawable.flag_newzealand,
            R.drawable.flag_uk,
            R.drawable.flag_bahrain
        )
        for (i in strCountries.indices) {
            allCountries.add(ListItem(strCountries[i], resourceIds[i]))
        }
        spnLocation.setAdapter(MyAdapter(activity!!, R.layout.spinner_row_item, allCountries))

        btn_login_sign_in.setOnClickListener {
            startActivity(Intent(activity!!, PosMainActivity::class.java))
        }
    }

    class ListItem(internal var title: String, internal var imageResourceId: Int)
    inner class MyAdapter(context: Context, textViewResourceId: Int, internal var objects: ArrayList<ListItem>) :
        ArrayAdapter<ListItem>(context, textViewResourceId, objects) {

        internal var inflater: LayoutInflater
        internal var holder: ViewHolder? = null

        init {
            inflater = (context as Activity).layoutInflater
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getCustomView(position, convertView, parent)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getCustomView(position, convertView, parent)
        }

        fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

            val listItem = objects[position]
            var row = convertView

            if (null == row) {
                holder = ViewHolder()
                row = inflater.inflate(R.layout.spinner_row_item, parent, false)
                holder!!.title = row!!.findViewById(R.id.title) as TextView
                holder!!.imgThumb = row.findViewById(R.id.imgThumb) as ImageView
                row.tag = holder
            } else {
                holder = row.tag as ViewHolder
            }

            holder!!.title!!.text = listItem.title
            holder!!.imgThumb!!.setBackgroundResource(listItem.imageResourceId)
//            holder!!.title!!.setTextColor(resources.getColor(R.color.orange))
            return row
        }

        inner class ViewHolder {
            internal var title: TextView? = null
            internal var imgThumb: ImageView? = null
        }
    }

}