package com.folklore.persistence

import com.folklore.domain.LoreFile
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object LoreFileReader {
    fun readAll() : List<LoreFile> {
        return transaction {
            SchemaUtils.create(File)

            File.selectAll().map { LoreFile(
                s3Path = it[File.s3Path],
                text = it[File.text],
                topic = it[File.topic],
                folder = null,
                topicFullName = null,
                keyWords = null
            ) }
        }
    }

}