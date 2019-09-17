package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "store_category"
)
class StoreCategory {

    @SerializedName("category_id")
    @Expose
    @PrimaryKey
    var categoryId: Long? = null
    @SerializedName("category_name")
    @Expose
    var categoryName: String? = null
    @SerializedName("category_image")
    @Expose
    var categoryImage: String? = null
    @SerializedName("categ_tooltip")
    @Expose
    var categTooltip: String? = null
    @SerializedName("sub_categories")
    @Expose
    @Ignore
    var subCategories: List<SubCategory>? = null
   /* constructor() {

    }
    constructor(
        categoryId: Long ,
        categoryName: String,
        categoryImage: String ,
        categTooltip: String
    ){
        this.categoryId = categoryId
        this.categoryImage =categoryImage
        this.categTooltip =categTooltip
        this.categoryName =categoryName

    }*/
}
