package com.mowi.di

import com.mowi.data.remote.MowiApiService
import com.mowi.data.remote.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMowiApiService(): MowiApiService {
        return RetrofitClient.apiService
    }
}
