package com.goshoppi.pos.ui.inventory

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.R
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_inventoryproduct_details.*

class InventoryProductDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventoryproduct_details)
        setUpRecyclerView()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setUpRecyclerView() {

        val dummyList = arrayListOf<String>()
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")
        dummyList.add("")

        rc_product_details_variants.layoutManager = LinearLayoutManager(this@InventoryProductDetails)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(dummyList, R.layout.inventory_product_details_variants_item_view)
            { itemData, viewHolder ->


            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                this@InventoryProductDetails.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
