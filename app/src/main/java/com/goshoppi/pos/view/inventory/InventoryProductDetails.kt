package com.goshoppi.pos.view.inventory

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.goshoppi.pos.R
import com.goshoppi.pos.architecture.repository.VariantRepository
import com.goshoppi.pos.model.Variant
import com.goshoppi.pos.utils.Constants
import com.ishaquehassan.recyclerviewgeneraladapter.RecyclerViewGeneralAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_inventoryproduct_details.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.io.File

class InventoryProductDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventoryproduct_details)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        var variantList: ArrayList<Variant>
        doAsync {
            variantList = VariantRepository.getInstance(this@InventoryProductDetails).getVariantsOfProductsById(
                intent.getStringExtra(Constants.PRODUCT_OBJECT_KEY)
            ) as ArrayList<Variant>
            uiThread {
                Timber.e("Size ${variantList.size}")
                setUpRecyclerView(variantList)
                rc_product_details_variants.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setUpRecyclerView(variantList: ArrayList<Variant>) {


        rc_product_details_variants.layoutManager = LinearLayoutManager(this@InventoryProductDetails)
        rc_product_details_variants.adapter =
            RecyclerViewGeneralAdapter(variantList, R.layout.inventory_product_details_variants_item_view)
            { itemData, viewHolder ->
                val mainView = viewHolder.itemView
                val variantImage = mainView.findViewById<ImageView>(R.id.img_variant_product_details)
                val variantBarcode = mainView.findViewById<TextView>(R.id.tv_variant_barcode)
                val variantPrice = mainView.findViewById<TextView>(R.id.tv_variant_price)
                val variantStock = mainView.findViewById<TextView>(R.id.tv_variant_stock)

                variantBarcode.text = itemData.barCode
                variantPrice.text = itemData.offerPrice
                variantStock.text = itemData.stockBalance

                val root = Environment.getExternalStorageDirectory().toString()
                val file = File("$root/posImages/prd_${itemData.productId}/varaint_images/${itemData.storeRangeId}")

                Timber.e("File is there ? ${file.exists()}")

                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .error(R.drawable.no_image)
                        .into(variantImage)

                } else {
                    Picasso.get()
                        .load(R.drawable.no_image)
                        .into(variantImage)
                }
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
