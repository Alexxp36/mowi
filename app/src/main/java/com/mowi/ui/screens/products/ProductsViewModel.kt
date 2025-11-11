package com.mowi.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mowi.data.models.Product
import com.mowi.data.repository.CartRepository
import com.mowi.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val error: String? = null,
    val addToCartSuccess: Boolean = false
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    fun loadProducts(category: String? = null, search: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedCategory = category,
                searchQuery = search ?: ""
            )

            productRepository.getProducts(
                category = category,
                search = search,
                page = 1
            ).fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        products = response.products,
                        filteredProducts = response.products,
                        currentPage = response.page,
                        hasMorePages = response.page < response.totalPages,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun loadMoreProducts() {
        if (_uiState.value.isLoading || !_uiState.value.hasMorePages) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val nextPage = _uiState.value.currentPage + 1
            productRepository.getProducts(
                category = _uiState.value.selectedCategory,
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                page = nextPage
            ).fold(
                onSuccess = { response ->
                    val updatedProducts = _uiState.value.products + response.products
                    _uiState.value = _uiState.value.copy(
                        products = updatedProducts,
                        filteredProducts = updatedProducts,
                        currentPage = response.page,
                        hasMorePages = response.page < response.totalPages,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun searchProducts(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadProducts(search = query.takeIf { it.isNotBlank() })
    }

    fun filterByCategory(category: String?) {
        loadProducts(category = category)
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            cartRepository.addToCart(productId, quantity).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(addToCartSuccess = true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )
        }
    }

    fun clearAddToCartSuccess() {
        _uiState.value = _uiState.value.copy(addToCartSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
