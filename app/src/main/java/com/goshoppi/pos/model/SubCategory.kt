package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
@Entity(
    tableName = "store_subcategory"
)
class SubCategory(
    subcategoryId: Long,
    categoryId: Long,
    subcategoryName: String,
    subcategoryImage: String,
    subcategTooltip: String
) {

    @SerializedName("subcategory_id")
    @Expose
    @PrimaryKey
        var subcategoryId: Long? = subcategoryId
    @SerializedName("subcategory_name")
    @Expose
    var subcategoryName: String? = subcategoryName
    @SerializedName("subcategory_image")
    @Expose
    var subcategoryImage: String? = subcategoryImage
    @SerializedName("subcateg_tooltip")
    @Expose
    var subcategTooltip: String? = subcategTooltip
    var categoryId: Long? = categoryId


}
