package com.mowi.di

import com.mowi.data.remote.MowiApiService
import com.mowi.data.repository.AuthRepository
import com.mowi.data.repository.CartRepository
import com.mowi.data.repository.ProductRepository
import com.mowi.data.repository.SupportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: MowiApiService): AuthRepository {
        return AuthRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideProductRepository(apiService: MowiApiService): ProductRepository {
        return ProductRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideCartRepository(apiService: MowiApiService): CartRepository {
        return CartRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideSupportRepository(apiService: MowiApiService): SupportRepository {
        return SupportRepository(apiService)
    }
}
