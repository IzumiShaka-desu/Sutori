package com.darkshandev.sutori.app.di

import com.darkshandev.sutori.BuildConfig
import com.darkshandev.sutori.app.Config
import com.darkshandev.sutori.data.datasources.SessionService
import com.darkshandev.sutori.data.network.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun provideOkhttpClient(
        interceptor: HttpLoggingInterceptor,
        sessionService: SessionService
    ): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(interceptor)
        .addInterceptor(AuthInterceptor(sessionService))
        .build()

    @Singleton
    @Provides
    fun provideRetrofitClient(
        client: OkHttpClient
    ): Retrofit = Retrofit
        .Builder()
        .baseUrl(Config.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}