package com.kaelmoreno.compose.composemultiplatformbase.data.repository

import com.kaelmoreno.compose.composemultiplatformbase.data.mapper.UserMapper
import com.kaelmoreno.compose.composemultiplatformbase.data.network.ResponseHandler
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserRepositoryTest {

    private val mapper = UserMapper()

    @Test
    fun userMapper_toDomain_and_toEntity_roundtrips() {
        val user = User(
            id = 1,
            name = "Test User",
            username = "testuser",
            email = "test@example.com",
            phone = "+1234567890",
            website = "test.com",
            address = null,
            company = null
        )

        val entity = mapper.toEntity(user)
        val result = mapper.toDomain(entity)

        assertEquals(user.id, result.id)
        assertEquals(user.name, result.name)
        assertEquals(user.username, result.username)
        assertEquals(user.email, result.email)
        assertEquals(user.phone, result.phone)
    }

    @Test
    fun userMapper_handles_list_conversion() {
        val users = listOf(
            User(id = 1, name = "User 1", username = null, email = null, phone = null, website = null, address = null, company = null),
            User(id = 2, name = "User 2", username = null, email = null, phone = null, website = null, address = null, company = null)
        )

        val entities = mapper.toEntities(users)
        assertEquals(2, entities.size)

        val domain = mapper.toDomain(entities)
        assertEquals(2, domain.size)
        assertEquals("User 1", domain[0].name)
        assertEquals("User 2", domain[1].name)
    }

    @Test
    fun responseHandler_success_contains_data() {
        val users = listOf(
            User(id = 1, name = "Test", username = null, email = null, phone = null, website = null, address = null, company = null)
        )
        val response = ResponseHandler.Success(users)
        assertNotNull(response.result)
        assertEquals(1, response.result?.size)
    }
}
