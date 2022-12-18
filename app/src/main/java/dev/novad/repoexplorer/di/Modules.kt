package dev.novad.repoexplorer.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dev.novad.repoexplorer.DataRepositoryImpl
import dev.novad.repoexplorer.network.ApiService
import dev.novad.repoexplorer.repository.DataRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
abstract class Bindings {
    @Binds
    abstract fun bindRepository(repositoryImpl: DataRepositoryImpl): DataRepository
}

@Module
@InstallIn(ViewModelComponent::class)
object DataRepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideRepositoryImpl(): DataRepositoryImpl {
        return DataRepositoryImpl(
            Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        )
    }
}

