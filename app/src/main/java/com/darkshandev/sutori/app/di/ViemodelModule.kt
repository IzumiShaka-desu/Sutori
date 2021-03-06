package com.darkshandev.sutori.app.di

import com.darkshandev.sutori.data.repositories.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface ViewModelModule {
    @Binds
    fun provideStoryRepositoryImpl(repo: StoryRepositoryImpl): StoryRepository

    @Binds
    fun provideUserRepositoryImpl(repo: UserRepositoryImpl): UserRepository

    @Binds
    fun provideLocationRepositoryImpl(repository: LocationRepositoryImpl): LocationRepository
}
