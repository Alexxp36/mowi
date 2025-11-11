package com.mowi.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mowi.data.models.Category
import com.mowi.data.models.Product
import com.mowi.data.repository.AuthRepository
import com.mowi.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load featured products
            productRepository.getFeaturedProducts().fold(
                onSuccess = { products ->
                    _uiState.value = _uiState.value.copy(featuredProducts = products)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )

            // Load categories
            productRepository.getCategories().fold(
                onSuccess = { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )

            // Check if user is logged in
            val isLoggedIn = authRepository.isLoggedIn()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = isLoggedIn,
                isLoading = false
            )
        }
    }

    fun refresh() {
        loadData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
