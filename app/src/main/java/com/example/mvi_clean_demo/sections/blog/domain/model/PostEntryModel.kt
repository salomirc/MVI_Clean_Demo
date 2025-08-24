package com.example.mvi_clean_demo.sections.blog.domain.model

data class PostEntryModel(
   val id: Int,
   val userId: Int,
   val title: String,
   val body: String,
)