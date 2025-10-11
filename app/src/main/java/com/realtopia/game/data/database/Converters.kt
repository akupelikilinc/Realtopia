package com.realtopia.game.data.database

import androidx.room.TypeConverter
import com.realtopia.game.data.model.Property

class Converters {
    
    @TypeConverter
    fun fromPropertyType(propertyType: Property.PropertyType): String {
        return propertyType.name
    }
    
    @TypeConverter
    fun toPropertyType(propertyType: String): Property.PropertyType {
        return Property.PropertyType.valueOf(propertyType)
    }
    
    @TypeConverter
    fun fromPropertyTypeList(propertyTypes: List<Property.PropertyType>): String {
        return propertyTypes.joinToString(",") { it.name }
    }
    
    @TypeConverter
    fun toPropertyTypeList(propertyTypes: String): List<Property.PropertyType> {
        return if (propertyTypes.isEmpty()) {
            emptyList()
        } else {
            propertyTypes.split(",").map { Property.PropertyType.valueOf(it) }
        }
    }
}
