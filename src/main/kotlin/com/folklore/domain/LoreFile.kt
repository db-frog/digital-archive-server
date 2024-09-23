package com.folklore.domain

data class LoreFile(
    val s3Path: String,
    val text: String,
    val topic: Int?,
    val folder: String?,
    val topicFullName: String?,
    val keyWords: String?,
)