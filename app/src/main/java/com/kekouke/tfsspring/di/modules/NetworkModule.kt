package com.kekouke.tfsspring.di.modules

import com.google.gson.GsonBuilder
import com.kekouke.tfsspring.data.api.services.ChatApiService
import com.kekouke.tfsspring.data.api.services.StreamsApiService
import com.kekouke.tfsspring.data.api.services.TopicsApiService
import com.kekouke.tfsspring.data.api.services.UsersApiService
import com.kekouke.tfsspring.data.api.response.events.EventResponseBase
import com.kekouke.tfsspring.data.api.response.events.EventDtoTypeAdapter
import com.kekouke.tfsspring.data.api.services.LongPollingApiService
import dagger.Module
import dagger.Provides
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
open class NetworkModule {

    protected open fun baseUrl() = BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder().apply {
                registerTypeAdapter(EventResponseBase::class.java, EventDtoTypeAdapter())
            }.create()
        ))
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.header("Authorization", authorization)
            chain.proceed(requestBuilder.build())
        }
        .build()

    @Provides
    @Singleton
    fun provideUsersApiService(retrofit: Retrofit): UsersApiService =
        retrofit.create(UsersApiService::class.java)

    @Provides
    @Singleton
    fun provideStreamsApiService(retrofit: Retrofit): StreamsApiService =
        retrofit.create(StreamsApiService::class.java)

    @Provides
    @Singleton
    fun provideTopicsApiService(retrofit: Retrofit): TopicsApiService =
        retrofit.create(TopicsApiService::class.java)

    @Provides
    @Singleton
    fun provideChatApiService(retrofit: Retrofit): ChatApiService =
        retrofit.create(ChatApiService::class.java)

    @Provides
    @Singleton
    fun provideLongPollingApiService(retrofit: Retrofit): LongPollingApiService =
        retrofit.create(LongPollingApiService::class.java)

    companion object {

        private const val BASE_URL = "https://tinkoff-android-spring-2024.zulipchat.com/api/v1/"

        private val authorization = Credentials.basic(
            "mikhailkozitskii@gmail.com",
            "ZiYJOgZaJWPvrxiIWfEm64Ad9shpn4bF"
        )
    }
}