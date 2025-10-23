package com.example.campmate.di

import com.example.campmate.data.remote.ApiService
import com.example.campmate.data.remote.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor // 2. 우리가 만든 '인증 검문소'를 주입받습니다.
    ): OkHttpClient {
        // Logcat에 통신 내용을 자세히 보여주는 loggingInterceptor를 생성합니다.
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)     // 3. 모든 요청에 '인증 검문소'를 통과하도록 설정합니다.
            .addInterceptor(loggingInterceptor) // 4. 모든 통신 내용을 로그로 찍도록 설정합니다.
            .build()
    }

    /**
     * Retrofit 인스턴스를 제공합니다.
     * 위에서 만든 OkHttpClient를 사용하여 통신합니다.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient // 1. 토큰 기능이 추가된 OkHttpClient를 주입받습니다.
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // 5. Retrofit이 우리가 설정한 클라이언트를 사용하도록 합니다.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * ApiService 인터페이스의 구현체를 제공합니다.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}