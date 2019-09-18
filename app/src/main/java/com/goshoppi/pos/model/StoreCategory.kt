package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "store_category"
)
class StoreCategory(
    categoryId: Long,
    categoryName: String,
    categoryImage: String,
    categTooltip: String
) {

    @SerializedName("category_id")
    @Expose
    @PrimaryKey
    var categoryId: Long? = categoryId
    @SerializedName("category_name")
    @Expose
    var categoryName: String? = categoryName
    @SerializedName("category_image")
    @Expose
    var categoryImage: String? = categoryImage
    @SerializedName("categ_tooltip")
    @Expose
    var categTooltip: String? = categTooltip
    @SerializedName("sub_categories")
    @Expose
    @Ignore
    var subCategories: List<SubCategory>? = null

}
