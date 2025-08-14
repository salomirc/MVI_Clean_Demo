package com.example.mvi_clean_demo.di

import com.example.mvi_clean_demo.sections.blog.data.network.api.BlogApi
import com.example.mvi_clean_demo.sections.blog.data.network.repository.BlogRepository
import com.example.mvi_clean_demo.sections.blog.data.network.repository.IBlogRepository
import com.example.mvi_clean_demo.sections.blog.domain.useCase.GetUsersUseCase
import com.example.mvi_clean_demo.sections.blog.domain.useCase.IGetUsersUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
interface BlogModule {

    @ViewModelScoped
    @Binds
    fun bindIBlogRepository(impl: BlogRepository): IBlogRepository

    @Binds
    fun bindIGetUsersUseCase(impl: GetUsersUseCase): IGetUsersUseCase

    companion object {
        @ViewModelScoped
        @Provides
        fun provideBlogApi(retrofit: Retrofit): BlogApi =
            retrofit.create(BlogApi::class.java)
    }

}