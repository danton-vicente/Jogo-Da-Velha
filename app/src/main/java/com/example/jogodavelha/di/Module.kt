package com.example.jogodavelha.di

import com.example.jogodavelha.usecase.EasyGameUseCase
import com.example.jogodavelha.usecase.HardGameUseCase
import com.example.jogodavelha.usecase.MediumGameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun provideHardGameUseCase() : HardGameUseCase {
        return HardGameUseCase()
    }

    @Singleton
    @Provides
    fun provideMediumGameUseCase() : MediumGameUseCase {
        return MediumGameUseCase()
    }

    @Singleton
    @Provides
    fun provideEasyGameUseCase() : EasyGameUseCase {
        return EasyGameUseCase()
    }
}