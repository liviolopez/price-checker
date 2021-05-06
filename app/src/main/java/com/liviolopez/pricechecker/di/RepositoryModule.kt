package com.liviolopez.pricechecker.di

import com.liviolopez.pricechecker.data.local.AppDataBase
import com.liviolopez.pricechecker.data.local.AppDataStore
import com.liviolopez.pricechecker.data.remote.RemoteDataSource
import com.liviolopez.pricechecker.repository.Repository
import com.liviolopez.pricechecker.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        remoteData: RemoteDataSource,
        localData: AppDataBase,
        appDataStore: AppDataStore
    ): Repository {
        return RepositoryImpl(remoteData, localData, appDataStore)
    }

}