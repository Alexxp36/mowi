package com.mowi.data.repository

import com.mowi.data.models.User
import com.mowi.data.remote.LoginRequest
import com.mowi.data.remote.MowiApiService
import com.mowi.data.remote.RegisterRequest
import com.mowi.data.remote.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val apiService: MowiApiService) {

    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String? = null
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(
                RegisterRequest(
                    email = email,
                    password = password,
                    name = name,
                    phone = phone
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                TokenManager.setToken(authResponse.token)
                Result.success(authResponse.user)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                TokenManager.setToken(authResponse.token)
                Result.success(authResponse.user)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.logout()
            TokenManager.clearToken()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            TokenManager.clearToken()
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentUser()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean {
        return TokenManager.getToken() != null
    }
}
