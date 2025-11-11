package com.mowi.data.remote

import com.mowi.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface MowiApiService {

    // Auth endpoints
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<User>

    // Products endpoints
    @GET("products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ProductsResponse>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<Product>

    @GET("products/featured")
    suspend fun getFeaturedProducts(): Response<List<Product>>

    // Categories endpoints
    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    // Cart endpoints
    @GET("cart")
    suspend fun getCart(): Response<CartResponse>

    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<CartResponse>

    @PUT("cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") itemId: String,
        @Body request: UpdateCartItemRequest
    ): Response<CartResponse>

    @DELETE("cart/items/{id}")
    suspend fun removeFromCart(@Path("id") itemId: String): Response<CartResponse>

    @DELETE("cart")
    suspend fun clearCart(): Response<Unit>

    // Orders endpoints
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<Order>

    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: String): Response<Order>

    // Support endpoints
    @GET("faqs")
    suspend fun getFAQs(): Response<List<FAQ>>

    @POST("support/tickets")
    suspend fun createSupportTicket(@Body request: SupportTicketRequest): Response<SupportTicket>
}

// Request models
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AddToCartRequest(
    val productId: String,
    val quantity: Int = 1
)

data class UpdateCartItemRequest(
    val quantity: Int
)

data class CreateOrderRequest(
    val items: List<OrderItem>,
    val shippingAddress: Address,
    val paymentMethod: String
)

data class SupportTicketRequest(
    val subject: String,
    val message: String,
    val category: String
)

// Response models
data class AuthResponse(
    val token: String,
    val user: User
)

data class ProductsResponse(
    val products: List<Product>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)

data class CartResponse(
    val items: List<CartItem>,
    val subtotal: Double,
    val tax: Double,
    val total: Double
)

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val tax: Double,
    val total: Double,
    val status: OrderStatus,
    val shippingAddress: Address,
    val paymentMethod: String,
    val createdAt: String,
    val updatedAt: String
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val productImage: String,
    val quantity: Int,
    val price: Double,
    val total: Double
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String
)

data class SupportTicket(
    val id: String,
    val subject: String,
    val message: String,
    val category: String,
    val status: String,
    val createdAt: String
)
