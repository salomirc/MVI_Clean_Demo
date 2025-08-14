package com.example.mvi_clean_demo.sections.blog.domain.model

data class PostEntry(
   val id: Int,
   val userId: Int,
   val title: String,
   val body: String,
)