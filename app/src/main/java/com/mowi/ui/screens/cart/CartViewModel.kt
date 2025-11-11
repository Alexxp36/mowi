package com.mowi.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mowi.data.models.CartItem
import com.mowi.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val isLoading: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val error: String? = null,
    val checkoutSuccess: Boolean = false
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            cartRepository.getCart().fold(
                onSuccess = { cart ->
                    _uiState.value = _uiState.value.copy(
                        items = cart.items,
                        subtotal = cart.subtotal,
                        tax = cart.tax,
                        total = cart.total,
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

    fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateCartItem(itemId, quantity).fold(
                onSuccess = { cart ->
                    _uiState.value = _uiState.value.copy(
                        items = cart.items,
                        subtotal = cart.subtotal,
                        tax = cart.tax,
                        total = cart.total
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(itemId).fold(
                onSuccess = { cart ->
                    _uiState.value = _uiState.value.copy(
                        items = cart.items,
                        subtotal = cart.subtotal,
                        tax = cart.tax,
                        total = cart.total
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart().fold(
                onSuccess = {
                    loadCart()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )
        }
    }

    fun proceedToCheckout() {
        // This would typically navigate to checkout screen
        // For now, just mark as success
        _uiState.value = _uiState.value.copy(checkoutSuccess = true)
    }

    fun clearCheckoutSuccess() {
        _uiState.value = _uiState.value.copy(checkoutSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
