package com.mowi.data.repository

import com.mowi.data.models.FAQ
import com.mowi.data.remote.MowiApiService
import com.mowi.data.remote.SupportTicket
import com.mowi.data.remote.SupportTicketRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupportRepository(private val apiService: MowiApiService) {

    suspend fun getFAQs(): Result<List<FAQ>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFAQs()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get FAQs: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSupportTicket(
        subject: String,
        message: String,
        category: String
    ): Result<SupportTicket> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createSupportTicket(
                SupportTicketRequest(
                    subject = subject,
                    message = message,
                    category = category
                )
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create support ticket: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
