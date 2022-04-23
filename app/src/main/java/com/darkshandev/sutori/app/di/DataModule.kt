package com.darkshandev.sutori.app.di

import com.darkshandev.sutori.data.datasources.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun provideStoryRemoteDatasourceImpl(datasource: RemoteStoryDatasourceImpl): RemoteStoryDatasource

    @Binds
    fun provideUserRemoteDatasourceImpl(datasource: RemoteUserDataSourcesImpl): RemoteUserDataSources

    @Binds
    fun provideSharedLocationManagerImpl(manager: SharedLocationManagerImpl): SharedLocationManager

    @Binds
    fun provideSessionServiceImpl(sessionService: SessionServiceImpl): SessionService
}