package com.liviolopez.pricechecker.di

import android.app.Application
import com.liviolopez.pricechecker.utils.InputValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Singleton
    @Provides
    fun provideInputValidator(app: Application) = InputValidator(app)

}