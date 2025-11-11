package com.mowi.ui.screens.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mowi.data.models.FAQ
import com.mowi.data.repository.SupportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SupportUiState(
    val isLoading: Boolean = false,
    val faqs: List<FAQ> = emptyList(),
    val expandedFaqId: String? = null,
    val error: String? = null,
    val ticketSubmitSuccess: Boolean = false
)

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val supportRepository: SupportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()

    init {
        loadFAQs()
    }

    private fun loadFAQs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            supportRepository.getFAQs().fold(
                onSuccess = { faqs ->
                    _uiState.value = _uiState.value.copy(
                        faqs = faqs,
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

    fun toggleFaq(faqId: String) {
        _uiState.value = _uiState.value.copy(
            expandedFaqId = if (_uiState.value.expandedFaqId == faqId) null else faqId
        )
    }

    fun submitTicket(subject: String, message: String, category: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            supportRepository.createSupportTicket(subject, message, category).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        ticketSubmitSuccess = true,
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

    fun clearTicketSubmitSuccess() {
        _uiState.value = _uiState.value.copy(ticketSubmitSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refresh() {
        loadFAQs()
    }
}
