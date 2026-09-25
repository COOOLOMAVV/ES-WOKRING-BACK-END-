package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Property

@Entity(tableName = "properties")
data class PropertyEntity(
  @PrimaryKey val id: String,
  val title: String,
  val address: String,
  val cityStateZip: String,
  val price: Long,
  val priceFormatted: String,
  val isRental: Boolean = false,
  val beds: Double,
  val baths: Double,
  val sqft: Int,
  val propertyType: String,
  val description: String,
  val imageResId: Int = 0,
  val mediaUris: List<String> = emptyList(),
  val isFavorite: Boolean = false,
  val status: String = "Active",
  val availableDates: List<String>,
  val availableTimeSlots: List<String>,
  val amenities: List<String>,
  val yearBuilt: Int = 2021,
  val agentName: String = "Sarah Jenkins",
  val agentTitle: String = "Premier Real Estate Broker • Cebu City Properties",
  val agentPhone: String = "+63 917 555 0192",
  val agentEmail: String = "sarah.jenkins@estateflow.com",
  val createdAt: Long = System.currentTimeMillis()
) {
  fun toDomain(): Property {
    return Property(
      id = id,
      title = title,
      address = address,
      cityStateZip = cityStateZip,
      price = price,
      priceFormatted = priceFormatted,
      isRental = isRental,
      beds = beds,
      baths = baths,
      sqft = sqft,
      propertyType = propertyType,
      description = description,
      imageResId = imageResId,
      mediaUris = mediaUris,
      isFavorite = isFavorite,
      status = status,
      availableDates = availableDates,
      availableTimeSlots = availableTimeSlots,
      amenities = amenities,
      yearBuilt = yearBuilt,
      agentName = agentName,
      agentTitle = agentTitle,
      agentPhone = agentPhone,
      agentEmail = agentEmail
    )
  }

  companion object {
    fun fromDomain(prop: Property, createdAt: Long = System.currentTimeMillis()): PropertyEntity {
      return PropertyEntity(
        id = prop.id,
        title = prop.title,
        address = prop.address,
        cityStateZip = prop.cityStateZip,
        price = prop.price,
        priceFormatted = prop.priceFormatted,
        isRental = prop.isRental,
        beds = prop.beds,
        baths = prop.baths,
        sqft = prop.sqft,
        propertyType = prop.propertyType,
        description = prop.description,
        imageResId = prop.imageResId,
        mediaUris = prop.mediaUris,
        isFavorite = prop.isFavorite,
        status = prop.status,
        availableDates = prop.availableDates,
        availableTimeSlots = prop.availableTimeSlots,
        amenities = prop.amenities,
        yearBuilt = prop.yearBuilt,
        agentName = prop.agentName,
        agentTitle = prop.agentTitle,
        agentPhone = prop.agentPhone,
        agentEmail = prop.agentEmail,
        createdAt = createdAt
      )
    }
  }
}
