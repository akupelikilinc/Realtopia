package com.realtopia.game.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.realtopia.game.data.model.Property

@Dao
interface PropertyDao {
    
    @Query("SELECT * FROM properties ORDER BY gridX, gridY")
    fun getAllProperties(): Flow<List<Property>>
    
    @Query("SELECT * FROM properties WHERE isOwned = 1")
    fun getOwnedProperties(): Flow<List<Property>>
    
    @Query("SELECT * FROM properties WHERE isOwned = 0")
    fun getAvailableProperties(): Flow<List<Property>>
    
    @Query("SELECT * FROM properties WHERE type = :type")
    fun getPropertiesByType(type: Property.PropertyType): Flow<List<Property>>
    
    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyById(id: String): Property?
    
    @Query("SELECT * FROM properties WHERE gridX = :x AND gridY = :y")
    suspend fun getPropertyByPosition(x: Int, y: Int): Property?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: Property)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<Property>)
    
    @Update
    suspend fun updateProperty(property: Property)
    
    @Query("UPDATE properties SET currentPrice = :newPrice, priceChange = :priceChange, priceChangePercentage = :priceChangePercentage WHERE id = :id")
    suspend fun updatePropertyPrice(id: String, newPrice: Double, priceChange: Double, priceChangePercentage: Double)
    
    @Query("UPDATE properties SET isOwned = 1, purchaseDate = :purchaseDate WHERE id = :id")
    suspend fun buyProperty(id: String, purchaseDate: Long)
    
    @Query("UPDATE properties SET isOwned = 0, purchaseDate = NULL WHERE id = :id")
    suspend fun sellProperty(id: String)
    
    @Query("SELECT COUNT(*) FROM properties WHERE isOwned = 1")
    suspend fun getOwnedPropertyCount(): Int
    
    @Query("SELECT SUM(currentPrice) FROM properties WHERE isOwned = 1")
    suspend fun getTotalPortfolioValue(): Double?
    
    @Query("SELECT SUM(currentPrice - price) FROM properties WHERE isOwned = 1 AND purchaseDate IS NOT NULL")
    suspend fun getTotalProfit(): Double?
    
    @Query("DELETE FROM properties")
    suspend fun deleteAllProperties()
    
    @Delete
    suspend fun deleteProperty(property: Property)
}
