package com.kaelmoreno.compose.composemultiplatformbase.data.mapper

import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.entity.UserEntity
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.Address
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.Company
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.Geo
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User

class UserMapper {

    fun toEntities(users: List<User>): List<UserEntity> = users.map { toEntity(it) }

    fun toDomain(entities: List<UserEntity>): List<User> = entities.map { toDomain(it) }

    fun toEntity(user: User): UserEntity = UserEntity(
        id = user.id,
        name = user.name,
        username = user.username,
        email = user.email,
        phone = user.phone,
        website = user.website,
        street = user.address?.street,
        suite = user.address?.suite,
        city = user.address?.city,
        zipcode = user.address?.zipcode,
        lat = user.address?.geo?.lat,
        lng = user.address?.geo?.lng,
        companyName = user.company?.name,
        companyCatchPhrase = user.company?.catchPhrase,
        companyBs = user.company?.bs
    )

    fun toDomain(entity: UserEntity): User = User(
        id = entity.id,
        name = entity.name,
        username = entity.username,
        email = entity.email,
        phone = entity.phone,
        website = entity.website,
        address = if (entity.street != null) {
            Address(
                street = entity.street,
                suite = entity.suite ?: "",
                city = entity.city ?: "",
                zipcode = entity.zipcode ?: "",
                geo = Geo(
                    lat = entity.lat ?: "0",
                    lng = entity.lng ?: "0"
                )
            )
        } else null,
        company = if (entity.companyName != null) {
            Company(
                name = entity.companyName,
                catchPhrase = entity.companyCatchPhrase ?: "",
                bs = entity.companyBs ?: ""
            )
        } else null
    )
}
