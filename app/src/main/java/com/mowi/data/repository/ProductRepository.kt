package com.mowi.data.repository

import com.mowi.data.models.Category
import com.mowi.data.models.Product
import com.mowi.data.remote.MowiApiService
import com.mowi.data.remote.ProductsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val apiService: MowiApiService) {

    suspend fun getProducts(
        category: String? = null,
        search: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): Result<ProductsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProducts(
                category = category,
                search = search,
                page = page,
                limit = limit
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(productId: String): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProductById(productId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeaturedProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFeaturedProducts()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get featured products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCategories()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get categories: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
