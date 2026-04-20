package com.kaelmoreno.compose.composemultiplatformbase.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val username: String?,
    val email: String?,
    val phone: String?,
    val website: String?,
    val street: String?,
    val suite: String?,
    val city: String?,
    val zipcode: String?,
    val lat: String?,
    val lng: String?,
    val companyName: String?,
    val companyCatchPhrase: String?,
    val companyBs: String?
)
