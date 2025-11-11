package com.mowi.data.repository

import com.mowi.data.remote.AddToCartRequest
import com.mowi.data.remote.CartResponse
import com.mowi.data.remote.MowiApiService
import com.mowi.data.remote.UpdateCartItemRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(private val apiService: MowiApiService) {

    suspend fun getCart(): Result<CartResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCart()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get cart: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToCart(productId: String, quantity: Int = 1): Result<CartResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addToCart(
                AddToCartRequest(
                    productId = productId,
                    quantity = quantity
                )
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to add to cart: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCartItem(itemId: String, quantity: Int): Result<CartResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateCartItem(
                itemId = itemId,
                request = UpdateCartItemRequest(quantity = quantity)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update cart item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(itemId: String): Result<CartResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.removeFromCart(itemId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to remove from cart: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.clearCart()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to clear cart: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
